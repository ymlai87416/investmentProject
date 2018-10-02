package com.ymlai87416.stockoption.server.service;

import com.ymlai87416.stockoption.server.domain.*;

import java.util.Date;
import java.util.List;

/**
 * Created by Tom on 12/10/2016.
 */
public interface TimePointService {
    List<TimePoint> getAllTimePoint(TimeSeries timeSeries);

    List<TimePoint> getTimePointByTimeSeriesAndDateRange(TimeSeries timeSeries, Date startDate, Date endDate);

    List<TimePoint> getTimePointByTimeSeriesListAndDate(List<TimeSeries> timeSeriesList, Date date);

    int deleteTimePoint(TimePoint timePoint);

    TimePoint saveTimePoint(TimePoint timePoint);

    List<TimePoint> saveTimePointInBatch(List<TimePoint> timePointList);

    int deleteTimePointByTimeSeries(TimeSeries timeSeries);

    int deleteAllTimePoint();

    List<TimePoint> searchTimePoint(TimePoint timePoint);

    java.sql.Date getLastestTimePointDateForSymbol(TimeSeries timeSeries);
}
