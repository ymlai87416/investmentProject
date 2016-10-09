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
import ymlai87416.dataservice.domain.Exchange;
import ymlai87416.dataservice.domain.Symbol;
import ymlai87416.dataservice.exception.PageFormatChangedException;
import ymlai87416.dataservice.exception.ParseException;
import ymlai87416.dataservice.fetcher.exchange.Exchanges;
import ymlai87416.dataservice.service.ExchangeService;
import ymlai87416.dataservice.service.MasterBackup;
import ymlai87416.dataservice.service.SymbolService;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;

/**
 * Created by Tom on 6/10/2016.
 */
@Component
public class HKExStockSymbolFetcher implements Fetcher{

    private Log log = LogFactory.getLog(HKExStockSymbolFetcher.class);

    final String url = "http://www.hkex.com.hk/eng/market/sec_tradinfo/stockcode/eisdeqty.htm";
    final String instrumentType = "Stock";
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

            List<Symbol> symbols = parseFrontPage(masterDir, iscached);
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

    private List<Symbol> parseFrontPage(File masterDir, boolean isCached) throws IOException, ParseException{
        TreeMap<String, String> urlToFileMapping = new TreeMap<String, String>();
        if(isCached){
            String cacheFilePath = masterDir.getAbsolutePath() + File.separator + cacheFileName;
            try(BufferedReader br = new BufferedReader(new FileReader(cacheFilePath))) {
                String key = br.readLine();
                String value;

                while (key != null) {
                    value = br.readLine();
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
            doc = Jsoup.parse(input, "UTF-8", url);
        }
        else{
            File input = new File(cacheFilePath);
            doc = Jsoup.parse(input, "UTF-8", url);
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
            String lotStr = cell.get(2).text();
            try{
                lot = Integer.parseInt(lotStr);
            }
            catch(Exception ex){
                lot = 1;
            }

            Exchange exchange = getOrSaveExchange(exchangeService, Exchanges.HKExchange);

            Symbol symbol = new Symbol();
            symbol.setExchange(exchange);
            symbol.setTicker(cell.get(0).text());
            symbol.setInstrument("Stock");
            symbol.setName(cell.get(1).text());
            symbol.setLot(lot);

            SymbolInfo info = new SymbolInfo(symbol, url);

            symbolInfoList.add(info);
        }

        //for each stock, download
        ArrayList<Symbol> resultList = new ArrayList<Symbol>();
        for(SymbolInfo symbolInfo : symbolInfoList){
            parseIndividualPage(symbolInfo.infoUrl, symbolInfo.symbol, masterDir, urlToFileMapping);
            resultList.add(symbolInfo.symbol);
        }

        createCompleteMark(masterDir);

        return resultList;
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
                doc = Jsoup.parse(input, "UTF-8", url);
            }
            else{
                //use cache
                File input = new File(cacheFIlePath);
                doc = Jsoup.parse(input, "UTF-8", url);
            }
        }
        else{
            //download file
            String destination = masterDir.getAbsolutePath() + File.separator + symbol.getTicker();
            downloadFileToMasterBackup(masterDir, url, destination);
            File input = new File(destination);
            doc = Jsoup.parse(input, "UTF-8", url);
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
        symbol.setCreatedDate(Utilities.getCurrentSQLDate());
        symbol.setLastUpdatedDate(Utilities.getCurrentSQLDate());

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
            writer.println(destination);
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
