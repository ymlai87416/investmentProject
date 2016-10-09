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

    public static void main(String[] args){
        MainApp instance = new MainApp();
        instance.run();
    }

    public void run(){
        GenericXmlApplicationContext ctx = new GenericXmlApplicationContext();
        ctx.load("classpath:spring-conf.xml");
        ctx.refresh();

        test(ctx);
    }

    public void test(ApplicationContext ctx){
        clearDatabase(ctx);
        //testPersistenceLayer(ctx);

        //testHKExStockOptionPriceFetcher(ctx);
        testHKExStockSymbolFetcher(ctx);
        testYahooHKStockPriceFetcher(ctx);
    }

    private void clearDatabase(ApplicationContext ctx) {
        DailyPriceService dailyPriceService = (DailyPriceService) ctx.getBean("jpaDailyPriceService");
        DataVendorService dataVendorService = (DataVendorService) ctx.getBean("jpaDataVendorService");
        ExchangeService exchangeService = (ExchangeService) ctx.getBean("jpaExchangeService");
        SymbolService symbolService = (SymbolService) ctx.getBean("jpaSymbolService");

        dailyPriceService.deleteAllDailyPrice();
        symbolService.deleteAllSymbol();
        dataVendorService.deleteAllDataVendor();
        exchangeService.deleteAllExchange();
    }

    public void testPersistenceLayer(ApplicationContext ctx){
        DailyPriceService dailyPriceService = (DailyPriceService)ctx.getBean("jpaDailyPriceService");
        DataVendorService dataVendorService = (DataVendorService)ctx.getBean("jpaDataVendorService");
        ExchangeService exchangeService = (ExchangeService)ctx.getBean("jpaExchangeService");
        SymbolService symbolService = (SymbolService)ctx.getBean("jpaSymbolService");

        Exchange newHkExchange = getTestingExchange(Exchanges.HKExchange);
        Exchange newNasdaqExchange = getTestingExchange(Exchanges.NASDAQExchange);
        Exchange newNyseExchange = getTestingExchange(Exchanges.NYSExchange);

        Exchange hkExchange, nasdaqExchange, nyseExchange;
        hkExchange = exchangeService.saveExchange(newHkExchange);
        Assert.isTrue(hkExchange != null);
        nasdaqExchange = exchangeService.saveExchange(newNasdaqExchange);
        Assert.isTrue(nasdaqExchange != null);
        nyseExchange = exchangeService.saveExchange(newNyseExchange);
        Assert.isTrue(nyseExchange != null);

        List<Exchange> exchangeList = exchangeService.listAllExchange();
        Assert.isTrue(exchangeList != null && exchangeList.size() == 3);

        List<Exchange> searchResultExchange = exchangeService.searchExchange(new Exchange(){{
            setName(Exchanges.HKExchange.getName());
        }});
        Assert.isTrue(searchResultExchange != null && searchResultExchange.size() == 1);

        DataVendor newHkexDataVendor, newYahooDataVendor;
        newHkexDataVendor = getTestingDataVendor(DataVendors.HKExDataVendor);
        newYahooDataVendor = getTestingDataVendor(DataVendors.YahooDataVendor);

        DataVendor hkexDataVendor, yahooDataVendor;
        hkexDataVendor = dataVendorService.saveDataVendor(newHkexDataVendor);
        Assert.isTrue(hkexDataVendor != null);
        yahooDataVendor = dataVendorService.saveDataVendor(newYahooDataVendor);
        Assert.isTrue(yahooDataVendor != null);

        List<DataVendor> dataVendorList = dataVendorService.listAllDataVendor();
        Assert.isTrue(dataVendorList != null && dataVendorList.size() == 2);

        List<DataVendor> searchResultDataVendor = dataVendorService.searchDataVendor(new DataVendor(){{
            setName(DataVendors.HKExDataVendor.getName());
        }});
        Assert.isTrue(searchResultDataVendor != null && searchResultDataVendor.size() == 1);

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

        List<Symbol> symbolSearchResult = symbolService.searchSymbol(new Symbol(){{
            setTicker(symbol3.getTicker());
        }});

        Assert.isTrue(symbolSearchResult != null && symbolSearchResult.size() == 1);

        //Create daily price
        List<DailyPrice> symbol1_price = getTestingPrice(symbol1, hkexDataVendor);
        dailyPriceService.saveDailyPriceInBatch(symbol1_price);
        List<DailyPrice> symbol2_price = getTestingPrice(symbol2, hkexDataVendor);
        dailyPriceService.saveDailyPriceInBatch(symbol2_price);
        List<DailyPrice> symbol1_price_db = dailyPriceService.getAllDailyPrice(symbol1);
        Assert.isTrue(symbol1_price_db != null && symbol1_price_db.size() == 3);
        List<DailyPrice> symbol3_price_db = dailyPriceService.getAllDailyPrice(symbol3);
        Assert.isTrue(symbol3_price_db == null || symbol3_price_db.size() == 0);
        List<DailyPrice> symbol3_price = getTestingPrice(symbol3, hkexDataVendor);
        dailyPriceService.saveDailyPriceInBatch(symbol3_price);

        //remove daily price
        deleteCount = dailyPriceService.deleteDailyPrice(symbol1_price.get(0));
        Assert.isTrue(deleteCount == 1);
        deleteCount = dailyPriceService.deleteDailyPriceBySymbol(symbol1);
        Assert.isTrue(deleteCount == 2);
        deleteCount = dailyPriceService.deleteDailyPriceBySymbol(symbol2);
        Assert.isTrue(deleteCount == 3);
        deleteCount = dailyPriceService.deleteDailyPriceBySymbol(symbol3);
        Assert.isTrue(deleteCount == 3);

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

    private Exchange getTestingExchange(Exchange input){
        Exchange exchange = new Exchange();
        exchange.setAbbrev(input.getAbbrev());
        exchange.setName(input.getName());
        exchange.setCity(input.getCity());
        exchange.setCountry(input.getCountry());
        exchange.setCurrency(input.getCurrency());
        exchange.setTimezoneOffset(input.getTimezoneOffset());
        exchange.setCreatedDate(Utilities.getCurrentSQLDate());
        exchange.setLastUpdatedDate(Utilities.getCurrentSQLDate());
        return exchange;
    }

    private DataVendor getTestingDataVendor(DataVendor input){
        DataVendor dataVendor = new DataVendor();
        dataVendor.setName(input.getName());
        dataVendor.setWebsiteUrl(input.getWebsiteUrl());
        dataVendor.setSupportEmail(input.getSupportEmail());
        dataVendor.setCreatedDate(Utilities.getCurrentSQLDate());
        dataVendor.setLastUpdatedDate(Utilities.getCurrentSQLDate());
        return dataVendor;
    }

    private List<Symbol> getTestingSymbol(Exchange exchange, DataVendor dataVendor){

        Symbol symbol1 = new Symbol();
        symbol1.setExchange(exchange);
        symbol1.setTicker("TEST1");
        symbol1.setInstrument("Stock");
        symbol1.setName("Testing stock 1");
        symbol1.setSector("Testing");
        symbol1.setLot(1000);
        symbol1.setCurrency("HKD");
        symbol1. setCreatedDate(Utilities.getCurrentSQLDate());
        symbol1.setLastUpdatedDate(Utilities.getCurrentSQLDate());

        Symbol symbol2 = new Symbol();
        symbol2.setExchange(exchange);
        symbol2.setTicker("TEST2");
        symbol2.setInstrument("Stock");
        symbol2.setName("Testing stock 2");
        symbol2.setSector("Testing");
        symbol2.setLot(500);
        symbol2.setCurrency("HKD");
        symbol2.setCreatedDate(Utilities.getCurrentSQLDate());
        symbol2.setLastUpdatedDate(Utilities.getCurrentSQLDate());

        Symbol symbol3 = new Symbol();
        symbol3.setExchange(exchange);
        symbol3.setTicker("TEST3");
        symbol3.setInstrument("Stock");
        symbol3.setName("Testing stock 3");
        symbol3.setSector("Testing");
        symbol3.setLot(2000);
        symbol3.setCurrency("HKD");
        symbol3.setCreatedDate(Utilities.getCurrentSQLDate());
        symbol3.setLastUpdatedDate(Utilities.getCurrentSQLDate());

        List<Symbol> symbolList = new ArrayList<Symbol>();
        symbolList.add(symbol1);
        symbolList.add(symbol2);
        symbolList.add(symbol3);

        return symbolList;
    }

    private List<DailyPrice> getTestingPrice(Symbol symbol, DataVendor dataVendor){
        DailyPrice price1 = null, price2 = null, price3 = null;

        if(symbol.getTicker().compareTo("TEST1") == 0){
            price1 = new DailyPrice();
            price1.setDataVendor(dataVendor);
            price1.setSymbol(symbol);
            price1.setPriceDate(new java.sql.Date(2016,7,1));
            price1.setCreatedDate(Utilities.getCurrentSQLDate());
            price1.setLastUpdatedDate(Utilities.getCurrentSQLDate());
            price1.setOpenPrice(10.0);
            price1.setHighPrice(12.9);
            price1.setLowPrice(9.4);
            price1.setClosePrice(11.9);
            price1.setAdjClosePrice(11.9);
            price1.setVolume(1000250L);

            price2 = new DailyPrice();
            price2.setDataVendor(dataVendor);
            price2.setSymbol(symbol);
            price2.setPriceDate(new java.sql.Date(2016,7,2));
            price2.setCreatedDate(Utilities.getCurrentSQLDate());
            price2.setLastUpdatedDate(Utilities.getCurrentSQLDate());
            price2.setOpenPrice(11.9);
            price2.setHighPrice(11.9);
            price2.setLowPrice(10.2);
            price2.setClosePrice(10.5);
            price2.setAdjClosePrice(10.5);
            price2.setVolume(400000L);

            price3 = new DailyPrice();
            price3.setDataVendor(dataVendor);
            price3.setSymbol(symbol);
            price3.setPriceDate(new java.sql.Date(2016,7,3));
            price3.setCreatedDate(Utilities.getCurrentSQLDate());
            price3.setLastUpdatedDate(Utilities.getCurrentSQLDate());
            price3.setOpenPrice(10.5);
            price3.setHighPrice(10.9);
            price3.setLowPrice(9.9);
            price3.setClosePrice(10.0);
            price3.setAdjClosePrice(10.0);
            price3.setVolume(300000L);
        }
        else if(symbol.getTicker().compareTo("TEST2") == 0){
            price1 = new DailyPrice();
            price1.setDataVendor(dataVendor);
            price1.setSymbol(symbol);
            price1.setPriceDate(new java.sql.Date(2016,0,1));
            price1.setCreatedDate(Utilities.getCurrentSQLDate());
            price1.setLastUpdatedDate(Utilities.getCurrentSQLDate());
            price1.setOpenPrice(100.);
            price1.setHighPrice(120.);
            price1.setLowPrice(100.);
            price1.setClosePrice(115.5);
            price1.setAdjClosePrice(115.5);
            price1.setVolume(10000000L);

            price2 = new DailyPrice();
            price2.setDataVendor(dataVendor);
            price2.setSymbol(symbol);
            price2.setPriceDate(new java.sql.Date(2016,0,2));
            price2.setCreatedDate(Utilities.getCurrentSQLDate());
            price2.setLastUpdatedDate(Utilities.getCurrentSQLDate());
            price2.setOpenPrice(115.5);
            price2.setHighPrice(120.);
            price2.setLowPrice(110.0);
            price2.setClosePrice(118.5);
            price2.setAdjClosePrice(118.5);
            price2.setVolume(10000000L);

            price3 = new DailyPrice();
            price3.setDataVendor(dataVendor);
            price3.setSymbol(symbol);
            price3.setPriceDate(new java.sql.Date(2016,0,3));
            price3.setCreatedDate(Utilities.getCurrentSQLDate());
            price3.setLastUpdatedDate(Utilities.getCurrentSQLDate());
            price3.setOpenPrice(118.5);
            price3.setHighPrice(125.);
            price3.setLowPrice(118.5);
            price3.setClosePrice(120.0);
            price3.setAdjClosePrice(120.0);
            price3.setVolume(10000000L);
        }
        else if(symbol.getTicker().compareTo("TEST3") == 0){
            price1 = new DailyPrice();
            price1.setDataVendor(dataVendor);
            price1.setSymbol(symbol);
            price1.setPriceDate(new java.sql.Date(2016,3,1));
            price1.setCreatedDate(Utilities.getCurrentSQLDate());
            price1.setLastUpdatedDate(Utilities.getCurrentSQLDate());
            price1.setOpenPrice(50.);
            price1.setHighPrice(55.);
            price1.setLowPrice(47.);
            price1.setClosePrice(53.);
            price1.setAdjClosePrice(53.);
            price1.setVolume(10000000L);

            price2 = new DailyPrice();
            price2.setDataVendor(dataVendor);
            price2.setSymbol(symbol);
            price2.setPriceDate(new java.sql.Date(2016,3,2));
            price2.setCreatedDate(Utilities.getCurrentSQLDate());
            price2.setLastUpdatedDate(Utilities.getCurrentSQLDate());
            price2.setOpenPrice(53.);
            price2.setHighPrice(60.);
            price2.setLowPrice(50.);
            price2.setClosePrice(60.);
            price2.setAdjClosePrice(60.);
            price2.setVolume(10000000L);

            price3 = new DailyPrice();
            price3.setDataVendor(dataVendor);
            price3.setSymbol(symbol);
            price3.setPriceDate(new java.sql.Date(2016,3,3));
            price3.setCreatedDate(Utilities.getCurrentSQLDate());
            price3.setLastUpdatedDate(Utilities.getCurrentSQLDate());
            price3.setOpenPrice(60.);
            price3.setHighPrice(60.);
            price3.setLowPrice(58.);
            price3.setClosePrice(58.);
            price3.setAdjClosePrice(58.);
            price3.setVolume(10000000L);
        }

        List<DailyPrice> resultList = new ArrayList<DailyPrice>();
        resultList.add(price1);
        resultList.add(price2);
        resultList.add(price3);

        return resultList;
    }


    public void testHKExStockOptionPriceFetcher(ApplicationContext ctx){
        HKExStockOptionPriceFetcher hKExStockOptionPriceFetcher = (HKExStockOptionPriceFetcher)ctx.getBean("HKExStockOptionPriceFetcher");

        hKExStockOptionPriceFetcher.run();
    }

    public void testHKExStockSymbolFetcher(ApplicationContext ctx){
        HKExStockSymbolFetcher hKExStockSymbolFetcher = (HKExStockSymbolFetcher)ctx.getBean("HKExStockSymbolFetcher");

        hKExStockSymbolFetcher.run();
    }

    public void testYahooHKStockPriceFetcher(ApplicationContext ctx){
        YahooHKStockPriceFetcher yahooHKStockPriceFetcher = (YahooHKStockPriceFetcher)ctx.getBean("YahooHKStockPriceFetcher");

        yahooHKStockPriceFetcher.run();
    }
}
