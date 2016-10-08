package ymlai87416.dataservice.service.jpa;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ymlai87416.dataservice.domain.Exchange;
import ymlai87416.dataservice.domain.Symbol;
import ymlai87416.dataservice.service.SymbolService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Created by Tom on 6/10/2016.
 */
@Service("jpaSymbolService")
@Repository
@Transactional
public class SymbolServiceImpl implements SymbolService {

    private Log log = LogFactory.getLog(SymbolServiceImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Symbol> listAllSymbol() {
        TypedQuery<Symbol> query = em.createQuery(
                "select s from Symbol s" , Symbol.class);

        return query.getResultList();
    }

    @Override
    public List<Symbol> listAllSymbolByExchange(Exchange exchange) {
        TypedQuery<Symbol> query = em.createQuery(
                "select s from Symbol s where s.exchange = :e" , Symbol.class);
        query = query.setParameter("e", exchange);

        return query.getResultList();
    }

    @Override
    public List<Symbol> searchSymbol(Symbol symbol) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        // Query for a List of objects.
        CriteriaQuery cq = cb.createQuery();
        Root e = cq.from(Symbol.class);

        if(symbol.getId() != null)
            cq.where(cb.equal(e.get("id"), symbol.getId()));
        if(symbol.getExchange() != null)
            cq.where(cb.equal(e.get("exchange"), symbol.getExchange()));
        if(symbol.getTicker() != null)
            cq.where(cb.equal(e.get("ticker"), symbol.getTicker()));
        if(symbol.getInstrument() != null)
            cq.where(cb.equal(e.get("instrument"), symbol.getInstrument()));
        if(symbol.getName() != null)
            cq.where(cb.equal(e.get("name"), symbol.getName()));
        if(symbol.getSector() != null)
            cq.where(cb.equal(e.get("sector"), symbol.getSector()));
        if(symbol.getLot() != null)
            cq.where(cb.equal(e.get("lot"), symbol.getLot()));
        if(symbol.getCurrency() != null)
            cq.where(cb.equal(e.get("currency"), symbol.getCurrency()));

        Query query = em.createQuery(cq);
        List<Symbol> result = query.getResultList();

        return result;
    }

    @Override
    public Symbol saveSymbol(Symbol symbol) {
        if (symbol.getId() == null) { // Insert repository
            log.info("Inserting new symbol");
            em.persist(symbol);
        } else { // Update repository
            em.merge(symbol);
            log.info("Updating existing symbol");
        }
        log.info("Symbol repository saved with id: " + symbol.getId());
        return symbol;
    }

    @Override
    public int deleteSymbol(Symbol symbol) {
        try{
            Symbol mergeExchange = em.merge(symbol);
            em.remove(mergeExchange);
            log.info("Symbol repository with id: " + symbol.getId()
                    + " deleted successfully");
            return 1;
        }
        catch(Exception ex){
            log.error("Symbol object does not exist in the database", ex);
            return 0;
        }

    }

    @Override
    public int deleteSymbolByExchange(Exchange exchange) {
        Query query = em.createQuery("DELETE FROM Symbol s WHERE s.exchange = :e");
        int deletedCount = query.setParameter("e", exchange).executeUpdate();

        return deletedCount;
    }
}
