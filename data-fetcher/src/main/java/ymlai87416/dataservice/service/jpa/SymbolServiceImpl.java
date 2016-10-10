package ymlai87416.dataservice.service.jpa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 6/10/2016.
 */
@Service("jpaSymbolService")
@Repository
@Transactional
public class SymbolServiceImpl implements SymbolService {

    private Logger log = LoggerFactory.getLogger(SymbolServiceImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional(readOnly=true)
    public List<Symbol> listAllSymbol() {
        TypedQuery<Symbol> query = em.createQuery(
                "select s from Symbol s" , Symbol.class);

        return query.getResultList();
    }

    @Override
    @Transactional(readOnly=true)
    public List<Symbol> listAllSymbolByExchange(Exchange exchange) {
        TypedQuery<Symbol> query = em.createQuery(
                "select s from Symbol s where s.exchange = :e" , Symbol.class);
        query = query.setParameter("e", exchange);

        return query.getResultList();
    }

    @Override
    public List<Symbol> searchSymbol(Symbol symbol) {
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
        if(symbol.getTicker() != null) {
            predicate = cb.equal(e.get("ticker"), symbol.getTicker());
            criteriaList.add(predicate);
        }
        if(symbol.getInstrument() != null) {
            predicate = cb.equal(e.get("instrument"), symbol.getInstrument());
            criteriaList.add(predicate);
        }
        if(symbol.getName() != null) {
            predicate = cb.equal(e.get("name"), symbol.getName());
            criteriaList.add(predicate);
        }
        if(symbol.getSector() != null) {
            predicate = cb.equal(e.get("sector"), symbol.getSector());
            criteriaList.add(predicate);
        }
        if(symbol.getLot() != null) {
            predicate = cb.equal(e.get("lot"), symbol.getLot());
            criteriaList.add(predicate);
        }
        if(symbol.getCurrency() != null) {
            predicate = cb.equal(e.get("currency"), symbol.getCurrency());
            criteriaList.add(predicate);
        }

        cq.where(cb.and(criteriaList.toArray(new Predicate[0])));

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

    private int batchSize = 25;

    @Override
    public List<Symbol> saveSymbolInBatch(List<Symbol> symbolList) {
        final List<Symbol> savedEntities = new ArrayList<Symbol>(symbolList.size());
        int i = 0;
        for (Symbol t : symbolList) {
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

    private Symbol persistOrMerge(Symbol t) {
        if (t.getId() == null) {
            em.persist(t);
            return t;
        } else {
            return em.merge(t);
        }
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

    @Override
    public int deleteAllSymbol() {
        Query query = em.createQuery("DELETE FROM Symbol s");
        int deletedCount = query.executeUpdate();

        return deletedCount;
    }
}
