package ymlai87416.dataservice;

import org.springframework.context.support.GenericXmlApplicationContext;
import ymlai87416.dataservice.domain.DailyPrice;
import ymlai87416.dataservice.domain.Symbol;
import ymlai87416.dataservice.service.DailyPriceService;
import ymlai87416.dataservice.service.DataVendorService;
import ymlai87416.dataservice.service.ExchangeService;
import ymlai87416.dataservice.service.SymbolService;

/**
 * Created by Tom on 6/10/2016.
 */
public class MainApp {
    public static DailyPriceService dailyPriceService;
    public static DataVendorService dataVendorService;
    public static ExchangeService exchangeService;
    public static SymbolService symbolService;

    public static void main(String[] args){
        GenericXmlApplicationContext ctx = new GenericXmlApplicationContext();
        ctx.load("classpath:app-context.xml");
        ctx.refresh();
        dailyPriceService = ctx.getBean("jpaDailyPriceService", DailyPriceService.class);
        dataVendorService = ctx.getBean("jpaDataVendorService", DataVendorService.class);
        exchangeService = ctx.getBean("jpaExchangeService", ExchangeService.class);
        symbolService = ctx.getBean("jpaSymbolService", SymbolService.class);

        MainApp instance = new MainApp();
        instance.run();
    }

    public void run(){
        //run the following first
        //HKExStockSymbolFetcher to check any difference
        //YahooStockPriceFetcher to check for new daily price update
        //HKExStockOptionPriceFetcher to check for new daily price update
    }
}
