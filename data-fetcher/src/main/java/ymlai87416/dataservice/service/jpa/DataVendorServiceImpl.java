package ymlai87416.dataservice.service.jpa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ymlai87416.dataservice.domain.DataVendor;
import ymlai87416.dataservice.service.DataVendorService;

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
@Service("jpaDataVendorService")
@Repository
@Transactional
public class DataVendorServiceImpl implements DataVendorService {
    private Logger log = LoggerFactory.getLogger(DataVendorServiceImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional(readOnly=true)
    public List<DataVendor> listAllDataVendor() {
        TypedQuery<DataVendor> query = em.createQuery(
                "select v from DataVendor v" , DataVendor.class);

        return query.getResultList();
    }

    @Override
    @Transactional(readOnly=true)
    public List<DataVendor> searchDataVendor(DataVendor dataVendor) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        Predicate predicate;

        // Query for a List of objects.
        CriteriaQuery cq = cb.createQuery();
        Root e = cq.from(DataVendor.class);
        List<Predicate> criteriaList = new ArrayList<Predicate>();

        cq = cq.select(e);
        if(dataVendor.getId() != null) {
            predicate = (cb.equal(e.get("id"), dataVendor.getId()));
            criteriaList.add(predicate);
        }
        if(dataVendor.getName() != null) {
            predicate = (cb.equal(e.get("name"), dataVendor.getName()));
            criteriaList.add(predicate);
        }
        if(dataVendor.getWebsiteUrl() != null) {
            predicate = (cb.equal(e.get("websiteUrl"), dataVendor.getWebsiteUrl()));
            criteriaList.add(predicate);
        }
        if(dataVendor.getSupportEmail() != null) {
            predicate = (cb.equal(e.get("supportEmail"), dataVendor.getSupportEmail()));
            criteriaList.add(predicate);
        }

        cq.where(cb.and(criteriaList.toArray(new Predicate[0])));

        Query query = em.createQuery(cq);
        List<DataVendor> result = query.getResultList();

        return result;
    }

    @Override
    public DataVendor saveDataVendor(DataVendor dataVendor) {
        if (dataVendor.getId() == null) { // Insert repository
            log.info("Inserting new data vendor");
            em.persist(dataVendor);
        } else { // Update repository
            em.merge(dataVendor);
            log.info("Updating existing data vendor");
        }
        log.info("Data vendor repository saved with id: " + dataVendor.getId());
        return dataVendor;
    }

    @Override
    public int deleteDataVendor(DataVendor dataVendor) {
        try{
            DataVendor mergeDataVendor = em.merge(dataVendor);
            em.remove(mergeDataVendor);
            log.info("Data vendor repository with id: " + dataVendor.getId()
                    + " deleted successfully");
            return 1;
        }
        catch(IllegalArgumentException ex){
            log.error("Data vendor object does not exist in the database", ex);
            return 0;
        }

    }

    @Override
    public int deleteAllDataVendor() {
        Query query = em.createQuery("DELETE FROM DataVendor d");
        int deletedCount = query.executeUpdate();

        return deletedCount;
    }
}
