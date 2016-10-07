package ymlai87416.dataservice.fetcher.exchange;

import org.springframework.beans.factory.annotation.Autowired;
import ymlai87416.dataservice.Utilities.Utilities;
import ymlai87416.dataservice.domain.Exchange;
import ymlai87416.dataservice.service.ExchangeService;

import java.util.List;

/**
 * Created by Tom on 7/10/2016.
 */
public abstract class NASDAQFetcher {
    public static Exchange NASDAQ = null;
    private static Object exchangeObj = new Object();


    @Autowired
    ExchangeService exchangeService;

    public NASDAQFetcher(){
        synchronized (exchangeObj){
            if(NASDAQ == null){
                NASDAQ = new Exchange();
                NASDAQ.setAbbrev("NASDAQ");
                NASDAQ.setName("National Association of Securities Dealers Automated Quotations");
                NASDAQ.setCity("New York");
                NASDAQ.setCountry("United States");
                NASDAQ.setCurrency("USD");
                NASDAQ.setTimezoneOffset(new java.sql.Time(-5, 0, 0));
            }
        }
    }

    public Exchange getHKExchangeFromDatabase(){
        List<Exchange> exchangeList = exchangeService.searchExchange(NASDAQ);
        if(exchangeList == null || exchangeList.size() == 0)
            return null;
        else
            return exchangeList.get(0);
    }

    public Exchange saveHKExchangeToDatabase(){
        NASDAQ.setCreatedDate(Utilities.getCurrentSQLDate());
        NASDAQ.setLastUpdatedDate(Utilities.getCurrentSQLDate());

        return exchangeService.saveExchange(NASDAQ);
    }
}
