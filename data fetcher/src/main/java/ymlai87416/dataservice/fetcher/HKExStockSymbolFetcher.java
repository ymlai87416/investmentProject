package ymlai87416.dataservice.fetcher;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import ymlai87416.dataservice.Exception.PageFormatChangedException;
import ymlai87416.dataservice.Exception.ParseException;
import ymlai87416.dataservice.Utilities.Utilities;
import ymlai87416.dataservice.domain.Exchange;
import ymlai87416.dataservice.domain.Symbol;
import ymlai87416.dataservice.fetcher.exchange.HKExFetcher;
import ymlai87416.dataservice.service.SymbolService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

/**
 * Created by Tom on 6/10/2016.
 */
public class HKExStockSymbolFetcher extends HKExFetcher {
    String configFile;
    Properties properties;

    final String url = "http://www.hkex.com.hk/eng/market/sec_tradinfo/stockcode/eisdeqty.htm";
    final String stockTableClassName = "table_grey_border";
    final String instrumentType = "Stock";

    @Autowired
    SymbolService symbolService;

    public boolean test(){

        return true;
    }

    public synchronized boolean run(){
        try{
            Exchange exchange = getExchangeFromDatbase();
            if(exchange == null)
                saveExchangeToDatebase();

            List<Symbol> symbols = parseFrontPage();

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

    private List<Symbol> parseFrontPage() throws IOException, ParseException{
        Document doc = Jsoup.connect(url).get();
        Element stockTable = doc.select("table.table_grey_border").first();
        Elements records = stockTable.select("tr.tr_normal");

        ArrayList<SymbolInfo> symbolInfoList = new ArrayList<SymbolInfo>();

        for(Element record : records){

            String url;
            Symbol symbol = new Symbol();

            Elements cell = record.select("td");

            symbol.setTicker(cell.get(0).text());
            symbol.setName(cell.get(1).text());

            url = cell.get(1).child(0).html();

            int lot;
            String lotstr = cell.get(2).text();
            try{
                lot = Integer.parseInt(lotstr);
            }
            catch(Exception ex){
                lot = 1;
            }
            symbol.setLot(lot);

            SymbolInfo info = new SymbolInfo(symbol, url);

            symbolInfoList.add(info);
        }

        //for each stock, download
        ArrayList<Symbol> resultList = new ArrayList<Symbol>();
        for(SymbolInfo symbolInfo : symbolInfoList){
            parseIndividualPage(symbolInfo.infoUrl, symbolInfo.symbol);
            resultList.add(symbolInfo.symbol);
        }

        return resultList;
    }

    private Symbol parseIndividualPage(String url, Symbol symbol) throws IOException, ParseException {
        Document doc = Jsoup.connect(url).get();
        Element firstCell = doc.select("tr.containsOwn(Company/Securities Name:)").first();
        Optional<Element> infoTable =  Optional.of(firstCell)
                .map(Element::parent)
                .map(Element::parent);

        if(!infoTable.isPresent())
            throw new PageFormatChangedException();

        Elements cells = infoTable.get().select("td");

        symbol.setCurrency(cells.get(20).text().trim());
        symbol.setInstrument(instrumentType);
        symbol.setSector(cells.get(14).text().trim());
        symbol.setExchange(HKExhange);
        symbol.setCreatedDate(Utilities.getCurrentSQLDate());
        symbol.setLastUpdatedDate(Utilities.getCurrentSQLDate());

        return symbol;
    }

    public void loadConfig(){

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
}
