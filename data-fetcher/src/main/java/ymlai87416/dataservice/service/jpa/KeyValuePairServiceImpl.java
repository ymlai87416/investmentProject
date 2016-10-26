package ymlai87416.dataservice.service.jpa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ymlai87416.dataservice.domain.KeyValuePair;
import ymlai87416.dataservice.service.KeyValuePairService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by Tom on 18/10/2016.
 */
@Service("jpaKeyValuePairService")
@Repository
@Transactional
public class KeyValuePairServiceImpl implements KeyValuePairService {

    private Logger log = LoggerFactory.getLogger(KeyValuePairServiceImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional(readOnly=true)
    public KeyValuePair searchValueByKey(String key) {
        TypedQuery<KeyValuePair> query = em.createQuery(
                "select s from KeyValuePair s where s.key = :e" , KeyValuePair.class);
        query = query.setParameter("e", key);

        KeyValuePair result;
        try {
            result = query.getSingleResult();
        }
        catch(Exception ex){
            result = null;
        }
        return result;
    }

    @Override
    @Transactional(readOnly=true)
    public List<KeyValuePair> searchValueByKeyNotExact(String key) {
        TypedQuery<KeyValuePair> query = em.createQuery(
                "select s from KeyValuePair s where s.key like :e" , KeyValuePair.class);
        query = query.setParameter("e", "%"+key+"%");

        return query.getResultList();
    }

    @Override
    public int saveKeyValuePair(List<KeyValuePair> keyValuePairList) {
        int saveCnt = 0;

        for(KeyValuePair keyValuePair: keyValuePairList){
            try {
                KeyValuePair inDB = searchValueByKey(keyValuePair.getKey());

                if (inDB != null) {
                    inDB.setValue(keyValuePair.getValue());
                    em.merge(inDB);
                    log.info("Updating existing key value pair");
                } else {
                    log.info("Inserting new key value pair");
                    em.persist(keyValuePair);
                }
                saveCnt++;
            }
            catch(Exception ex){
                ex.printStackTrace();
            }
        }

        return saveCnt;
    }

    @Override
    public int deleteKeyValuePair(List<String> keyList) {
        try {
            Query query = em.createQuery("DELETE FROM KeyValuePair d WHERE d.key in :s");
            int deletedCount = query.setParameter("s", keyList).executeUpdate();

            return deletedCount;
        }
        catch(Exception ex){
            return 0;
        }
    }

    @Override
    public boolean deleteKeyValuePair(String key) {
        try {
            Query query = em.createQuery("DELETE FROM KeyValuePair d WHERE d.key = :s");
            int deletedCount = query.setParameter("s", key).executeUpdate();

            return deletedCount == 1;
        }
        catch(Exception ex){
            return false;
        }
    }
}
