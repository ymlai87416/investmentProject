package ymlai87416.dataservice.fetcher;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ymlai87416.dataservice.Utilities.Utilities;
import ymlai87416.dataservice.domain.DailyPrice;
import ymlai87416.dataservice.domain.DataVendor;
import ymlai87416.dataservice.domain.Exchange;
import ymlai87416.dataservice.domain.Symbol;
import ymlai87416.dataservice.exception.ParseException;
import ymlai87416.dataservice.fetcher.datavendor.DataVendors;
import ymlai87416.dataservice.fetcher.exchange.Exchanges;
import ymlai87416.dataservice.service.DailyPriceService;
import ymlai87416.dataservice.service.DataVendorService;
import ymlai87416.dataservice.service.ExchangeService;
import ymlai87416.dataservice.service.SymbolService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by Tom on 6/10/2016.
 */
@Component
public class HKExStockOptionPriceFetcher implements Fetcher{
    private Log log = LogFactory.getLog(HKExStockOptionPriceFetcher.class);

    static final String url = "http://www.hkex.com.hk/chi/ddp/most_active_contracts_c.asp?marketid=4";
    static final SimpleDateFormat parser=new SimpleDateFormat("dd/MM/yyyy hh:mm zzz");

    @Autowired
    ExchangeService exchangeService;

    @Autowired
    DataVendorService dataVendorService;

    @Autowired
    SymbolService symbolService;

    @Autowired
    DailyPriceService dailyPriceService;

    @Override
    public synchronized boolean run(){
        try{
            Document doc = Jsoup.connect(url).get();
            java.sql.Date priceDate = parsePriceDate(doc);
            Exchange exchange = getOrSaveExchange(exchangeService, Exchanges.HKExchange);
            DataVendor dataVendor = getOrSaveDataVendor(dataVendorService, DataVendors.HKExDataVendor);

            if(priceDate == null)
                log.error(String.format("Cannot retrieve price data from url: %s", url));
            else{
                log.info(String.format("Start downloading price data for date: %s ", parser.format(priceDate)));
                ArrayList<PriceTimeSequence> resultList = parsePricePage(doc, exchange, dataVendor, priceDate);

                log.info(String.format("Saving original copy to backup directory for date: %s ", parser.format(priceDate)));
                savePriceTimeSequenceToDatabase(exchange, resultList, priceDate);

                log.info(String.format("Complete price downloading process for date: %s ", parser.format(priceDate)));
            }
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
        catch(ParseException ex){
            ex.printStackTrace();
        }

        return true;
    }

    private ArrayList<PriceTimeSequence> parsePricePage(Document doc, Exchange exchange, DataVendor dataVendor, java.sql.Date priceDate) throws IOException, ParseException{
        ArrayList<PriceTimeSequence> timeSequenceList = new ArrayList<>();
        Elements stockOptionList1 = doc.select("tr.tableHdrB2");
        Elements stockOptionList2 = doc.select("tr.tableHdrB1");

        Elements stockOptionList = new Elements();
        stockOptionList.addAll(stockOptionList1);
        stockOptionList.addAll(stockOptionList2);

        for(Element stockOption : stockOptionList){
            String contract1 = stockOption.child(0).text();
            String contract2 = stockOption.child(1).text();

            String contract = String.format("%s %s", contract1, contract2);

            Symbol symbol;
            symbol = new Symbol();
            symbol.setExchange(exchange);
            symbol.setTicker(contract);
            symbol.setInstrument("Stock Option");
            symbol.setName(contract);
            symbol.setSector(null);
            symbol.setLot(null);
            symbol.setCurrency("HKD");
            symbol.setCreatedDate(Utilities.getCurrentSQLDate());
            symbol.setLastUpdatedDate(Utilities.getCurrentSQLDate());

            //get the symbol from db first or save it first.

            Double opening, close, high, low;
            Long volume;

            opening = tryDoubleParse(stockOption.child(2).text());
            close = tryDoubleParse(stockOption.child(5).text());
            high = tryDoubleParse(stockOption.child(6).text());
            low = tryDoubleParse(stockOption.child(7).text());
            volume = tryLongParse(stockOption.child(8).text());

            DailyPrice price;

            price = new DailyPrice();
            price.setDataVendor(dataVendor);
            price.setPriceDate(Utilities.getCurrentSQLDate());
            price.setOpenPrice(opening);
            price.setClosePrice(close);
            price.setHighPrice(high);
            price.setLowPrice(low);
            price.setVolume(volume);
            price.setSymbol(symbol);
            price.setCreatedDate(Utilities.getCurrentSQLDate());
            price.setLastUpdatedDate(Utilities.getCurrentSQLDate());

            timeSequenceList.add(new PriceTimeSequence(symbol, price));
        }

        return timeSequenceList;
    }

    private java.sql.Date parsePriceDate(Document doc){
        Elements priceMetaInfo = doc.select("td.verd_black11");
        String resultStr = null;
        for(Element element : priceMetaInfo){
            String info = element.text();

            if(info.startsWith("最後更新:")){
                resultStr = info.replace("最後更新:", "").trim();
            }
        }

        java.util.Date parseResult;
        if(resultStr != null)
            try {
                parseResult = parser.parse(resultStr);
            }
            catch(Exception ex){
                parseResult = null;
            }
        else
            parseResult = null;

        if(parseResult == null)
            return null;
        else
            return new java.sql.Date(parseResult.getTime());
    }

    private void savePriceTimeSequenceToDatabase(Exchange exchange, ArrayList<PriceTimeSequence> resultList, java.sql.Date priceDate){
        Symbol symbolSearchCriteria = new Symbol();
        symbolSearchCriteria.setExchange(exchange);
        symbolSearchCriteria.setInstrument("Stock Option");
        symbolSearchCriteria.setCurrency("HKD");
        List<Symbol> allStockOptionSymbolList = symbolService.searchSymbol(symbolSearchCriteria);
        List<Symbol> newSymbolForSave = new ArrayList<Symbol>();
        TreeMap<String, Symbol> symbolLookup = new TreeMap<String, Symbol>();

        for(Symbol symbol: allStockOptionSymbolList)
            symbolLookup.put(symbol.getTicker(), symbol);

        for(PriceTimeSequence timeseq : resultList){
            Symbol symbol = timeseq.symbol;
            if(symbolLookup.get(symbol.getTicker()) == null)
                newSymbolForSave.add(symbol);
        }

        List<Symbol> savedSymbolList = symbolService.saveSymbolInBatch(newSymbolForSave);
        for(Symbol symbol: savedSymbolList)
            symbolLookup.put(symbol.getTicker(), symbol);

        allStockOptionSymbolList = new ArrayList(symbolLookup.values());
        List<DailyPrice> allDailyPriceInDate = dailyPriceService.getDailyPriceBySymbolListAndDate(allStockOptionSymbolList, priceDate);

        for(DailyPrice price : allDailyPriceInDate)
            dailyPriceService.deleteDailyPrice(price);

        for(PriceTimeSequence result : resultList){
            Symbol symbol = result.symbol;
            DailyPrice dailyPrice = result.dailyPrice;

            Symbol savedSymbol = symbolLookup.get(symbol.getTicker());

            dailyPrice.setSymbol(savedSymbol);
            dailyPriceService.saveDailyPrice(dailyPrice);
        }
    }

    private Double tryDoubleParse(String text){
        try {
            Double result = Double.parseDouble(text);
            return result;
        }
        catch(Exception ex){
            return null;
        }
    }

    private Long tryLongParse(String text){
        try{
            Long result = Long.parseLong(text);
            return result;
        }
        catch(Exception ex){
            return null;
        }
    }

    private class PriceTimeSequence{
        public PriceTimeSequence(Symbol symbol, DailyPrice dailyPrice){
            this.symbol = symbol;
            this.dailyPrice = dailyPrice;
        }

        Symbol symbol;
        DailyPrice dailyPrice;
    }


}

