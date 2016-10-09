package ymlai87416.dataservice.fetcher;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

/**
 * Created by Tom on 7/10/2016.
 */
@Component
public class YahooHKStockPriceFetcher implements Fetcher{

    private Log log = LogFactory.getLog(YahooHKStockPriceFetcher.class);

    @Override
    public boolean run() {


        return false;
    }
}
