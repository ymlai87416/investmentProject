package ymlai87416.dataservice.reader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import ymlai87416.dataservice.domain.Exchange;
import ymlai87416.dataservice.domain.Symbol;
import ymlai87416.dataservice.exception.ParseException;
import ymlai87416.dataservice.fetcher.constant.FileEncoding;
import ymlai87416.dataservice.fetcher.constant.Instruments;
import ymlai87416.dataservice.utilities.Utilities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 16/10/2016.
 */
public class HKExStockSymbolWebPageParser {

    private Logger log = LoggerFactory.getLogger(HKExStockSymbolWebPageParser.class);
    public static final String url = "http://www.hkex.com.hk/eng/market/sec_tradinfo/stockcode/eisdeqty.htm";
    public static final String instrumentType = Instruments.STOCK;
    private Exchange exchange;

    public HKExStockSymbolWebPageParser(Exchange exchange){
        this.exchange = exchange;
    }

    public List<Pair<Symbol, String>> parseFrontPage(String localPath)
            throws IOException, ParseException {

        List<Pair<Symbol, String>> result = new ArrayList<Pair<Symbol, String>>();

        Document doc;
        if(url == null){
            doc = Jsoup.parse(url);
        }
        else {
            File localFile = new File(localPath);
            doc = Jsoup.parse(localFile, FileEncoding.defaultFileEncoding, url);
        }


        Element stockTable = doc.select("table.table_grey_border").first();
        Elements records = stockTable.select("tr.tr_normal");

        for(Element record : records){
            String url;
            Elements cell = record.select("td");

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

            Symbol symbol = new Symbol();
            symbol.setExchange(exchange);
            symbol.setTicker(convertStockNumberToTicker(cell.get(0).text()));
            symbol.setInstrument(Instruments.STOCK);
            symbol.setName(cell.get(1).text());
            symbol.setLot(lot);


            Pair<Symbol, String> info = Pair.of(symbol, url);

            result.add(info);
        }

        return result;
    }

    public Symbol parseIndividualPage(Symbol symbol, String url)
            throws IOException, ParseException{
        Document doc = Jsoup.parse(url);
        return parseIndividualPage(symbol, doc);
    }

    public Symbol parseIndividualPage(Symbol symbol, String url, String localFilePath)
            throws IOException, ParseException {
        Document doc;

        try{
            File file = new File(localFilePath);
            doc = Jsoup.parse(file, FileEncoding.defaultFileEncoding, url);
        }
        catch(Exception ex){
            ex.printStackTrace();
            return null;
        }

        return parseIndividualPage(symbol, doc);
    }

    private Symbol parseIndividualPage(Symbol symbol, Document document) throws ParseException{
        Elements tables = document.select("table:contains(Company/Securities Name:)");

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

    private String convertStockNumberToTicker(String stockNumber){
        return stockNumber+".HK";
    }

}
