package ymlai87416.dataservice.fetcher;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import ymlai87416.dataservice.reader.HKExStockSymbolWebPageParser;
import ymlai87416.dataservice.utilities.Utilities;
import ymlai87416.dataservice.domain.Exchange;
import ymlai87416.dataservice.domain.Symbol;
import ymlai87416.dataservice.exception.ParseException;
import ymlai87416.dataservice.fetcher.constant.Exchanges;
import ymlai87416.dataservice.fetcher.constant.FileEncoding;
import ymlai87416.dataservice.fetcher.constant.Instruments;
import ymlai87416.dataservice.service.ExchangeService;
import ymlai87416.dataservice.service.MasterBackup;
import ymlai87416.dataservice.service.SymbolService;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Created by Tom on 6/10/2016.
 *
 * TODO: Save all file in UTF-8 encoding
 */
@Component("HKExStockSymbolFetcher")
public class HKExStockSymbolFetcher implements Fetcher{

    private Logger log = LoggerFactory.getLogger(HKExStockSymbolFetcher.class);

    //final String url = "http://www.hkex.com.hk/eng/market/sec_tradinfo/stockcode/eisdeqty.htm";
    //final String instrumentType = Instruments.STOCK;
    final String cacheFileName = ".cache";

    @Autowired
    ExchangeService exchangeService;

    @Autowired
    SymbolService symbolService;

    @Autowired
    MasterBackup masterBackup;

    @Override
    public synchronized boolean run(Map<String, Object> parameter){
        try{
            log.info("Start fetching HK stock symbol...");

            File masterDir = initMasterBackup();

            HKExStockSymbolWebPageParser parser = new HKExStockSymbolWebPageParser(getOrSaveExchange(exchangeService, Exchanges.HKExchange));

            File frontPageLocalCache = createLocalCacheForWebPage(masterDir, HKExStockSymbolWebPageParser.url, "main");

            List<Pair<Symbol, String>> symbolInfoList = parser.parseFrontPage(frontPageLocalCache.getAbsolutePath());

            List<Symbol> existingSymbol = symbolService.listAllSymbolByExchange(Exchanges.HKExchange);

            List<String> existingTicker = existingSymbol.stream().map(x -> x.getTicker()).collect(Collectors.toList());

            for(Pair<Symbol, String> symbolInfo : symbolInfoList){
                if(existingTicker.contains(symbolInfo.getFirst().getTicker()))
                    symbolInfoList.remove(symbolInfo);
            }

            for(Pair<Symbol, String> symbolInfo : symbolInfoList){
                File pageCache = createLocalCacheForWebPage(masterDir, symbolInfo.getSecond(), symbolInfo.getFirst().getTicker());

                Symbol symbol = symbolInfo.getFirst();
                String url = symbolInfo.getSecond();
                Symbol updatedSymbol = parser.parseIndividualPage(symbol, url, pageCache.getAbsolutePath());

                existingSymbol.add(updatedSymbol);
            }

            if(!checkCompleteMark(masterDir))
                createCompleteMark(masterDir);

            for(Symbol symbol : existingSymbol)
                symbolService.saveSymbol(symbol);
        }
        catch(IOException ex){
            ex.printStackTrace();
            return false;
        }
        catch(ParseException ex){
            ex.printStackTrace();
            return false;
        }

        log.info("Finish fetching HK stock symbol");
        return true;
    }

    private File createLocalCacheForWebPage(File masterDir, String url, String cacheFileName) throws IOException{
        boolean isCached = checkCompleteMark(masterDir);
        TreeMap<String, String> urlToFileMapping = new TreeMap<String, String>();
        if(isCached){
            String cacheFilePath = masterDir.getAbsolutePath() + File.separator + cacheFileName;
            InputStreamReader isr = new InputStreamReader(new FileInputStream(cacheFilePath), FileEncoding.defaultFileEncoding);
            BufferedReader br = new BufferedReader(isr);
            try{
                List<String> lines = br.lines().collect(Collectors.toList());

                for(int i=0; i<lines.size(); i+=2){
                    String key = lines.get(i);
                    String value = masterDir.getAbsolutePath() + File.separator + lines.get(i+1);
                    urlToFileMapping.put(key, value);
                }
            }
            catch(Exception ex){
                urlToFileMapping.clear();
                File cacheFile = new File(cacheFilePath);
                cacheFile.delete();
            }
        }

        String cacheFilePath = urlToFileMapping.get(url);

        if(cacheFilePath == null){
            String destination = masterDir.getAbsolutePath() + File.separator + cacheFileName;
            downloadFileToMasterBackup(masterDir, url, destination);
            cacheFilePath = destination;
        }
        else{
            cacheFilePath = masterDir.getAbsolutePath() + File.separator + cacheFilePath;
        }

        return new File(cacheFilePath);
    }

    private File initMasterBackup(){
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

    private void downloadFileToMasterBackup(File masterDirectory, String url, String destination) {
        try {
            Utilities.downloadWebPageToFile(url, destination);
            String cacheFileNameAbsolutePath = masterDirectory.getAbsoluteFile() + File.separator + cacheFileName;
            PrintWriter writer = new PrintWriter(new FileOutputStream(new File(cacheFileNameAbsolutePath),true));
            writer.println(url);
            File fDestination = new File(destination);
            writer.println(fDestination.getName());
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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


}
