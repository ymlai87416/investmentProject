package ymlai87416.dataservice.fetcher.exchange;

import ymlai87416.dataservice.Utilities.Utilities;
import ymlai87416.dataservice.domain.Exchange;

/**
 * Created by Tom on 8/10/2016.
 */
public class Exchanges {
    public static Exchange HKExchange;
    public static Exchange NYSExchange;
    public static Exchange NASDAQExchange;

    static{
        HKExchange = new Exchange();
        HKExchange.setAbbrev("SEHK");
        HKExchange.setName("The Stock Exchange of Hong Kong Limited");
        HKExchange.setCity("Hong Kong");
        HKExchange.setCountry("China");
        HKExchange.setCurrency("HKD");
        HKExchange.setTimezoneOffset(new java.sql.Time(8, 0, 0));
        HKExchange.setCreatedDate(Utilities.getCurrentSQLDate());
        HKExchange.setLastUpdatedDate(Utilities.getCurrentSQLDate());

        NYSExchange = new Exchange();
        NYSExchange.setAbbrev("NYSE");
        NYSExchange.setName("New York Stock Exchange");
        NYSExchange.setCity("New York");
        NYSExchange.setCountry("United States");
        NYSExchange.setCurrency("USD");
        NYSExchange.setTimezoneOffset(new java.sql.Time(-5, 0, 0));
        NYSExchange.setCreatedDate(Utilities.getCurrentSQLDate());
        NYSExchange.setLastUpdatedDate(Utilities.getCurrentSQLDate());

        NASDAQExchange = new Exchange();
        NASDAQExchange.setAbbrev("NASDAQ");
        NASDAQExchange.setName("National Association of Securities Dealers Automated Quotations");
        NASDAQExchange.setCity("New York");
        NASDAQExchange.setCountry("United States");
        NASDAQExchange.setCurrency("USD");
        NASDAQExchange.setTimezoneOffset(new java.sql.Time(-5, 0, 0));
        NASDAQExchange.setCreatedDate(Utilities.getCurrentSQLDate());
        NASDAQExchange.setLastUpdatedDate(Utilities.getCurrentSQLDate());
    }
}
