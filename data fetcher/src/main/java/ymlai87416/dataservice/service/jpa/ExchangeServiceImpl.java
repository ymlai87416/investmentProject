package ymlai87416.dataservice.service.jpa;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ymlai87416.dataservice.domain.DailyPrice;
import ymlai87416.dataservice.domain.DataVendor;
import ymlai87416.dataservice.domain.Exchange;
import ymlai87416.dataservice.service.ExchangeService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by Tom on 6/10/2016.
 */
@Service("jpaExchangeService")
@Repository
@Transactional
public class ExchangeServiceImpl implements ExchangeService {

    private Log log = LogFactory.getLog(ExchangeServiceImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Exchange> listAllExchange() {
        TypedQuery<Exchange> query = em.createQuery(
                "select e from Exchange e" , Exchange.class);

        return query.getResultList();
    }

    @Override
    public List<Exchange> searchExchange(Exchange exchange) {
        return null;
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
    public void deleteExchange(Exchange exchange) {
        Exchange mergeExchange = em.merge(exchange);
        em.remove(mergeExchange);
        log.info("Exchange repository with id: " + exchange.getId()
                + " deleted successfully");
    }
}
