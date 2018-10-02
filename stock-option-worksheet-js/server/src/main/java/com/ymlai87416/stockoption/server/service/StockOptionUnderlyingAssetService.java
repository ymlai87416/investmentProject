package com.ymlai87416.stockoption.server.service;

import com.ymlai87416.stockoption.server.domain.StockOptionUnderlyingAsset;
import com.ymlai87416.stockoption.server.domain.Symbol;

import java.util.List;

public interface StockOptionUnderlyingAssetService {
    List<StockOptionUnderlyingAsset> listAllStockOptionUnderlyingAsset();
}
