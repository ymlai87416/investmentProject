package ymlai87416.dataservice.fetcher;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by Tom on 6/10/2016.
 */
@Component
public class HKExStockOptionPriceFetcher implements Fetcher{

    String url = "http://www.hkex.com.hk/chi/ddp/most_active_contracts_c.asp?marketid=4";

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
            ArrayList<PriceTimeSequence> resultList = parsePricePage();

            savePriceTimeSequenceToDatabase(resultList);
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
        catch(ParseException ex){
            ex.printStackTrace();
        }

        return true;
    }

    private ArrayList<PriceTimeSequence> parsePricePage() throws IOException, ParseException{
        ArrayList<PriceTimeSequence> timeSequenceList = new ArrayList<>();
        Document doc = Jsoup.connect(url).get();
        Elements stockOptionList1 = doc.select("tr.tableHdrB2");
        Elements stockOptionList2 = doc.select("tr.tableHdrB1");

        Elements stockOptionList = new Elements();
        stockOptionList.addAll(stockOptionList1);
        stockOptionList.addAll(stockOptionList2);

        for(Element stockOption : stockOptionList){
            String contract1 = stockOption.child(0).text();
            String contract2 = stockOption.child(1).text();

            String contract = String.format("%s %s", contract1, contract2);

            Symbol symbol = new Symbol();
            symbol.setExchange(getOrSaveExchange(exchangeService, Exchanges.HKExchange));
            symbol.setTicker(contract);
            symbol.setInstrument("Stock Option");
            symbol.setName(contract);
            symbol.setSector(null);
            symbol.setLot(null);
            symbol.setCurrency("HKD");
            symbol.setCreatedDate(Utilities.getCurrentSQLDate());
            symbol.setLastUpdatedDate(Utilities.getCurrentSQLDate());

            Double opening, close, high, low;
            Long volume;

            opening = tryDoubleParse(stockOption.child(2).text());
            close = tryDoubleParse(stockOption.child(5).text());
            high = tryDoubleParse(stockOption.child(6).text());
            low = tryDoubleParse(stockOption.child(7).text());
            volume = tryLongParse(stockOption.child(8).text());

            DailyPrice price = new DailyPrice();
            price.setDataVendor(getOrSaveDataVendor(dataVendorService, DataVendors.HKExDataVendor));
            price.setOpenPrice(opening);
            price.setClosePrice(close);
            price.setHighPrice(high);
            price.setLowPrice(low);
            price.setVolume(volume);
            price.setSymbol(symbol);

            timeSequenceList.add(new PriceTimeSequence(symbol, price));
        }

        return timeSequenceList;
    }

    private void savePriceTimeSequenceToDatabase(ArrayList<PriceTimeSequence> resultList){
        for(PriceTimeSequence result : resultList){
            Symbol symbol = result.symbol;
            DailyPrice dailyPrice = result.dailyPrice;

            List<Symbol> symbolSearchResult = symbolService.searchSymbol(symbol);

            Symbol savedSymbol = null;

            if(symbolSearchResult == null || symbolSearchResult.size() == 0) {
                savedSymbol = symbolService.saveSymbol(symbol);
            }
            else{
                savedSymbol = symbolSearchResult.get(0);
            }

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

