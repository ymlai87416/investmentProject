package ymlai87416.dataservice.fetcher;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ymlai87416.dataservice.Utilities.Utilities;
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

    final String url = "http://www.hkex.com.hk/eng/market/sec_tradinfo/stockcode/eisdeqty.htm";
    final String instrumentType = Instruments.STOCK;
    final String cacheFileName = ".cache";

    @Autowired
    ExchangeService exchangeService;

    @Autowired
    SymbolService symbolService;

    @Autowired
    MasterBackup masterBackup;

    @Override
    public synchronized boolean run(){
        try{
            File masterDir = initMasterBackup();
            boolean iscached = checkCompleteMark(masterDir);

            List<Symbol> symbols = parseFrontPage(masterDir, iscached, false);

            if(!iscached)
                createCompleteMark(masterDir);

            for(Symbol symbol : symbols)
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

        return true;
    }

    private List<Symbol> parseFrontPage(File masterDir, boolean isCached, boolean checkForUpdate) throws IOException, ParseException{
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
            }
        }

        String cacheFilePath;

        cacheFilePath = urlToFileMapping.get(url);
        Document doc;
        if(cacheFilePath == null){
            String destination = masterDir.getAbsolutePath() + File.separator + "main";
            downloadFileToMasterBackup(masterDir, url, destination);
            File input = new File(destination);
            doc = Jsoup.parse(input, FileEncoding.defaultFileEncoding, url);
        }
        else{
            File input = new File(cacheFilePath);
            doc = Jsoup.parse(input, FileEncoding.defaultFileEncoding, url);
        }

        Element stockTable = doc.select("table.table_grey_border").first();
        Elements records = stockTable.select("tr.tr_normal");

        ArrayList<SymbolInfo> symbolInfoList = new ArrayList<SymbolInfo>();

        for(Element record : records){
            String url;
            Elements cell = record.select("td");

            //TODO: remove line after debug
            if(cell.size() == 2)
                System.out.println("I am here.");

            url = cell.get(1).child(0).attr("abs:href");

            int lot;
            String lotStr = cell.get(2).text().replace(",", "");
            try{
                lot = Integer.parseInt(lotStr);
            }
            catch(Exception ex){
                lot = 1;
            }

            Exchange exchange = getOrSaveExchange(exchangeService, Exchanges.HKExchange);

            Symbol symbol = new Symbol();
            symbol.setExchange(exchange);
            symbol.setTicker(convertStockNumberToTicker(cell.get(0).text()));
            symbol.setInstrument(Instruments.STOCK);
            symbol.setName(cell.get(1).text());
            symbol.setLot(lot);

            SymbolInfo info = new SymbolInfo(symbol, url);

            symbolInfoList.add(info);
        }

        //if check for update, only deep down
        if(checkForUpdate){
            List<Symbol> existingSymbol = symbolService.listAllSymbolByExchange(Exchanges.HKExchange);

            for(SymbolInfo symbolInfo : symbolInfoList){
                if(existingSymbol.contains(symbolInfo))
                    symbolInfoList.remove(symbolInfo);
            }
        }

        //for each stock, download
        ArrayList<Symbol> resultList = new ArrayList<Symbol>();
        for(SymbolInfo symbolInfo : symbolInfoList){
            parseIndividualPage(symbolInfo.infoUrl, symbolInfo.symbol, masterDir, urlToFileMapping);
            resultList.add(symbolInfo.symbol);
        }

        return resultList;
    }

    private String convertStockNumberToTicker(String stockNumber){
        return stockNumber+".HK";
    }

    private Symbol parseIndividualPage(String url, Symbol symbol, File masterDir, TreeMap<String, String> urlToFileMapping) throws IOException, ParseException {
        Document doc;
        if(urlToFileMapping != null){
            String cacheFIlePath = urlToFileMapping.get(url);
            if(cacheFIlePath == null){
                //download file
                String destination = masterDir.getAbsolutePath() + File.separator + symbol.getTicker();
                downloadFileToMasterBackup(masterDir, url, destination);
                File input = new File(destination);
                doc = Jsoup.parse(input, FileEncoding.defaultFileEncoding, url);
            }
            else{
                //use cache
                File input = new File(cacheFIlePath);
                doc = Jsoup.parse(input, FileEncoding.defaultFileEncoding, url);
            }
        }
        else{
            //download file
            String destination = masterDir.getAbsolutePath() + File.separator + symbol.getTicker();
            downloadFileToMasterBackup(masterDir, url, destination);
            File input = new File(destination);
            doc = Jsoup.parse(input, FileEncoding.defaultFileEncoding, url);
        }

        Elements tables = doc.select("table:contains(Company/Securities Name:)");

        if(tables == null || tables.size() == 0)
            throw new ParseException();

        Element smallestTable = tables.get(0);
        int minTableContentLen = tables.get(0).text().length();

        for(Element table : tables){
            if(table.text().length() < minTableContentLen) {
                smallestTable = table;
                minTableContentLen = table.text().length();
            }
        }

        Elements cells = smallestTable.select("td");

        symbol.setCurrency(cells.get(20).text().trim());
        symbol.setInstrument(instrumentType);
        symbol.setSector(cells.get(14).text().trim());
        symbol.setCreatedDate(Utilities.getCurrentSQLDateTime());
        symbol.setLastUpdatedDate(Utilities.getCurrentSQLDateTime());

        return symbol;
    }


    class SymbolInfo{
        Symbol symbol;
        String infoUrl;

        public SymbolInfo(Symbol symbol, String infoUrl){
            this.symbol = symbol;
            this.infoUrl = infoUrl;
        }
    }

    public static void main(String[] args){

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
