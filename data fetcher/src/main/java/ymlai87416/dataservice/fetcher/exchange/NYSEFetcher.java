package ymlai87416.dataservice.fetcher.exchange;

import org.springframework.beans.factory.annotation.Autowired;
import ymlai87416.dataservice.Utilities.Utilities;
import ymlai87416.dataservice.domain.Exchange;
import ymlai87416.dataservice.service.ExchangeService;

import java.util.List;

/**
 * Created by Tom on 7/10/2016.
 */
public class NYSEFetcher {
    public static Exchange NYSE = null;
    private static Object exchangeObj = new Object();


    @Autowired
    ExchangeService exchangeService;

    public NYSEFetcher(){
        synchronized (exchangeObj){
            if(NYSE == null){
                NYSE = new Exchange();
                NYSE.setAbbrev("NYSE");
                NYSE.setName("New York Stock Exchange");
                NYSE.setCity("New York");
                NYSE.setCountry("United States");
                NYSE.setCurrency("USD");
                NYSE.setTimezoneOffset(new java.sql.Time(-5, 0, 0));
            }
        }
    }

    public Exchange getExchangeFromDatbase(){
        List<Exchange> exchangeList = exchangeService.searchExchange(NYSE);
        if(exchangeList == null || exchangeList.size() == 0)
            return null;
        else
            return exchangeList.get(0);
    }

    public Exchange saveExchangeToDatebase(){
        NYSE.setCreatedDate(Utilities.getCurrentSQLDate());
        NYSE.setLastUpdatedDate(Utilities.getCurrentSQLDate());

        return exchangeService.saveExchange(NYSE);
    }
}
