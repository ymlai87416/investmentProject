package ymlai87416.dataservice.service;

import ymlai87416.dataservice.domain.DataVendor;

import java.util.List;

/**
 * Created by Tom on 6/10/2016.
 */
public interface DataVendorService {
    List<DataVendor> listAllDataVendor();

    List<DataVendor> searchDataVendor(DataVendor dataVendor);

    DataVendor saveDataVendor(DataVendor dataVendor);

    int deleteDataVendor(DataVendor dataVendor);
}
