package ymlai87416.dataservice.reader;

import com.opencsv.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import ymlai87416.dataservice.domain.TimePoint;
import ymlai87416.dataservice.domain.TimeSeries;
import ymlai87416.dataservice.fetcher.HKExStockSymbolFetcher;
import ymlai87416.dataservice.utilities.Utilities;

import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 16/10/2016.
 */
public class HKExStockOptionIVCSVReader {

    private Logger log = LoggerFactory.getLogger(HKExStockOptionIVCSVReader.class);
    private static String category = "HK Stock Option volatility";
    private static SimpleDateFormat csvDateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public List<TimeSeries> getVolatilityTimeSeriesFromFile(String file){
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
}
