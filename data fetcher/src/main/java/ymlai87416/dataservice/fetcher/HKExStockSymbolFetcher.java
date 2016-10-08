package ymlai87416.dataservice.fetcher;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ymlai87416.dataservice.exception.PageFormatChangedException;
import ymlai87416.dataservice.exception.ParseException;
import ymlai87416.dataservice.Utilities.Utilities;
import ymlai87416.dataservice.domain.Exchange;
import ymlai87416.dataservice.domain.Symbol;
import ymlai87416.dataservice.fetcher.exchange.Exchanges;
import ymlai87416.dataservice.service.ExchangeService;
import ymlai87416.dataservice.service.SymbolService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Tom on 6/10/2016.
 */
@Component
public class HKExStockSymbolFetcher implements Fetcher{

    final String url = "http://www.hkex.com.hk/eng/market/sec_tradinfo/stockcode/eisdeqty.htm";
    final String instrumentType = "Stock";

    @Autowired
    ExchangeService exchangeService;

    @Autowired
    SymbolService symbolService;

    @Override
    public synchronized boolean run(){
        try{
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


            Elements cell = record.select("td");


            url = cell.get(1).child(0).html();

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
}
