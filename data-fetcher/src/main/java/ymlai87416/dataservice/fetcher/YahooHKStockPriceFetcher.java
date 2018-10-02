package ymlai87416.dataservice.fetcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;
import ymlai87416.dataservice.utilities.Utilities;
import ymlai87416.dataservice.domain.DailyPrice;
import ymlai87416.dataservice.domain.Symbol;
import ymlai87416.dataservice.fetcher.constant.DataVendors;
import ymlai87416.dataservice.fetcher.constant.Exchanges;
import ymlai87416.dataservice.fetcher.constant.Instruments;
import ymlai87416.dataservice.service.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Tom on 7/10/2016.
 */
@Component("YahooHKStockPriceFetcher")
public class YahooHKStockPriceFetcher implements Fetcher{

    private Logger log = LoggerFactory.getLogger(YahooHKStockPriceFetcher.class);

    private java.sql.Date startDate = null;
    private java.sql.Date endDate  = null;
    private static int MaximumRetryCount = 4;

    @Autowired
    ExchangeService exchangeService;

    @Autowired
    DailyPriceService dailyPriceService;

    @Autowired
    DataVendorService dataVendorService;

    @Autowired
    SymbolService symbolService;

    @Autowired
    MasterBackup masterBackup;

    @Override
    public boolean run(Map<String, Object> parameter) {

        System.setProperty("yahoofinance.baseurl.histquotes", "https://ichart.yahoo.com/table.csv");
        try{
            log.info("Downloading stock quote from Yahoo!");
            updateStartAndEndDate();

            File masterDir = initMasterBackup();
            boolean isCached = checkCompleteMark(masterDir);

            Symbol searchCriteria = new Symbol();
            searchCriteria.setInstrument(Instruments.STOCK);
            searchCriteria.setExchange(getOrSaveExchange(exchangeService, Exchanges.HKExchange));

            List<Symbol> allSymbols = symbolService.searchSymbol(searchCriteria, false);
            log.info("Got symbol list from database.");
            List<Symbol> redownloadDueToAdjustment = firstRoundDownloadDailyPrice(allSymbols);
            log.info("Pass 1 data fetching finished.");

            boolean allCompleted = false;
            int retryCount = 0;
            while(true){
                ++retryCount;
                if(retryCount > MaximumRetryCount)
                    break;

                try {
                    redownloadDueToAdjustment = retryDownloadDailyPrice(redownloadDueToAdjustment);
                    if(redownloadDueToAdjustment == null || redownloadDueToAdjustment.size() == 0) {
                        allCompleted = true;
                        break;
                    }
                }
                catch(Exception ex){
                    ex.printStackTrace();
                    log.error("Pass" + retryCount+ 1 +" encouter an exception.", ex);
                }
                log.info("Pass" + retryCount+ 1 +" data fetching finished.");
            }


            if(!isCached && allCompleted)
                createCompleteMark(masterDir);

            log.info("Complete downloading stock quote from Yahoo!");
            return true;
        }
        catch(Exception ex){
            log.error("Exception occurred when downloading stock quote from Yahoo!", ex);
            return false;
        }
    }

    private void updateStartAndEndDate(){
        startDate = new java.sql.Date(2000-1900, 1, 1);
        endDate  = Utilities.getCurrentSQLDate();
    }

    private File initMasterBackup(){
        log.info("Create master backup directory for current data fetch round");
        File previousFolder = masterBackup.retrievedLatestBatchFolder(this.getClass());

        boolean readExistingFolder = true;
        if(previousFolder == null){
            readExistingFolder = false;
        }
        else{
            if(!checkCompleteMark(previousFolder))
                readExistingFolder = false;
        }

        if(readExistingFolder)
            return masterBackup.retrievedLatestBatchFolder(this.getClass());
        else {
            File newFolder = masterBackup.getCurrentBatchFolder(this.getClass());
            return newFolder;
        }
    }

    private DateRange decideDailyPriceDownloadDateRange(Symbol symbol){
        java.sql.Date latestDate = dailyPriceService.getLastestDailyPriceDateForSymbol(symbol);
        if(latestDate == null)
            return new DateRange(startDate, endDate);
        else if(latestDate.compareTo(endDate) == 0)
            return null;
        else{
            return new DateRange(latestDate, endDate);
        }
    }

    //TODO: Reduce memory usage
    private List<Symbol> firstRoundDownloadDailyPrice(List<Symbol> allSymbols){
        List<Symbol> failedSymbol = new ArrayList<Symbol>();

        for(Symbol symbol : allSymbols){
            log.info("First round: downloading symbol " + symbol.getName() );
            try{
                boolean skipSave = false;
                DateRange dateRange = decideDailyPriceDownloadDateRange(symbol);

                if(dateRange == null) continue;

                //download symbol
                List<DailyPrice> dailyPrices = getDailyPriceFromYahoo(symbol, dateRange);

                //check for adjustment and dividend
                List<DailyPrice> lastTradingPriceSearchResult = dailyPriceService.getDailyPriceBySymbolAndDateRange(symbol, dateRange.startDate, dateRange.startDate);

                if(lastTradingPriceSearchResult != null && lastTradingPriceSearchResult.size() > 0){
                    DailyPrice lastTradingDayPriceDB = lastTradingPriceSearchResult.get(0);
                    DailyPrice lastTradingDayPriceNet = dailyPrices.get(0);

                    if(lastTradingDayPriceDB.getPriceDate().compareTo(lastTradingDayPriceNet.getPriceDate()) != 0){
                        failedSymbol.add(symbol); //I should not be happening, we are missing points.
                        dailyPriceService.deleteDailyPriceBySymbol(symbol);
                        skipSave = true;
                    }
                    else{
                        double adjRatioDB = lastTradingDayPriceDB.getClosePrice() / lastTradingDayPriceDB.getAdjClosePrice();
                        double adjRatioNet = lastTradingDayPriceNet.getClosePrice() / lastTradingDayPriceNet.getAdjClosePrice();
                        double deviate = adjRatioDB / adjRatioNet;
                        if(deviate <= 0.9 || deviate >= 1.1) {
                            failedSymbol.add(symbol);
                            dailyPriceService.deleteDailyPriceBySymbol(symbol);
                            skipSave = true;
                        }
                    }

                    dailyPrices.remove(0);
                    if(dailyPrices.size() == 0)
                        skipSave = true;
                }

                //save symbol
                if(!skipSave)
                    dailyPriceService.saveDailyPriceInBatch(dailyPrices);

                log.info("First round: completed downloading symbol " + symbol.getName() );
            }
            catch(Exception ex){
                ex.printStackTrace();
                log.error("Failed to retrieve info for the symbol " + symbol.getName() , ex);
                failedSymbol.add(symbol);
            }
        }

        return failedSymbol;
    }

    private List<Symbol> retryDownloadDailyPrice(List<Symbol> redownloadDueToAdjustment){
        List<Symbol> failedSymbol = new ArrayList<Symbol>();
        for(Symbol symbol: redownloadDueToAdjustment){
            log.info("Retry round: downloading symbol " + symbol.getName() );
            try {
                DateRange dateRange = decideDailyPriceDownloadDateRange(symbol);

                if(dateRange == null) continue;

                //download symbol
                List<DailyPrice> dailyPrices = getDailyPriceFromYahoo(symbol, dateRange);

                //save symbol
                dailyPriceService.saveDailyPriceInBatch(dailyPrices);
                log.info("Retry round: completed downloading symbol " + symbol.getName() );
            }
            catch(Exception ex){
                ex.printStackTrace();
                log.error("Failed to retrieve info for the symbol " + symbol.getName() , ex);
                failedSymbol.add(symbol);
            }
        }

        return failedSymbol;
    }

    private List<DailyPrice> getDailyPriceFromYahoo(Symbol symbol, DateRange dateRange) throws IOException {
        log.info("Getting daily price from Yahoo! Finance for symbol:  " + symbol.getName() +
                " from " + dateRange.startDate.toString() + " to " + dateRange.endDate.toString());

        Stock stock = YahooFinance.get(symbol.getTicker());
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(dateRange.startDate);
        Calendar endDate = Calendar.getInstance();
        endDate.setTime(dateRange.endDate);

        List<HistoricalQuote> HistQuotes = stock.getHistory(startDate, endDate, Interval.DAILY);

        List<DailyPrice> dailyPrices = new ArrayList<DailyPrice>();
        for(HistoricalQuote quote: HistQuotes){
            DailyPrice dailyPrice = new DailyPrice();
            dailyPrice.setDataVendor(getOrSaveDataVendor(dataVendorService, DataVendors.YahooDataVendor));
            dailyPrice.setSymbol(symbol);
            //check I want date part only
            dailyPrice.setPriceDate(Utilities.convertUtilDateToSqlDate(quote.getDate().getTime()));
            dailyPrice.setCreatedDate(Utilities.getCurrentSQLDateTime());
            dailyPrice.setLastUpdatedDate(Utilities.getCurrentSQLDateTime());

            //handle garbage
            if(quote.getOpen() == null || quote.getHigh() == null ||
                    quote.getLow() == null || quote.getClose() == null || quote.getAdjClose() == null)
                continue;

            dailyPrice.setOpenPrice(quote.getOpen().doubleValue());
            dailyPrice.setHighPrice(quote.getHigh().doubleValue());
            dailyPrice.setLowPrice(quote.getLow().doubleValue());
            dailyPrice.setClosePrice(quote.getClose().doubleValue());
            dailyPrice.setAdjClosePrice(quote.getAdjClose().doubleValue());
            dailyPrice.setVolume(quote.getVolume());

            dailyPrices.add(dailyPrice);
        }


        //this list has to be sorted in ascending order
        Collections.sort(dailyPrices, new Comparator<DailyPrice>(){

            @Override
            public int compare(DailyPrice o1, DailyPrice o2) {
                return o1.getPriceDate().compareTo(o2.getPriceDate());
            }
        });

        log.info("Completed getting daily price from Yahoo! Finance for symbol:  " + symbol.getName() +
                " from " + dateRange.startDate.toString() + " to " + dateRange.endDate.toString());

        return dailyPrices;
    }

    private boolean checkCompleteMark(File file){
        try {
            String completeMarkPath = file.getAbsolutePath() + File.separator + ".complete";
            File completeMark = new File(completeMarkPath);

            return completeMark.exists();
        }
        catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    private void createCompleteMark(File file){
        try {
            String completeMarkPath = file.getAbsolutePath() + File.separator + ".complete";
            File completeMark = new File(completeMarkPath);

            completeMark.createNewFile();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }


    class DateRange{
        public DateRange(){

        }

        public DateRange(java.sql.Date startDate, java.sql.Date endDate){
            this.startDate = startDate;
            this.endDate = endDate;
        }

        java.sql.Date startDate;
        java.sql.Date endDate;
    }

    public void saveRetrievedResult(){

    }

    public void getCachedResult(){

    }
}
