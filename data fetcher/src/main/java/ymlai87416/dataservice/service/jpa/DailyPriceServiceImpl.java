package ymlai87416.dataservice.service.jpa;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ymlai87416.dataservice.domain.DailyPrice;
import ymlai87416.dataservice.domain.Symbol;
import ymlai87416.dataservice.service.DailyPriceService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
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

    private Log log = LogFactory.getLog(DailyPriceServiceImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional(readOnly=true)
    public List<DailyPrice> getAllDailyPrice(Symbol symbol) {
        TypedQuery<DailyPrice> query = em.createQuery(
                "select p from DailyPrice p where p.symbol=:s" , DailyPrice.class);
        query = query.setParameter("s", symbol);

        return query.getResultList();
    }

    @Override
    public List<DailyPrice> getDailyPriceBySymbolAndDateRange(Symbol symbol, Date startDate, Date endDate) {
        TypedQuery<DailyPrice> query = em.createQuery(
                "select p from DailyPrice p where p.symbol=:s and p.priceDate >= :startDate and p.priceDate <= :endDate" , DailyPrice.class);
        query = query.setParameter("s", symbol);
        query = query.setParameter("startDate", startDate);
        query = query.setParameter("endDate", endDate);

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

    @Value("${hibernate.jdbc.batch_size}")
    private int batchSize;

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
