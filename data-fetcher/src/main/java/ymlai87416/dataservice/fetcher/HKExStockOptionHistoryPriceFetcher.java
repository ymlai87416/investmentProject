package ymlai87416.dataservice.fetcher;

import com.opencsv.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import sun.security.pkcs.ParsingException;
import ymlai87416.dataservice.domain.*;
import ymlai87416.dataservice.reader.HKExStockOptionReportCSVReader;
import ymlai87416.dataservice.utilities.Utilities;
import ymlai87416.dataservice.fetcher.constant.DataVendors;
import ymlai87416.dataservice.fetcher.constant.Exchanges;
import ymlai87416.dataservice.service.*;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Tom on 11/10/2016.
 */
@Component("HKExStockOptionHistoryPriceFetcher")
public class HKExStockOptionHistoryPriceFetcher implements Fetcher{

    private Logger log = LoggerFactory.getLogger(HKExStockOptionHistoryPriceFetcher.class);
    private static String urlFormaturlFormat = "http://www.hkex.com.hk/eng/stat/dmstat/dayrpt/dqe%s.zip";
    private static String zipFileNameFormat="dqe%s.zip";

    private static SimpleDateFormat linkDateFormat = new SimpleDateFormat("yyMMdd");

    Date startDate;
    Date endDate;
    Date processedDate;

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

    @Autowired
    KeyValuePairService keyValuePairService;

    @Override
    public boolean run(Map<String, Object> parameter) {
        File file = initMasterBackup();

        determineStartTimeAndEndTime(parameter);

        if(!checkDownloadCompleteMark(file)){
            downloadZipFilesToFolder(file);
            createDownloadCompleteMark(file);
        }

        unzipFolderToCSV(file);

        List<Pair<Symbol, DailyPrice>> resultList = readCSV(file);

        List<Symbol> symbolList = groupResultList(resultList);

        saveSymbolPricePairToDB(symbolList);

        Progress p = new Progress();

        //TODO THe real end date is not today.
        p.lastProcessedDate = processedDate;
        writeProgressToDB(p);

        return true;
    }

    private void determineStartTimeAndEndTime(Map<String, Object> parameter){
        Progress progress = readProgressFromDB();
        if(progress != null){
            startDate  = Utilities.getNextDate(progress.lastProcessedDate);
        }
        else{
            startDate = new Date(2016-1900, 10-1, 13);
        }

        endDate = new Date();
    }

    private void downloadZipFilesToFolder(File masterDir){
        Date realEndDate = null;
        for(long time=startDate.getTime(); time < endDate.getTime(); time+=24*60*60*1000) {
            try {
                Date ctime = new Date(time);
                String dateStr = linkDateFormat.format(ctime);
                String url = String.format(urlFormaturlFormat, dateStr);
                String zip = String.format(zipFileNameFormat, dateStr);
                URL website = new URL(url);
                ReadableByteChannel rbc = Channels.newChannel(website.openStream());

                FileOutputStream fos = new FileOutputStream(masterDir.getAbsoluteFile() + File.separator + zip);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

                realEndDate = new Date(time);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if(realEndDate == null)
            realEndDate = startDate;
        endDate = realEndDate;
    }

    private void unzipFolderToCSV(File masterDir){
        String[] fileList = masterDir.list();

        for(int i=0; i<fileList.length; ++i){
            if(fileList[i].endsWith(".zip")){
                try{
                    byte[] buffer = new byte[1024];

                    //get the zip file content
                    ZipInputStream zis =
                            new ZipInputStream(new FileInputStream(masterDir.getAbsolutePath() + File.separator + fileList[i]));
                    //get the zipped file list entry
                    ZipEntry ze = zis.getNextEntry();

                    while(ze!=null){

                        String fileName = ze.getName();
                        File newFile = new File(masterDir + File.separator + fileName);

                        if(newFile.exists())
                            newFile.delete();

                        log.info("file unzip : "+ newFile.getAbsoluteFile());

                        //create all non exists folders
                        //else you will hit FileNotFoundException for compressed folder
                        new File(newFile.getParent()).mkdirs();

                        FileOutputStream fos = new FileOutputStream(newFile);

                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }

                        fos.close();
                        ze = zis.getNextEntry();
                    }

                    zis.closeEntry();
                    zis.close();
                }
                catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }

    private List<Pair<Symbol, DailyPrice>> readCSV(File masterDir){
        String[] fileList = masterDir.list();

        Exchange exchange = getOrSaveExchange(exchangeService, Exchanges.HKExchange);
        DataVendor dataVendor = getOrSaveDataVendor(dataVendorService, DataVendors.HKExDataVendor);
        HKExStockOptionReportCSVReader csvReader = new HKExStockOptionReportCSVReader(exchange, dataVendor);

        List<Pair<Symbol, DailyPrice>> result =new ArrayList<>();

        for(int i=0; i<fileList.length; ++i){
            if(fileList[i].endsWith(".csv")){

                String csvFileName = fileList[i];

                String priceDateStr = csvFileName.toUpperCase().replace("DQE", "").replace(".CSV", "").trim();
                java.util.Date priceDate;
                try {
                    priceDate = linkDateFormat.parse(priceDateStr);
                }
                catch(Exception ex){
                    ex.printStackTrace();
                    continue;
                }

                if(processedDate == null || processedDate.compareTo(priceDate) > 0)
                    processedDate = priceDate;

                if(priceDate.compareTo(startDate) < 0)
                    log.info(String.format("Skip file: %s", csvFileName));
                else {
                    log.info(String.format("Processing file: %s", csvFileName));
                    String csvFilePath = masterDir.getAbsolutePath() + File.separator + csvFileName;

                    try {
                        File csvFile = new File(csvFilePath);
                        List<Pair<Symbol, DailyPrice>> intermediate = csvReader.readStockOptionReportCSV(csvFile.getAbsolutePath(), priceDate);

                        result.addAll(intermediate);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return result;
    }

    //TODO: Out of memory error
    private void saveSymbolPricePairToDB(List<Symbol> symbolList){
        for(Symbol symbol : symbolList){

            List<Symbol> symbolDBSearchResult = symbolService.searchSymbol(symbol, true);

            if(symbolDBSearchResult == null || symbolDBSearchResult.size() == 0){
                symbolService.saveSymbol(symbol);
            }
            else{
                if(symbolDBSearchResult.size() > 1){
                    List<String> foundSymbolId= symbolDBSearchResult.stream().map(x -> x.getId().toString()).collect(Collectors.toList());
                    String symbolIdList = String.join(",", foundSymbolId);
                    log.error(String.format("One ore more symbol found when searching symbol. symbol id are: ", symbolIdList));
                    continue;
                }

                Symbol symbolDB = symbolDBSearchResult.get(0);
                List<DailyPrice> existingDailyPrice = symbolDB.getDailyPriceList();
                if(existingDailyPrice == null){
                    for(DailyPrice dailyPrice : symbol.getDailyPriceList()) {
                        dailyPrice.setSymbol(symbolDB);
                        dailyPriceService.saveDailyPrice(dailyPrice);
                    }
                }
                else{
                    List<DailyPrice> dailyPriceList = symbol.getDailyPriceList();

                    for(DailyPrice dailyPrice : dailyPriceList){
                        boolean insert = true;
                        for(DailyPrice dp : symbolDB.getDailyPriceList()){
                            if(dp.getPriceDate().compareTo(dailyPrice.getPriceDate()) == 0){
                                insert=false;
                                dp.setLastUpdatedDate(Utilities.getCurrentSQLDateTime());
                                dp.setOpenPrice(dailyPrice.getOpenPrice());
                                dp.setHighPrice(dailyPrice.getHighPrice());
                                dp.setLowPrice(dailyPrice.getLowPrice());
                                dp.setClosePrice(dailyPrice.getClosePrice());
                                dp.setAdjClosePrice(dailyPrice.getAdjClosePrice());
                                dp.setVolume(dailyPrice.getVolume());
                                dp.setIv(dailyPrice.getIv());
                                dp.setOpenInterest(dailyPrice.getOpenInterest());

                                //save and break;
                                dailyPriceService.saveDailyPrice(dp);
                                break;
                            }
                        }

                        if(insert == true){
                            dailyPrice.setSymbol(symbolDB);
                            dailyPriceService.saveDailyPrice(dailyPrice);
                        }
                    }
                }
            }
        }
    }

    private List<Symbol> groupResultList(List<Pair<Symbol, DailyPrice>> resultList){
        List<Symbol> returnVal;

        Map<String, Symbol> symbolMap = new HashMap<String, Symbol>();

        for(Pair<Symbol, DailyPrice> result : resultList){
            Symbol symbol = result.getFirst();
            DailyPrice price = result.getSecond();
            if(symbolMap.get(symbol.getTicker()) == null){
                symbolMap.put(symbol.getTicker(), result.getFirst());
            }

            Symbol symbolDict = symbolMap.get(symbol.getTicker());

            if(symbolDict.getDailyPriceList() == null)
                symbolDict.setDailyPriceList(new ArrayList<DailyPrice>());

            symbolDict.getDailyPriceList().add(price);
            price.setSymbol(symbolDict);
        }

        returnVal = new ArrayList<>(symbolMap.values());

        return returnVal;
    }

    private File initMasterBackup(){
        File previousFolder = masterBackup.retrievedLatestBatchFolder(this.getClass());

        boolean readExistingFolder = true;
        if(previousFolder == null){
            readExistingFolder = false;
        }
        else{
            if(!checkDownloadCompleteMark(previousFolder))
                readExistingFolder = false;
        }

        if(readExistingFolder)
            return masterBackup.retrievedLatestBatchFolder(this.getClass());
        else {
            File newFolder = masterBackup.getCurrentBatchFolder(this.getClass());
            return newFolder;
        }
    }

    private boolean checkDownloadCompleteMark(File file){
        try {
            String completeMarkPath = file.getAbsolutePath() + File.separator + ".download.complete";
            File completeMark = new File(completeMarkPath);

            return completeMark.exists();
        }
        catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    private void createDownloadCompleteMark(File file){
        try {
            String completeMarkPath = file.getAbsolutePath() + File.separator + ".download.complete";
            File completeMark = new File(completeMarkPath);

            completeMark.createNewFile();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private void writeProgressToDB(Progress progress){
        KeyValuePair pair = new KeyValuePair();
        pair.setKey(this.getClass().getName());
        pair.setValue(new SimpleDateFormat("yyyyMMdd").format(progress.lastProcessedDate));
        keyValuePairService.saveKeyValuePair(Arrays.asList(pair));
    }

    private Progress readProgressFromDB(){
        try {
            KeyValuePair pair = keyValuePairService.searchValueByKey(this.getClass().getName());

            if (pair != null) {
                String info = pair.getValue();
                Progress p = new Progress();
                p.lastProcessedDate = (new SimpleDateFormat("yyyyMMdd")).parse(info);
                return p;
            }
            return null;
        }
        catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    private class Progress{
        Date lastProcessedDate;
    }
}
