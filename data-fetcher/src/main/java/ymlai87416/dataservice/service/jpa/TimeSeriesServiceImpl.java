package ymlai87416.dataservice.service.jpa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ymlai87416.dataservice.domain.Symbol;
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
    public List<TimeSeries> searchSymbol(TimeSeries timeSeries) {
        Predicate predicate;
        CriteriaBuilder cb = em.getCriteriaBuilder();

        // Query for a List of objects.
        CriteriaQuery cq = cb.createQuery();
        Root e = cq.from(Symbol.class);

        List<Predicate> criteriaList = new ArrayList<Predicate>();
        cq = cq.select(e);
        if(symbol.getId() != null){
            predicate = cb.equal(e.get("id"), symbol.getId());
            criteriaList.add(predicate);
        }
        if(symbol.getExchange() != null) {
            predicate = cb.equal(e.get("exchange"), symbol.getExchange());
            criteriaList.add(predicate);
        }

        cq.where(cb.and(criteriaList.toArray(new Predicate[0])));

        Query query = em.createQuery(cq);
        List<Symbol> result = query.getResultList();

        return result;
    }

    @Override
    public TimeSeries saveTimeSeries(TimeSeriesService timeSeries) {
        return null;
    }

    @Override
    public List<TimeSeries> saveTimeSeriesInBatch(List<TimeSeries> timeSeriesList) {
        return null;
    }

    @Override
    public int deleteTimeSeries(TimeSeries timeSeries) {
        return 0;
    }

    @Override
    public int deleteAllTimeSeries() {
        return 0;
    }
}
