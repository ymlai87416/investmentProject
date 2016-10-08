package ymlai87416.dataservice.fetcher.datavendor;

import ymlai87416.dataservice.Utilities.Utilities;
import ymlai87416.dataservice.domain.DataVendor;

/**
 * Created by Tom on 8/10/2016.
 */
public class DataVendors {
    public static DataVendor YahooDataVendor = new DataVendor(){{
        setName("Yahoo");
        setWebsiteUrl("https://hk.finance.yahoo.com/");
        setSupportEmail(null);
        setCreatedDate(Utilities.getCurrentSQLDate());
        setLastUpdatedDate(Utilities.getCurrentSQLDate());
    }};

    public static DataVendor HKExDataVendor = new DataVendor(){{
        setName("The Stock Exchange of Hong Kong Limited");
        setWebsiteUrl("https://www.hkex.com.hk");
        setSupportEmail(null);
        setCreatedDate(Utilities.getCurrentSQLDate());
        setLastUpdatedDate(Utilities.getCurrentSQLDate());
    }};
}
