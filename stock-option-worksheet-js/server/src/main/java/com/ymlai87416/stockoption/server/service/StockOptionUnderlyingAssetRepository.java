package com.ymlai87416.stockoption.server.service;

import com.ymlai87416.stockoption.server.domain.StockOptionUnderlyingAsset;
        import org.springframework.data.jpa.repository.JpaRepository;

public interface StockOptionUnderlyingAssetRepository extends JpaRepository<StockOptionUnderlyingAsset, Long> {
}
