package com.ymlai87416.stockoption.server.service.jpa;

import com.ymlai87416.stockoption.server.domain.StockOptionUnderlyingAsset;
import com.ymlai87416.stockoption.server.domain.Symbol;
import com.ymlai87416.stockoption.server.service.StockOptionUnderlyingAssetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Service("jpaStockOptionUnderlyingAssetService")
@Repository
@Transactional
public class StockOptionUnderlyingAssetServiceImpl implements StockOptionUnderlyingAssetService {
    private Logger log = LoggerFactory.getLogger(SymbolServiceImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional(readOnly=true)
    public List<StockOptionUnderlyingAsset> listAllStockOptionUnderlyingAsset() {
        TypedQuery<StockOptionUnderlyingAsset> query = em.createQuery(
                "select s from StockOptionUnderlyingAsset s" , StockOptionUnderlyingAsset.class);

        return query.getResultList();
    }
}
