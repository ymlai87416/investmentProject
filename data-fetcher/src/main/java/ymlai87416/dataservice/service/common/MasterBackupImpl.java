package ymlai87416.dataservice.service.common;

import org.springframework.stereotype.Service;
import ymlai87416.dataservice.service.MasterBackup;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Created by Tom on 9/10/2016.
 */
@Service
public class MasterBackupImpl implements MasterBackup {
    Properties prop = new Properties();
    InputStream input = null;

    String masterFolder = "C:\\MasterBackup";

    public MasterBackupImpl(){
        try {
            input = new FileInputStream("app.properties");
            prop.load(input);

            masterFolder = prop.getProperty("page.cache.dir");

            Path path = new File(masterFolder).toPath();

            if (!Files.exists(path)) {
                File f = new File(masterFolder);
                f.mkdirs();
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public File getCurrentBatchFolder(Class clazz) {
        File masterFile = new File(masterFolder);
        File pwd = new File(masterFile.getAbsolutePath() + File.pathSeparator + clazz.getName());

        if (!Files.exists(pwd.toPath())) {
            pwd.mkdirs();
        }

        //scan the folder to check for 1,2,3
        return pwd;
    }

    @Override
    public File retrievedLatestBatchFolder(Class clazz) {
        return null;
    }

    @Override
    public File doArchive(Class clazz) {
        return null;
    }
}
