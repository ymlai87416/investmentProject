package com.ymlai87416.stockoption.server.service;

import com.ymlai87416.stockoption.server.domain.*;

import java.util.List;

/**
 * Created by Tom on 12/10/2016.
 */
public interface TimeSeriesService {
    List<TimeSeries> listAllTimeSeries();

    List<TimeSeries> listAllTimeSeriesByCategory(String category);

    List<TimeSeries> searchTimeSeries(TimeSeries timeSeries);

    List<TimeSeries> searchTimeSeries(TimeSeries timeSeries, boolean initChild);

    TimeSeries saveTimeSeries(TimeSeries timeSeries);

    List<TimeSeries> saveTimeSeriesInBatch(List<TimeSeries> timeSeriesList);

    int deleteTimeSeries(TimeSeries timeSeries);

    int deleteAllTimeSeries();

}
