package ymlai87416.dataservice.service.jpa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ymlai87416.dataservice.Utilities.Utilities;
import ymlai87416.dataservice.domain.DailyPrice;
import ymlai87416.dataservice.domain.TimePoint;
import ymlai87416.dataservice.domain.TimeSeries;
import ymlai87416.dataservice.service.TimePointService;

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
 * Created by Tom on 12/10/2016.
 */
@Service("jpaTimePointService")
@Repository
@Transactional
public class TimePointServiceImpl implements TimePointService {
    private Logger log = LoggerFactory.getLogger(TimePointServiceImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional(readOnly=true)
    public List<TimePoint> getAllTimePoint(TimeSeries timeSeries) {
        TypedQuery<TimePoint> query = em.createQuery(
                "select p from TimePoint p where p.symbol=:s order by timePointDate" , TimePoint.class);
        query = query.setParameter("s", timeSeries);

        return query.getResultList();
    }

    @Override
    @Transactional(readOnly=true)
    public List<TimePoint> getTimePointBySymbolAndDateRange(TimeSeries timeSeries, Date startDate, Date endDate) {
        TypedQuery<TimePoint> query = em.createQuery(
                "select p from TimePoint p where p.timeSeries=:s and p.priceDate >= :startDate and p.priceDate <= :endDate" , TimePoint.class);
        query = query.setParameter("s", timeSeries);
        query = query.setParameter("startDate", startDate);
        query = query.setParameter("endDate", endDate);

        return query.getResultList();
    }

    @Override
    @Transactional(readOnly=true)
    public List<TimePoint> getTimePointBySymbolListAndDate(List<TimeSeries> timeSeriesList, Date date) {
        TypedQuery<TimePoint> query = em.createQuery(
                "select p from TimePoint p where p.timeSeries IN :s and p.priceDate =:date" , TimePoint.class);
        query = query.setParameter("s", timeSeriesList);
        query = query.setParameter("date", date);

        return query.getResultList();
    }

    @Override
    public int deleteTimePoint(TimePoint timePoint) {
        try{
            TimePoint mergePrice = em.merge(timePoint);
            em.remove(mergePrice);
            log.info("Time point repository with id: " + timePoint.getId()
                    + " deleted successfully");
            return 1;
        }
        catch(IllegalArgumentException ex){
            log.error("Time point object does not exist in the database", ex);
            return 0;
        }
    }

    @Override
    public TimePoint saveTimePoint(TimePoint timePoint) {
        if (timePoint.getId() == null) { // Insert repository
            log.info("Inserting new time point");
            em.persist(timePoint);
        } else { // Update repository
            em.merge(timePoint);
            log.info("Updating existing time point");
        }
        log.info("Time point repository saved with id: " + timePoint.getId());
        return timePoint;
    }

    private int batchSize = 25;

    @Override
    public List<TimePoint> saveTimePointInBatch(List<TimePoint> timePointList) {
        final List<TimePoint> savedEntities = new ArrayList<TimePoint>(timePointList.size());
        int i = 0;
        for (TimePoint t : timePointList) {
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

    private TimePoint persistOrMerge(TimePoint t) {
        if (t.getId() == null) {
            em.persist(t);
            return t;
        } else {
            return em.merge(t);
        }
    }

    @Override
    public int deleteTimePointByTimeSeries(TimeSeries timeSeries) {
        Query query = em.createQuery("DELETE FROM TimePoint d WHERE d.TimeSeries = :s");
        int deletedCount = query.setParameter("s", timeSeries).executeUpdate();

        return deletedCount;
    }

    @Override
    public int deleteAllTimePoint() {
        Query query = em.createQuery("DELETE FROM TimePoint d");
        int deletedCount = query.executeUpdate();

        return deletedCount;
    }

    @Override
    @Transactional(readOnly=true)
    public List<TimePoint> searchTimePoint(TimePoint timePoint) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        Predicate predicate;

        // Query for a List of objects.
        CriteriaQuery cq = cb.createQuery();
        Root e = cq.from(TimePoint.class);
        List<Predicate> criteriaList = new ArrayList<>();

        cq = cq.select(e);
        if(timePoint.getId() != null) {
            predicate = (cb.equal(e.get("id"), timePoint.getId()));
            criteriaList.add(predicate);
        }
        if(timePoint.getTimeSeries() != null) {
            predicate = (cb.equal(e.get("timeSeries"), timePoint.getTimeSeries()));
            criteriaList.add(predicate);
        }
        if(timePoint.getTimePointDate() != null) {
            predicate = (cb.equal(e.get("timePointDate"), timePoint.getTimePointDate()));
            criteriaList.add(predicate);
        }
        if(timePoint.getValue() != null) {
            predicate = (cb.equal(e.get("value"), timePoint.getValue()));
            criteriaList.add(predicate);
        }

        cq.where(cb.and(criteriaList.toArray(new Predicate[0])));

        Query query = em.createQuery(cq);
        List<TimePoint> result = query.getResultList();

        return result;
    }

    @Override
    @Transactional(readOnly=true)
    public java.sql.Date getLastestTimePointDateForSymbol(TimeSeries timeSeries) {
        TypedQuery<java.util.Date> query = em.createQuery("SELECT max(p.timePointDate) from TimePoint p where p.timeSeries =:timeSeries"
                , java.util.Date.class);
        query = query.setParameter("timeSeries", timeSeries);

        java.util.Date date = query.getSingleResult();
        if(date == null)
            return null;
        else
            return Utilities.convertUtilDateToSqlDate(date);
    }
}
