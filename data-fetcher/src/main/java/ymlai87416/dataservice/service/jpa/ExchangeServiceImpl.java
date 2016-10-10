package ymlai87416.dataservice.service.jpa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ymlai87416.dataservice.domain.DataVendor;
import ymlai87416.dataservice.domain.Exchange;
import ymlai87416.dataservice.service.ExchangeService;

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
 * Created by Tom on 6/10/2016.
 */
@Service("jpaExchangeService")
@Repository
@Transactional
public class ExchangeServiceImpl implements ExchangeService {

    private Logger log = LoggerFactory.getLogger(ExchangeServiceImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional(readOnly=true)
    public List<Exchange> listAllExchange() {
        TypedQuery<Exchange> query = em.createQuery(
                "select e from Exchange e" , Exchange.class);

        return query.getResultList();
    }

    @Override
    @Transactional(readOnly=true)
    public List<Exchange> searchExchange(Exchange exchange) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        Predicate predicate;

        // Query for a List of objects.
        CriteriaQuery cq = cb.createQuery();
        Root e = cq.from(Exchange.class);
        List<Predicate> criteriaList = new ArrayList<Predicate>();

        cq = cq.select(e);
        if(exchange.getId() != null) {
            predicate = (cb.equal(e.get("id"), exchange.getId()));
            criteriaList.add(predicate);
        }
        if(exchange.getAbbrev() != null) {
            predicate = (cb.equal(e.get("abbrev"), exchange.getAbbrev()));
            criteriaList.add(predicate);
        }
        if(exchange.getName() != null) {
            predicate = (cb.equal(e.get("name"), exchange.getName()));
            criteriaList.add(predicate);
        }
        if(exchange.getCity() != null) {
            predicate = (cb.equal(e.get("city"), exchange.getCity()));
            criteriaList.add(predicate);
        }
        if(exchange.getCountry() != null) {
            predicate = (cb.equal(e.get("country"), exchange.getCountry()));
            criteriaList.add(predicate);
        }
        if(exchange.getCurrency() != null) {
            predicate = (cb.equal(e.get("currency"), exchange.getCurrency()));
            criteriaList.add(predicate);
        }
        if(exchange.getTimezoneOffset() != null) {
            predicate = (cb.equal(e.get("timezoneOffset"), exchange.getTimezoneOffset()));
            criteriaList.add(predicate);
        }

        cq.where(cb.and(criteriaList.toArray(new Predicate[0])));

        Query query = em.createQuery(cq);
        List<Exchange> result = query.getResultList();

        return result;
    }

    @Override
    public Exchange saveExchange(Exchange exchange) {
        if (exchange.getId() == null) { // Insert repository
            log.info("Inserting new exchange");
            em.persist(exchange);
        } else { // Update repository
            em.merge(exchange);
            log.info("Updating existing exchange");
        }
        log.info("Exchange repository saved with id: " + exchange.getId());
        return exchange;
    }

    @Override
    public int deleteExchange(Exchange exchange) {
        try{
            Exchange mergeExchange = em.merge(exchange);
            em.remove(mergeExchange);
            log.info("Exchange repository with id: " + exchange.getId()
                    + " deleted successfully");
            return 1;
        }
        catch(Exception ex){
            log.error("Exchange object does not exist in the database", ex);
            return 0;
        }

    }

    @Override
    public int deleteAllExchange() {
        Query query = em.createQuery("DELETE FROM Exchange e");
        int deletedCount = query.executeUpdate();

        return deletedCount;
    }
}
