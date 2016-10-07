package ymlai87416.dataservice.fetcher;

import ymlai87416.dataservice.fetcher.datavendor.YahooStockPriceFetcher;

/**
 * Created by Tom on 7/10/2016.
 */
public class YahooHKStockPriceFetcher extends YahooStockPriceFetcher {
    @Override
    public boolean test() {
        return false;
    }

    @Override
    public boolean run() {


        return false;
    }

    @Override
    public void loadConfig() {

    }
}
