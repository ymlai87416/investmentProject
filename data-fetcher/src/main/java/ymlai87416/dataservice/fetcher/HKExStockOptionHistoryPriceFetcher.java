package ymlai87416.dataservice.fetcher;

import com.opencsv.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.stereotype.Component;
import ymlai87416.dataservice.domain.DailyPrice;
import ymlai87416.dataservice.domain.DataVendor;
import ymlai87416.dataservice.domain.Exchange;
import ymlai87416.dataservice.domain.Symbol;
import ymlai87416.dataservice.fetcher.constant.DataVendors;
import ymlai87416.dataservice.fetcher.constant.Exchanges;
import ymlai87416.dataservice.service.*;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Tom on 11/10/2016.
 */
@Component("HKExStockOptionHistoryPriceFetcher")
public class HKExStockOptionHistoryPriceFetcher implements Fetcher{

    private Logger log = LoggerFactory.getLogger(HKExStockOptionHistoryPriceFetcher.class);
    String urlFormaturlFormat = "http://www.hkex.com.hk/eng/stat/dmstat/dayrpt/dqe%02d%02d%02d.zip";
    String zipFileNameFormat="dqe%02d%02d%02d.zip";

    @Autowired
    ExchangeService exchangeService;

    @Autowired
    DailyPriceService dailyPriceService;

    @Autowired
    DataVendorService dataVendorService;

    @Autowired
    SymbolService symbolService;

    @Autowired
    MasterBackup masterBackup;

    @Override
    public boolean run() {
        File file = initMasterBackup();

        Exchange exchange = getOrSaveExchange(exchangeService, Exchanges.HKExchange);
        DataVendor dataVendor = getOrSaveDataVendor(dataVendorService, DataVendors.HKExDataVendor);

        if(checkDownloadCompleteMark(file)){
            downloadZipFilesToFolder(file);
            createDownloadCompleteMark(file);
        }

        if(checkUnzipCompleteMark(file)){
            unzipFolderToCSV(file);
            createUnzipCompleteMark(file);
        }

        if(checkReadCompleteMark(file)){
            readCSV(file);
            createReadCompleteMark(file);
        }

        return true;
    }

    private void downloadZipFilesToFolder(File masterDir){
        Date startDate = new Date(2016-1900, 8-1, 12);
        Date endDate = new Date();

        for(long time=startDate.getTime(); time < endDate.getTime(); time+=24*60*60*1000) {
            try {
                Date ctime = new Date(time);
                String url = String.format(urlFormaturlFormat, ctime.getYear()+1900, ctime.getMonth()+1, ctime.getDay());
                String zip = String.format(zipFileNameFormat, ctime.getYear()+1900, ctime.getMonth()+1, ctime.getDay());
                URL website = new URL(url);
                ReadableByteChannel rbc = Channels.newChannel(website.openStream());

                FileOutputStream fos = new FileOutputStream(masterDir.getAbsoluteFile() + File.separator + zip);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void unzipFolderToCSV(File masterDir){
        String[] fileList = masterDir.list();

        for(int i=0; i<fileList.length; ++i){
            if(fileList[i].endsWith(".zip")){
                try{
                    byte[] buffer = new byte[1024];

                    //get the zip file content
                    ZipInputStream zis =
                            new ZipInputStream(new FileInputStream(fileList[i]));
                    //get the zipped file list entry
                    ZipEntry ze = zis.getNextEntry();

                    while(ze!=null){

                        String fileName = ze.getName();
                        File newFile = new File(masterDir + File.separator + fileName);

                        System.out.println("file unzip : "+ newFile.getAbsoluteFile());

                        //create all non exists folders
                        //else you will hit FileNotFoundException for compressed folder
                        new File(newFile.getParent()).mkdirs();

                        FileOutputStream fos = new FileOutputStream(newFile);

                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }

                        fos.close();
                        ze = zis.getNextEntry();
                    }

                    zis.closeEntry();
                    zis.close();
                }
                catch(Exception ex){
                    ex.printStackTrace();
                }


            }
        }
    }

    private void readCSV(File masterDir){
        String[] fileList = masterDir.list();

        for(int i=0; i<fileList.length; ++i){
            if(fileList[i].endsWith(".csv")){

                String csvFile = fileList[i];

                CSVReader reader = null;
                try {
                    reader = new CSVReader(new FileReader(csvFile));
                    String[] line;
                    while ((line = reader.readNext()) != null) {



                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }
    }

    private File initMasterBackup(){
        File previousFolder = masterBackup.retrievedLatestBatchFolder(this.getClass());

        boolean readExistingFolder = true;
        if(previousFolder == null){
            readExistingFolder = false;
        }
        else{
            if(!checkDownloadCompleteMark(previousFolder))
                readExistingFolder = false;
        }

        if(readExistingFolder)
            return masterBackup.retrievedLatestBatchFolder(this.getClass());
        else {
            File newFolder = masterBackup.getCurrentBatchFolder(this.getClass());
            return newFolder;
        }
    }

    private boolean checkDownloadCompleteMark(File file){
        try {
            String completeMarkPath = file.getAbsolutePath() + File.separator + ".download.complete";
            File completeMark = new File(completeMarkPath);

            return completeMark.exists();
        }
        catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    private void createDownloadCompleteMark(File file){
        try {
            String completeMarkPath = file.getAbsolutePath() + File.separator + ".download.complete";
            File completeMark = new File(completeMarkPath);

            completeMark.createNewFile();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private boolean checkUnzipCompleteMark(File file){
        try {
            String completeMarkPath = file.getAbsolutePath() + File.separator + ".unzip.complete";
            File completeMark = new File(completeMarkPath);

            return completeMark.exists();
        }
        catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    private void createUnzipCompleteMark(File file){
        try {
            String completeMarkPath = file.getAbsolutePath() + File.separator + ".unzip.complete";
            File completeMark = new File(completeMarkPath);

            completeMark.createNewFile();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private boolean checkReadCompleteMark(File file){
        try {
            String completeMarkPath = file.getAbsolutePath() + File.separator + ".read.complete";
            File completeMark = new File(completeMarkPath);

            return completeMark.exists();
        }
        catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    private void createReadCompleteMark(File file){
        try {
            String completeMarkPath = file.getAbsolutePath() + File.separator + ".read.complete";
            File completeMark = new File(completeMarkPath);

            completeMark.createNewFile();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private class ResultPair{


        Symbol symbol;
        DailyPrice dailyPrice;
    }
}
