package ymlai87416.dataservice.service;

import java.io.File;

/**
 * Created by Tom on 9/10/2016.
 */
public interface MasterBackup {
    File getCurrentBatchFolder(Class clazz);

    File retrievedLatestBatchFolder(Class clazz);

    File doArchive(Class clazz);
}
