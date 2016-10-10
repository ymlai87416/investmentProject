package ymlai87416.dataservice.service.common;

import org.springframework.stereotype.Service;
import ymlai87416.dataservice.service.MasterBackup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Properties;

/**
 * Created by Tom on 9/10/2016.
 */
@Service
public class MasterBackupImpl implements MasterBackup {
    Properties prop = new Properties();
    InputStream input = null;

    static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    String masterFolder = "E:\\StockDataBackup";

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
        java.util.Date currentDate = new java.util.Date();
        File masterFile = new File(masterFolder);
        File pwd = new File(masterFile.getAbsolutePath() + File.separator + clazz.getName() + File.separator + sdf.format(currentDate));

        if (!Files.exists(pwd.toPath())) {
            pwd.mkdirs();
        }

        String[] directories = pwd.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });

        int maxNumber = 0;

        if(directories !=null) {
            for (String directory : directories) {
                try {
                    int number = Integer.parseInt(directory);
                    if (number > maxNumber)
                        maxNumber = number;
                } catch (Exception ex) {
                    continue;
                }
            }
        }

        int nextNumber = maxNumber + 1;
        pwd = new File(pwd.getAbsolutePath() + File.separator + String.valueOf(nextNumber));
        pwd.mkdir();
        return pwd;
    }

    @Override
    public File retrievedLatestBatchFolder(Class clazz) {
        java.util.Date currentDate = new java.util.Date();
        File masterFile = new File(masterFolder);
        File pwd = new File(masterFile.getAbsolutePath() + File.separator + clazz.getName()+ File.separator + sdf.format(currentDate));

        if (!Files.exists(pwd.toPath())) {
            return null;
        }

        String[] directories = pwd.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });

        int maxNumber = 0;

        if(directories == null) return null;
        for(String directory : directories){
            try{
                int number = Integer.parseInt(directory);
                if(number > maxNumber)
                    maxNumber = number;
            }
            catch(Exception ex){
                continue;
            }
        }

        if(maxNumber == 0)
            return null;
        else {
            pwd = new File(pwd.getAbsolutePath() + File.separator + String.valueOf(maxNumber));
            return pwd;
        }
    }

    @Override
    public File doArchive(Class clazz) {
        return null;
    }
}
