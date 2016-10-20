package ymlai87416.dataservice.service;

import ymlai87416.dataservice.domain.KeyValuePair;

import java.util.List;

/**
 * Created by Tom on 18/10/2016.
 */
public interface KeyValuePairService {
    KeyValuePair searchValueByKey(String key);

    List<KeyValuePair> searchValueByKeyNotExact(String key);

    int saveKeyValuePair(List<KeyValuePair> pair);

    int deleteKeyValuePair(List<String> keyList);

    boolean deleteKeyValuePair(String key);
}
