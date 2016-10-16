package ymlai87416.dataservice.service.jpa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ymlai87416.dataservice.utilities.Utilities;
import ymlai87416.dataservice.domain.DailyPrice;
import ymlai87416.dataservice.domain.Symbol;
import ymlai87416.dataservice.service.DailyPriceService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Tom on 6/10/2016.
 */
@Service("jpaDailyPriceService")
@Repository
@Transactional
public class DailyPriceServiceImpl implements DailyPriceService{

    private Logger log = LoggerFactory.getLogger(DailyPriceServiceImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional(readOnly=true)
    public List<DailyPrice> getAllDailyPrice(Symbol symbol) {
        TypedQuery<DailyPrice> query = em.createQuery(
                "select p from DailyPrice p where p.symbol=:s order by priceDate" , DailyPrice.class);
        query = query.setParameter("s", symbol);

        return query.getResultList();
    }

    @Override
    @Transactional(readOnly=true)
    public List<DailyPrice> getDailyPriceBySymbolAndDateRange(Symbol symbol, Date startDate, Date endDate) {
        TypedQuery<DailyPrice> query = em.createQuery(
                "select p from DailyPrice p where p.symbol=:s and p.priceDate >= :startDate and p.priceDate <= :endDate" , DailyPrice.class);
        query = query.setParameter("s", symbol);
        query = query.setParameter("startDate", startDate);
        query = query.setParameter("endDate", endDate);

        return query.getResultList();
    }

    @Override
    @Transactional(readOnly=true)
    public List<DailyPrice> getDailyPriceBySymbolListAndDate(List<Symbol> symbolList, Date date) {
        TypedQuery<DailyPrice> query = em.createQuery(
                "select p from DailyPrice p where p.symbol IN :s and p.priceDate =:date" , DailyPrice.class);
        query = query.setParameter("s", symbolList);
        query = query.setParameter("date", date);

        return query.getResultList();
    }

    @Override
    public int deleteDailyPrice(DailyPrice dailyPrice) {
        try{
            DailyPrice mergePrice = em.merge(dailyPrice);
            em.remove(mergePrice);
            log.info("DailyPrice repository with id: " + dailyPrice.getId()
                    + " deleted successfully");
            return 1;
        }
        catch(IllegalArgumentException ex){
            log.error("Daily price object does not exist in the database", ex);
            return 0;
        }
    }

    @Override
    public DailyPrice saveDailyPrice(DailyPrice dailyPrice) {
        if (dailyPrice.getId() == null) { // Insert repository
            log.info("Inserting new daily price");
            em.persist(dailyPrice);
        } else { // Update repository
            em.merge(dailyPrice);
            log.info("Updating existing daily price");
        }
        log.info("DailyPrice repository saved with id: " + dailyPrice.getId());
        return dailyPrice;
    }

    @Override
    public int deleteDailyPriceBySymbol(Symbol symbol) {
        Query query = em.createQuery("DELETE FROM DailyPrice d WHERE d.symbol = :s");
        int deletedCount = query.setParameter("s", symbol).executeUpdate();

        return deletedCount;
    }

    @Override
    public int deleteAllDailyPrice() {
        Query query = em.createQuery("DELETE FROM DailyPrice d");
        int deletedCount = query.executeUpdate();

        return deletedCount;
    }

    @Override
    public List<DailyPrice> searchDailyPrice(DailyPrice dailyPrice) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        Predicate predicate;

        // Query for a List of objects.
        CriteriaQuery cq = cb.createQuery();
        Root e = cq.from(DailyPrice.class);
        List<Predicate> criteriaList = new ArrayList<>();

        cq = cq.select(e);
        if(dailyPrice.getId() != null) {
            predicate = (cb.equal(e.get("id"), dailyPrice.getId()));
            criteriaList.add(predicate);
        }
        if(dailyPrice.getDataVendor() != null) {
            predicate = (cb.equal(e.get("dataVendor"), dailyPrice.getDataVendor()));
            criteriaList.add(predicate);
        }
        if(dailyPrice.getSymbol() != null) {
            predicate = (cb.equal(e.get("symbol"), dailyPrice.getSymbol()));
            criteriaList.add(predicate);
        }
        if(dailyPrice.getPriceDate() != null) {
            predicate = (cb.equal(e.get("priceDate"), dailyPrice.getPriceDate()));
            criteriaList.add(predicate);
        }
        if(dailyPrice.getCreatedDate() != null) {
            predicate = (cb.equal(e.get("createdDate"), dailyPrice.getCreatedDate()));
            criteriaList.add(predicate);
        }
        if(dailyPrice.getLastUpdatedDate() != null){
            predicate = (cb.equal(e.get("lastUpdatedDate"), dailyPrice.getLastUpdatedDate()));
            criteriaList.add(predicate);
        }
        if(dailyPrice.getOpenPrice() != null) {
            predicate = (cb.equal(e.get("openPrice"), dailyPrice.getOpenPrice()));
            criteriaList.add(predicate);
        }
        if(dailyPrice.getHighPrice() != null) {
            predicate = (cb.equal(e.get("highPrice"), dailyPrice.getHighPrice()));
            criteriaList.add(predicate);
        }
        if(dailyPrice.getLowPrice() != null) {
            predicate = (cb.equal(e.get("lowPrice"), dailyPrice.getLowPrice()));
            criteriaList.add(predicate);
        }
        if(dailyPrice.getClosePrice() != null) {
            predicate = (cb.equal(e.get("closePrice"), dailyPrice.getClosePrice()));
            criteriaList.add(predicate);
        }
        if(dailyPrice.getAdjClosePrice() != null) {
            predicate = (cb.equal(e.get("adjClosePrice"), dailyPrice.getAdjClosePrice()));
            criteriaList.add(predicate);
        }
        if(dailyPrice.getVolume() != null) {
            predicate = (cb.equal(e.get("volume"), dailyPrice.getVolume()));
            criteriaList.add(predicate);
        }

        cq.where(cb.and(criteriaList.toArray(new Predicate[0])));

        Query query = em.createQuery(cq);
        List<DailyPrice> result = query.getResultList();

        return result;
    }

    @Override
    public java.sql.Date getLastestDailyPriceDateForSymbol(Symbol symbol) {
        TypedQuery<java.util.Date> query = em.createQuery("SELECT max(p.priceDate) from DailyPrice p where p.symbol =:symbol"
                                                            , java.util.Date.class);
        query = query.setParameter("symbol", symbol);

        java.util.Date date = query.getSingleResult();
        if(date == null)
            return null;
        else
            return Utilities.convertUtilDateToSqlDate(date);
    }

    private int batchSize = 25;

    @Override
    public List<DailyPrice> saveDailyPriceInBatch(List<DailyPrice> dailyPriceList) {
        final List<DailyPrice> savedEntities = new ArrayList<DailyPrice>(dailyPriceList.size());
        int i = 0;
        for (DailyPrice t : dailyPriceList) {
            savedEntities.add(persistOrMerge(t));
            i++;
            if (i % batchSize == 0) {
                // Flush a batch of inserts and release memory.
                em.flush();
                em.clear();
            }
        }
        return savedEntities;
    }

    private DailyPrice persistOrMerge(DailyPrice t) {
        if (t.getId() == null) {
            em.persist(t);
            return t;
        } else {
            return em.merge(t);
        }
    }
}
