package ymlai87416.dataservice;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.util.Assert;
import ymlai87416.dataservice.Utilities.Utilities;
import ymlai87416.dataservice.domain.DailyPrice;
import ymlai87416.dataservice.domain.DataVendor;
import ymlai87416.dataservice.domain.Exchange;
import ymlai87416.dataservice.domain.Symbol;
import ymlai87416.dataservice.fetcher.HKExStockOptionPriceFetcher;
import ymlai87416.dataservice.fetcher.HKExStockSymbolFetcher;
import ymlai87416.dataservice.fetcher.YahooHKStockPriceFetcher;
import ymlai87416.dataservice.fetcher.datavendor.DataVendors;
import ymlai87416.dataservice.fetcher.exchange.Exchanges;
import ymlai87416.dataservice.service.DailyPriceService;
import ymlai87416.dataservice.service.DataVendorService;
import ymlai87416.dataservice.service.ExchangeService;
import ymlai87416.dataservice.service.SymbolService;

import javax.rmi.CORBA.Util;
import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 6/10/2016.
 */
public class MainApp {
    public static DailyPriceService dailyPriceService;
    public static DataVendorService dataVendorService;
    public static ExchangeService exchangeService;
    public static SymbolService symbolService;

    public static void main(String[] args){
        MainApp instance = new MainApp();
        instance.run();
    }

    public void run(){
        GenericXmlApplicationContext ctx = new GenericXmlApplicationContext();
        ctx.load("classpath:app-context.xml");
        ctx.refresh();

        test(ctx);
    }

    public void test(ApplicationContext ctx){

    }

    public void testPersistenceLayer(ApplicationContext ctx){
        DailyPriceService dailyPriceService = (DailyPriceService)ctx.getBean("jpaDailyPriceService");
        DataVendorService dataVendorService = (DataVendorService)ctx.getBean("jpaDataVendorService");
        ExchangeService exchangeService = (ExchangeService)ctx.getBean("jpaExchangeService");
        SymbolService symbolService = (SymbolService)ctx.getBean("jpaSymbolService");

        Exchange hkExchange, nasdaqExchange, nyseExchange;
        hkExchange = exchangeService.saveExchange(Exchanges.HKExchange);
        Assert.isTrue(hkExchange != null);
        nasdaqExchange = exchangeService.saveExchange(Exchanges.NASDAQExchange);
        Assert.isTrue(nasdaqExchange != null);
        nyseExchange = exchangeService.saveExchange(Exchanges.NYSExchange);
        Assert.isTrue(nyseExchange != null);

        List<Exchange> exchangeList = exchangeService.listAllExchange();
        Assert.isTrue(exchangeList != null && exchangeList.size() == 3);

        List<Exchange> searchResultExchange = exchangeService.searchExchange(new Exchange(){{
            setName(Exchanges.HKExchange.getName());
        }});
        Assert.isTrue(searchResultExchange != null && exchangeList.size() == 1);

        DataVendor hkexDataVendor, yahooDataVendor;
        hkexDataVendor = dataVendorService.saveDataVendor(DataVendors.HKExDataVendor);
        Assert.isTrue(hkexDataVendor != null);
        yahooDataVendor = dataVendorService.saveDataVendor(DataVendors.YahooDataVendor);
        Assert.isTrue(yahooDataVendor != null);

        List<DataVendor> dataVendorList = dataVendorService.listAllDataVendor();
        Assert.isTrue(dataVendorList != null && dataVendorList.size() == 2);

        List<DataVendor> searchResultDataVendor = dataVendorService.searchDataVendor(new DataVendor(){{
            setName(DataVendors.HKExDataVendor.getName());
        }});
        Assert.isTrue(searchResultDataVendor != null && exchangeList.size() == 1);

        int deleteCount = -1;

        //Create several symbol
        List<Symbol> symbolList = getTestingSymbol(hkExchange, hkexDataVendor);
        Symbol symbol1 = symbolService.saveSymbol(symbolList.get(0));
        Assert.isTrue(symbol1 != null);
        Symbol symbol2 = symbolService.saveSymbol(symbolList.get(1));
        Assert.isTrue(symbol2 != null);
        Symbol symbol3 = symbolService.saveSymbol(symbolList.get(2));
        Assert.isTrue(symbol3 != null);

        List<Symbol> allSymbolList = symbolService.listAllSymbol();
        Assert.isTrue(allSymbolList != null && allSymbolList.size() == 3);

        List<Symbol> hkSymbolList = symbolService.listAllSymbolByExchange(hkExchange);
        Assert.isTrue(hkSymbolList != null && hkSymbolList.size() == 3);

        List<Symbol> nasdaqSymbolList = symbolService.listAllSymbolByExchange(nasdaqExchange);
        Assert.isTrue(nasdaqSymbolList == null || nasdaqSymbolList.size() == 0);

        List<Symbol> symbolSearchResult = symbolService.searchSymbol();

        //Create daily price




        //remove daily price


        //remove symbol
        deleteCount = symbolService.deleteSymbol(symbol1);
        Assert.isTrue(deleteCount == 1);
        deleteCount = symbolService.deleteSymbol(symbol2);
        Assert.isTrue(deleteCount == 1);
        deleteCount = symbolService.deleteSymbol(symbol3);
        Assert.isTrue(deleteCount == 1);

        //now delete data vendor

        deleteCount = dataVendorService.deleteDataVendor(hkexDataVendor);
        Assert.isTrue(deleteCount == 1);
        deleteCount = dataVendorService.deleteDataVendor(yahooDataVendor);
        Assert.isTrue(deleteCount == 1);

        //now delete exchange
        deleteCount = exchangeService.deleteExchange(hkExchange);
        Assert.isTrue(deleteCount == 1);
        deleteCount = exchangeService.deleteExchange(nasdaqExchange);
        Assert.isTrue(deleteCount == 1);
        deleteCount = exchangeService.deleteExchange(nyseExchange);
        Assert.isTrue(deleteCount == 1);

    }

    private List<Symbol> getTestingSymbol(Exchange exchange, DataVendor dataVendor){

        Symbol symbol1 = new Symbol(){{
            setExchange(exchange);
            setTicker("TEST1");
            setInstrument("Stock");
            setName("Testing stock 1");
            setSector("Testing");
            setLot(1000);
            setCurrency("HKD");
            setCreatedDate(Utilities.getCurrentSQLDate());
            setLastUpdatedDate(Utilities.getCurrentSQLDate());
        }};
        Symbol symbol2 = new Symbol(){{
            setExchange(exchange);
            setTicker("TEST2");
            setInstrument("Stock");
            setName("Testing stock 2");
            setSector("Testing");
            setLot(500);
            setCurrency("HKD");
            setCreatedDate(Utilities.getCurrentSQLDate());
            setLastUpdatedDate(Utilities.getCurrentSQLDate());
        }};
        Symbol symbol3 = new Symbol(){{
            setExchange(exchange);
            setTicker("TEST3");
            setInstrument("Stock");
            setName("Testing stock 3");
            setSector("Testing");
            setLot(2000);
            setCurrency("HKD");
            setCreatedDate(Utilities.getCurrentSQLDate());
            setLastUpdatedDate(Utilities.getCurrentSQLDate());
        }};

        List<Symbol> symbolList = new ArrayList<Symbol>();
        symbolList.add(symbol1);
        symbolList.add(symbol2);
        symbolList.add(symbol3);

        return symbolList;
    }

    private List<DailyPrice> getTestingPrice(Symbol symbol, DataVendor dataVendor){
        DailyPrice price1 = null, price2 = null, price3 = null;

        if(symbol.getTicker().compareTo("TEST1") == 0){
            price1 = new DailyPrice(){{
                setDataVendor(dataVendor);
                setSymbol(symbol);
                setPriceDate(new java.sql.Date(2016,7,1));
                setCreatedDate(Utilities.getCurrentSQLDate());
                setLastUpdatedDate(Utilities.getCurrentSQLDate());
                setOpenPrice(10.0);
                setHighPrice(12.9);
                setLowPrice(9.4);
                setClosePrice(11.9);
                setAdjClosePrice(11.9);
                setVolume(1000250L);
            }};

            price2 = new DailyPrice(){{
                setDataVendor(dataVendor);
                setSymbol(symbol);
                setPriceDate(new java.sql.Date(2016,7,2));
                setCreatedDate(Utilities.getCurrentSQLDate());
                setLastUpdatedDate(Utilities.getCurrentSQLDate());
                setOpenPrice(11.9);
                setHighPrice(11.9);
                setLowPrice(10.2);
                setClosePrice(10.5);
                setAdjClosePrice(10.5);
                setVolume(400000L);
            }};

            price3 = new DailyPrice(){{
                setDataVendor(dataVendor);
                setSymbol(symbol);
                setPriceDate(new java.sql.Date(2016,7,3));
                setCreatedDate(Utilities.getCurrentSQLDate());
                setLastUpdatedDate(Utilities.getCurrentSQLDate());
                setOpenPrice(10.5);
                setHighPrice(10.9);
                setLowPrice(9.9);
                setClosePrice(10.0);
                setAdjClosePrice(10.0);
                setVolume(300000L);
            }};
        }
        else if(symbol.getTicker().compareTo("TEST2") == 0){
            price1 = new DailyPrice(){{
                setDataVendor(dataVendor);
                setSymbol(symbol);
                setPriceDate(new java.sql.Date(2016,0,1));
                setCreatedDate(Utilities.getCurrentSQLDate());
                setLastUpdatedDate(Utilities.getCurrentSQLDate());
                setOpenPrice(100.);
                setHighPrice(120.);
                setLowPrice(100.);
                setClosePrice(115.5);
                setAdjClosePrice(115.5);
                setVolume(10000000L);
            }};

            price2 = new DailyPrice(){{
                setDataVendor(dataVendor);
                setSymbol(symbol);
                setPriceDate(new java.sql.Date(2016,0,2));
                setCreatedDate(Utilities.getCurrentSQLDate());
                setLastUpdatedDate(Utilities.getCurrentSQLDate());
                setOpenPrice(115.5);
                setHighPrice(120.);
                setLowPrice(110.0);
                setClosePrice(118.5);
                setAdjClosePrice(118.5);
                setVolume(10000000L);
            }};

            price3 = new DailyPrice(){{
                setDataVendor(dataVendor);
                setSymbol(symbol);
                setPriceDate(new java.sql.Date(2016,0,3));
                setCreatedDate(Utilities.getCurrentSQLDate());
                setLastUpdatedDate(Utilities.getCurrentSQLDate());
                setOpenPrice(118.5);
                setHighPrice(125.);
                setLowPrice(118.5);
                setClosePrice(120.0);
                setAdjClosePrice(120.0);
                setVolume(10000000L);
            }};
        }
        else if(symbol.getTicker().compareTo("TEST3") == 0){
            price1 = new DailyPrice(){{
                setDataVendor(dataVendor);
                setSymbol(symbol);
                setPriceDate(new java.sql.Date(2016,3,1));
                setCreatedDate(Utilities.getCurrentSQLDate());
                setLastUpdatedDate(Utilities.getCurrentSQLDate());
                setOpenPrice(50.);
                setHighPrice(55.);
                setLowPrice(47.);
                setClosePrice(53.);
                setAdjClosePrice(53.);
                setVolume(10000000L);
            }};

            price2 = new DailyPrice(){{
                setDataVendor(dataVendor);
                setSymbol(symbol);
                setPriceDate(new java.sql.Date(2016,3,2));
                setCreatedDate(Utilities.getCurrentSQLDate());
                setLastUpdatedDate(Utilities.getCurrentSQLDate());
                setOpenPrice(53.);
                setHighPrice(60.);
                setLowPrice(50.);
                setClosePrice(60.);
                setAdjClosePrice(60.);
                setVolume(10000000L);
            }};

            price3 = new DailyPrice(){{
                setDataVendor(dataVendor);
                setSymbol(symbol);
                setPriceDate(new java.sql.Date(2016,3,3));
                setCreatedDate(Utilities.getCurrentSQLDate());
                setLastUpdatedDate(Utilities.getCurrentSQLDate());
                setOpenPrice(60.);
                setHighPrice(60.);
                setLowPrice(58.);
                setClosePrice(58.);
                setAdjClosePrice(58.);
                setVolume(10000000L);
            }};
        }

        List<DailyPrice> resultList = new ArrayList<DailyPrice>();
        resultList.add(price1);
        resultList.add(price2);
        resultList.add(price3);

        return resultList;
    }


    public void testHKExStockOptionPriceFetcher(ApplicationContext ctx){
        HKExStockOptionPriceFetcher dailyPriceService = (HKExStockOptionPriceFetcher)ctx.getBean("jpaDailyPriceService");
    }

    public void testHKExStockSymbolFetcher(ApplicationContext ctx){
        HKExStockSymbolFetcher dailyPriceService = (HKExStockSymbolFetcher)ctx.getBean("jpaDailyPriceService");
    }

    public void testYahooHKStockPriceFetcher(ApplicationContext ctx){
        YahooHKStockPriceFetcher dailyPriceService = (YahooHKStockPriceFetcher)ctx.getBean("jpaDailyPriceService");
    }
}
