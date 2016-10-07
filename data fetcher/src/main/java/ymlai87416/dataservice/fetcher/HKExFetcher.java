package ymlai87416.dataservice.fetcher;

import org.springframework.beans.factory.annotation.Autowired;
import ymlai87416.dataservice.Utilities.Utilities;
import ymlai87416.dataservice.domain.Exchange;
import ymlai87416.dataservice.service.ExchangeService;

import java.util.List;

/**
 * Created by Tom on 6/10/2016.
 */
public abstract class HKExFetcher {

    public static Exchange HKExhange = null;
    private static Object exchangeObj = new Object();


    @Autowired
    ExchangeService exchangeService;

    public HKExFetcher(){
        synchronized (HKExhange){
            if(HKExhange == null){
                HKExhange = new Exchange();
                HKExhange.setAbbrev("SEHK");
                HKExhange.setName("The Stock Exchange of Hong Kong Limited");
                HKExhange.setCity("Hong Kong");
                HKExhange.setCountry("China");
                HKExhange.setCurrency("HKD");
                HKExhange.setTimezoneOffset(new java.sql.Time(8, 0, 0));
            }
        }
    }

    public Exchange getHKExchangeFromDatabase(){
        List<Exchange> exchangeList = exchangeService.searchExchange(HKExhange);
        if(exchangeList == null || exchangeList.size() == 0)
            return null;
        else
            return exchangeList.get(0);
    }

    public Exchange saveHKExchangeToDatabase(){
        HKExhange.setCreatedDate(Utilities.getCurrentSQLDate());
        HKExhange.setLastUpdatedDate(Utilities.getCurrentSQLDate());

        return exchangeService.saveExchange(HKExhange);
    }

}
