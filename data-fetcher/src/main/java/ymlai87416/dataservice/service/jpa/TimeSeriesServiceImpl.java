package ymlai87416.dataservice.service.jpa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ymlai87416.dataservice.domain.TimeSeries;
import ymlai87416.dataservice.service.TimeSeriesService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 12/10/2016.
 */
@Service("jpaTimeSeriesService")
@Repository
@Transactional
public class TimeSeriesServiceImpl implements TimeSeriesService {

    private Logger log = LoggerFactory.getLogger(TimeSeriesServiceImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional(readOnly=true)
    public List<TimeSeries> listAllTimeSeries() {
        TypedQuery<TimeSeries> query = em.createQuery(
                "select s from TimeSeries s" , TimeSeries.class);

        return query.getResultList();
    }

    @Override
    @Transactional(readOnly=true)
    public List<TimeSeries> listAllTimeSeriesByCategory(String category) {
        TypedQuery<TimeSeries> query = em.createQuery(
                "select s from TimeSeries s where s.category = :e" , TimeSeries.class);
        query = query.setParameter("e", category);

        return query.getResultList();
    }

    @Override
    @Transactional(readOnly=true)
    public List<TimeSeries> searchTimeSeries(TimeSeries timeSeries) {
        Predicate predicate;
        CriteriaBuilder cb = em.getCriteriaBuilder();

        // Query for a List of objects.
        CriteriaQuery cq = cb.createQuery();
        Root e = cq.from(TimeSeries.class);

        List<Predicate> criteriaList = new ArrayList<Predicate>();
        cq = cq.select(e);
        if(timeSeries.getId() != null){
            predicate = cb.equal(e.get("id"), timeSeries.getId());
            criteriaList.add(predicate);
        }
        if(timeSeries.getSeriesName() != null) {
            predicate = cb.equal(e.get("seriesName"), timeSeries.getSeriesName());
            criteriaList.add(predicate);
        }
        if(timeSeries.getCategory() != null) {
            predicate = cb.equal(e.get("category"), timeSeries.getCategory());
            criteriaList.add(predicate);
        }

        cq.where(cb.and(criteriaList.toArray(new Predicate[0])));

        Query query = em.createQuery(cq);
        List<TimeSeries> result = query.getResultList();

        return result;
    }

    @Override
    public TimeSeries saveTimeSeries(TimeSeries timeSeries) {
        if (timeSeries.getId() == null) { // Insert repository
            log.info("Inserting new time series");
            em.persist(timeSeries);
        } else { // Update repository
            em.merge(timeSeries);
            log.info("Updating existing time series");
        }
        log.info("Time series repository saved with id: " + timeSeries.getId());
        return timeSeries;
    }

    private int batchSize = 25;

    @Override
    public List<TimeSeries> saveTimeSeriesInBatch(List<TimeSeries> timeSeriesList) {
        final List<TimeSeries> savedEntities = new ArrayList<TimeSeries>(timeSeriesList.size());
        int i = 0;
        for (TimeSeries t : timeSeriesList) {
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

    private TimeSeries persistOrMerge(TimeSeries t) {
        if (t.getId() == null) {
            em.persist(t);
            return t;
        } else {
            return em.merge(t);
        }
    }

    @Override
    public int deleteTimeSeries(TimeSeries timeSeries) {
        try{
            TimeSeries mergeExchange = em.merge(timeSeries);
            em.remove(mergeExchange);
            log.info("Time series repository with id: " + timeSeries.getId()
                    + " deleted successfully");
            return 1;
        }
        catch(Exception ex){
            log.error("Time series object does not exist in the database", ex);
            return 0;
        }
    }

    @Override
    public int deleteAllTimeSeries() {
        Query query = em.createQuery("DELETE FROM TimeSeries s");
        int deletedCount = query.executeUpdate();

        return deletedCount;
    }
}
