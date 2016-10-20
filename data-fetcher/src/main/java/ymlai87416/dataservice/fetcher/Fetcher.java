package ymlai87416.dataservice.fetcher;

import ymlai87416.dataservice.domain.DataVendor;
import ymlai87416.dataservice.domain.Exchange;
import ymlai87416.dataservice.service.DataVendorService;
import ymlai87416.dataservice.service.ExchangeService;

import java.util.List;
import java.util.Map;

/**
 * Created by Tom on 7/10/2016.
 */
public interface Fetcher {

    boolean run(Map<String, Object> parameter);

    default Exchange getOrSaveExchange(ExchangeService exchangeService, Exchange exchange){
        List<Exchange> searchResult = exchangeService.searchExchange(exchange);

        Exchange result;
        if(searchResult == null || searchResult.size() == 0)
            result = exchangeService.saveExchange(exchange);
        else
            result = searchResult.get(0);

        return result;
    }

    default DataVendor getOrSaveDataVendor(DataVendorService dataVendorService, DataVendor dataVendor){
        List<DataVendor> searchResult = dataVendorService.searchDataVendor(dataVendor);

        DataVendor result;
        if(searchResult == null || searchResult.size() == 0)
            result = dataVendorService.saveDataVendor(dataVendor);
        else
            result = searchResult.get(0);

        return result;
    }
}
