package ymlai87416.dataservice.fetcher.datavendor;

import ymlai87416.dataservice.Utilities.Utilities;
import ymlai87416.dataservice.domain.DataVendor;

import javax.rmi.CORBA.Util;

/**
 * Created by Tom on 8/10/2016.
 */
public class DataVendors {
    public static DataVendor YahooDataVendor;
    public static DataVendor HKExDataVendor;

    static{
        YahooDataVendor = new DataVendor();
        YahooDataVendor.setName("Yahoo");
        YahooDataVendor.setWebsiteUrl("https://hk.finance.yahoo.com/");
        YahooDataVendor.setSupportEmail(null);
        YahooDataVendor.setCreatedDate(Utilities.getCurrentSQLDate());
        YahooDataVendor.setLastUpdatedDate(Utilities.getCurrentSQLDate());

        HKExDataVendor = new DataVendor();
        HKExDataVendor.setName("The Stock Exchange of Hong Kong Limited");
        HKExDataVendor.setWebsiteUrl("https://www.hkex.com.hk");
        HKExDataVendor.setSupportEmail(null);
        HKExDataVendor.setCreatedDate(Utilities.getCurrentSQLDate());
        HKExDataVendor.setLastUpdatedDate(Utilities.getCurrentSQLDate());
    }


}
