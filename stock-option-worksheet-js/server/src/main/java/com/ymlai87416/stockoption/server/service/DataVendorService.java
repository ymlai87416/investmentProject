package com.ymlai87416.stockoption.server.service;

import com.ymlai87416.stockoption.server.domain.*;

import java.util.List;

/**
 * Created by Tom on 6/10/2016.
 */
public interface DataVendorService {
    List<DataVendor> listAllDataVendor();

    List<DataVendor> searchDataVendor(DataVendor dataVendor);

    DataVendor saveDataVendor(DataVendor dataVendor);

    int deleteDataVendor(DataVendor dataVendor);

    int deleteAllDataVendor();
}
