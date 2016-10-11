package ymlai87416.dataservice.service;

import ymlai87416.dataservice.domain.DailyPrice;
import ymlai87416.dataservice.domain.Symbol;
import ymlai87416.dataservice.domain.TimePoint;
import ymlai87416.dataservice.domain.TimeSeries;

import java.util.Date;
import java.util.List;

/**
 * Created by Tom on 12/10/2016.
 */
public interface TimePointService {
    List<TimePoint> getAllTimePoint(TimeSeries symbol);

    List<TimePoint> getTimePointBySymbolAndDateRange(TimeSeries timeSeries, Date startDate, Date endDate);

    List<TimePoint> getTimePointBySymbolListAndDate(List<TimeSeries> timeSeriesList, Date date);

    int deleteTimePoint(TimePoint timePoint);

    TimePoint saveTimePoint(TimePoint timePoint);

    List<TimePoint> saveTimePointInBatch(List<TimePoint> timePointList);

    int deleteTimePointByTimeSeries(TimeSeries timeSeries);

    int deleteAllTimePoint();

    List<DailyPrice> searchTimePoint(TimePoint timePoint);

    java.sql.Date getLastestTimePointDateForSymbol(TimeSeries timeSeries);
}
