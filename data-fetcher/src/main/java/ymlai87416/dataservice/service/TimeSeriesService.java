package ymlai87416.dataservice.service;

import ymlai87416.dataservice.domain.Exchange;
import ymlai87416.dataservice.domain.Symbol;
import ymlai87416.dataservice.domain.TimeSeries;

import java.util.List;

/**
 * Created by Tom on 12/10/2016.
 */
public interface TimeSeriesService {
    List<TimeSeries> listAllTimeSeries();

    List<TimeSeries> listAllTimeSeriesByCategory(String category);

    List<TimeSeries> searchSymbol(TimeSeries timeSeries);

    TimeSeries saveTimeSeries(TimeSeries timeSeries);

    List<TimeSeries> saveTimeSeriesInBatch(List<TimeSeries> timeSeriesList);

    int deleteTimeSeries(TimeSeries timeSeries);

    int deleteAllTimeSeries();
}
