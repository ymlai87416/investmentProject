package ymlai87416.dataservice.fetcher;

import com.opencsv.CSVReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import ymlai87416.dataservice.Utilities.Utilities;
import ymlai87416.dataservice.domain.TimePoint;
import ymlai87416.dataservice.domain.TimeSeries;
import ymlai87416.dataservice.fetcher.constant.FileEncoding;
import ymlai87416.dataservice.service.MasterBackup;
import ymlai87416.dataservice.service.TimePointService;
import ymlai87416.dataservice.service.TimeSeriesService;

import javax.rmi.CORBA.Util;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Tom on 11/10/2016.
 */
@Component("HKExStockOptionIVFetcher")
public class HKExStockOptionIVFetcher implements Fetcher {
    private static String url = "http://www.hkex.com.hk/eng/sorc/options/statistics_hv_iv.aspx";
    private static String fileDownloadUrlFormat = "http://www.hkex.com.hk/eng/sorc/options/statistics_hv_iv.aspx?action=csv&type=3&ucode=%s";
    private static String fileName = "index.html";
    private static String urlEncoding = "UTF-8";
    private static String category = "HK Stock Option volatility";
    private static SimpleDateFormat csvDateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private Logger log = LoggerFactory.getLogger(HKExStockOptionIVFetcher.class);

    @Autowired
    TimeSeriesService timeSeriesService;

    @Autowired
    TimePointService timePointService;

    @Autowired
    MasterBackup masterBackup;

    @Override
    public boolean run() {
        File masterDir = initMasterBackup();

        try{

            if(!checkCompleteMark(masterDir)){
                downloadFileToMasterBackup(url, masterDir.getAbsolutePath() + File.separator + fileName);

                File input = new File(masterDir.getAbsolutePath() + File.separator + fileName);

                Document doc = Jsoup.parse(input, FileEncoding.defaultFileEncoding, url);
                Map<String, String> stockOptionCodeList = fetchStockOptionIVCode(doc);

                for(Map.Entry<String, String> elem : stockOptionCodeList.entrySet()){
                    String file = downloadIVFiles(masterDir, elem.getKey(), elem.getValue());
                }
                createCompleteMark(masterDir);
            }

            File[] fileList = masterDir.listFiles();

            for(File file : fileList){
                if(file.getName().endsWith(".csv")){
                    List<TimeSeries> volatilitySeriesList = getVolatilityTimeSeriesFromFile(file.getAbsolutePath());

                    for(TimeSeries timeSeries : volatilitySeriesList){
                        List<TimeSeries> timeSeriesInDBSearchResult = timeSeriesService.searchTimeSeries(timeSeries);

                        if(timeSeriesInDBSearchResult == null || timeSeriesInDBSearchResult.size() == 0){
                            timeSeriesService.saveTimeSeries(timeSeries);
                        }
                        else{
                            Assert.isTrue(timeSeriesInDBSearchResult.size() == 1); //only 1 series in DB
                            TimeSeries timeSeriesInDB = timeSeriesInDBSearchResult.get(0);

                            Comparator<TimePoint> timePointComparator = new Comparator<TimePoint>(){
                                @Override
                                public int compare(TimePoint o1, TimePoint o2) {
                                    return o1.getTimePointDate().compareTo(o2.getTimePointDate());
                                }
                            };

                            List<TimePoint> timePointList = timeSeries.getTimePointList();
                            Collections.sort(timePointList,timePointComparator);

                            java.sql.Date startDate = timePointList.get(0).getTimePointDate();
                            java.sql.Date endDate = timePointList.get(timePointList.size()-1).getTimePointDate();

                            List<TimePoint> timePointsInDB = timePointService.getTimePointByTimeSeriesAndDateRange(timeSeriesInDB, startDate, endDate);

                            Collections.sort(timePointsInDB, timePointComparator );

                            int indexDB = 0; int indexWeb;

                            for(indexWeb = 0; indexWeb < timePointList.size(); ++indexWeb){
                                for(; indexDB < timePointsInDB.size(); ++indexDB){
                                    if(timePointList.get(indexWeb).getTimePointDate().compareTo(
                                            timePointsInDB.get(indexDB).getTimePointDate()
                                    ) >= 0)
                                        break;
                                }

                                if(indexDB > timePointsInDB.size()){
                                    timeSeriesInDB.getTimePointList().add(timePointList.get(indexWeb));
                                    timePointList.get(indexWeb).setTimeSeries(timeSeriesInDB);
                                }
                                else{
                                    if(timePointsInDB.get(indexDB).getTimePointDate().compareTo(
                                            timePointList.get(indexWeb).getTimePointDate()
                                    ) == 0){
                                        //update the database record
                                        TimePoint timePointInDB = timePointsInDB.get(indexDB);
                                        TimePoint timePointFromWeb = timePointList.get(indexWeb);

                                        if(timePointInDB.getValue() != timePointFromWeb.getValue()){
                                            timePointInDB.setValue(timePointFromWeb.getValue());
                                            timePointInDB.setLastUpdatedDate(Utilities.getCurrentSQLDate());
                                        }
                                    }
                                }
                            }

                            timeSeriesService.saveTimeSeries(timeSeriesInDB);
                        }

                    }
                }
            }

        }
        catch(Exception ex){
            ex.printStackTrace();
        }

        return false;
    }

    private Map<String, String> fetchStockOptionIVCode(Document doc){
        Elements stockCodeOptions = doc.select("option");

        Map<String, String> result = new TreeMap<String, String>();

        for(Element elem : stockCodeOptions){
            String key, value;
            key = elem.text();
            value = elem.attr("value");

            if(key != null && value != null)
                result.put(key, value);
        }

        return result;
    }

    private String downloadIVFiles(File masterDir, String assetName, String code){
        String url = String.format(fileDownloadUrlFormat, code);
        String destination = masterDir.getAbsolutePath() + File.separator + assetName + ".csv";

        try{
            Utilities.downloadWebPageToFile(url, destination, urlEncoding);
        }
        catch(Exception ex){
            ex.printStackTrace();
            return null;
        }

        return destination;
    }

    private List<TimeSeries> getVolatilityTimeSeriesFromFile(String file){
        List<TimeSeries> timeSeriesList = new ArrayList<TimeSeries>();

        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(file));
            String[] line;

            line = reader.readNext();
            String assetName = line[1];

            line = reader.readNext();

            line = reader.readNext();

            Assert.isTrue(line.length > 1);

            for(int i=1 ; i<line.length; ++i){
                TimeSeries timeSeries = new TimeSeries();

                String seriesName = assetName + " " + line[i];
                timeSeries.setSeriesName(seriesName);
                timeSeries.setCategory(category);
                timeSeries.setCreatedDate(Utilities.getCurrentSQLDate());
                timeSeries.setLastUpdatedDate(Utilities.getCurrentSQLDate());

                timeSeriesList.add(timeSeries);
            }

            while ((line = reader.readNext()) != null) {
                try {
                    java.util.Date timePointDate = csvDateFormat.parse(line[0]);
                    java.sql.Date timePointSqlDate = Utilities.convertUtilDateToSqlDate(timePointDate);

                    for(int i=1; i<line.length; ++i){
                        try{
                            Double timePointValue = Double.parseDouble(line[i]);
                            TimePoint timePoint = new TimePoint();

                            TimeSeries timeSeries = timeSeriesList.get(i-1);

                            timePoint.setTimeSeries(timeSeries);
                            timePoint.setTimePointDate(timePointSqlDate);
                            timePoint.setValue(timePointValue);
                            timePoint.setCreatedDate(Utilities.getCurrentSQLDate());
                            timePoint.setLastUpdatedDate(Utilities.getCurrentSQLDate());

                            if(timeSeries.getTimePointList() == null){
                                timeSeries.setTimePointList(new ArrayList<>());
                            }
                            timeSeries.getTimePointList().add(timePoint);
                        }
                        catch(Exception ex){
                            ex.printStackTrace();
                            log.error(String.format("Error when parsing the number: %s. Skipping this time point.", line[i]), ex);
                        }
                    }
                }
                catch(Exception ex) {
                    ex.printStackTrace();
                    log.error(String.format("Error when parsing the following date: %s. Continue with next line.", line[0]), ex);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return timeSeriesList;
    }

    private boolean checkCompleteMark(File file){
        try {
            String completeMarkPath = file.getAbsolutePath() + File.separator + ".complete";
            File completeMark = new File(completeMarkPath);

            return completeMark.exists();
        }
        catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    private void createCompleteMark(File file){
        try {
            String completeMarkPath = file.getAbsolutePath() + File.separator + ".complete";
            File completeMark = new File(completeMarkPath);

            completeMark.createNewFile();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private File initMasterBackup(){
        File previousFolder = masterBackup.retrievedLatestBatchFolder(this.getClass());

        boolean readExistingFolder = true;
        if(previousFolder == null){
            readExistingFolder = false;
        }
        else{
            if(!checkCompleteMark(previousFolder))
                readExistingFolder = false;
        }

        if(readExistingFolder)
            return masterBackup.retrievedLatestBatchFolder(this.getClass());
        else {
            File newFolder = masterBackup.getCurrentBatchFolder(this.getClass());
            return newFolder;
        }
    }

    private void downloadFileToMasterBackup(String url, String destination) {
        try {
            Utilities.downloadWebPageToFile(url, destination);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
