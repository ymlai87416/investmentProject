package ymlai87416.dataservice.service.jpa;

import ymlai87416.dataservice.service.DailyPriceService;
import ymlai87416.dataservice.service.DataVendorService;

/**
 * Created by Tom on 6/10/2016.
 */
public class DataVendorServiceImpl implements DataVendorService {

    List<DataVendor> listAllDataVendor();

    List<DataVendor> searchDataVendor(DataVendor dataVendor);

    void saveDataVendor(DataVendor dataVendor);

    void deleteDataVendor(DataVendor dataVendor);

}
