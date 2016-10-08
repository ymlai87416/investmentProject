package ymlai87416.dataservice.fetcher.exchange;

import ymlai87416.dataservice.domain.Exchange;

/**
 * Created by Tom on 8/10/2016.
 */
public class Exchanges {
    public static Exchange HKExchange = new Exchange()
    {{
        setAbbrev("SEHK");
        setName("The Stock Exchange of Hong Kong Limited");
        setCity("Hong Kong");
        setCountry("China");
        setCurrency("HKD");
        setTimezoneOffset(new java.sql.Time(8, 0, 0));
    }};

    public static Exchange NYSExchange = new Exchange()
    {{
        setAbbrev("NYSE");
        setName("New York Stock Exchange");
        setCity("New York");
        setCountry("United States");
        setCurrency("USD");
        setTimezoneOffset(new java.sql.Time(-5, 0, 0));
    }};

    public static Exchange NASDAQExchange = new Exchange()
    {{
        setAbbrev("NASDAQ");
        setName("National Association of Securities Dealers Automated Quotations");
        setCity("New York");
        setCountry("United States");
        setCurrency("USD");
        setTimezoneOffset(new java.sql.Time(-5, 0, 0));
    }};
}
