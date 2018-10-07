package com.ymlai87416.stockoption.server.controller;

import com.ymlai87416.stockoption.server.domain.*;
import com.ymlai87416.stockoption.server.model.*;
import com.ymlai87416.stockoption.server.service.*;
import com.ymlai87416.stockoption.server.utilities.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class IVSeriesController {

    /**
     * findBySEHKCode                       : /ivseries/{id}
     * findBySEHKCodeWithTimePoint          : /ivseries/{id}?history=1?startDate=yyyymmdd&endDate=yyyymmdd
     * sehk2IvName                          : /ivseries/list
     */

    private TimeSeriesRepository timeSeriesRepository;
    private TimePointRepository timePointRepository;
    private StockOptionUnderlyingAssetRepository stockOptionUnderlyingAssetRepository;
    List<StockOptionUnderlyingAsset> underlyingAssetsList;

    @Autowired
    private IVSeriesController(TimeSeriesRepository timeSeriesRepository,
                               TimePointRepository timePointRepository,
                               StockOptionUnderlyingAssetRepository stockOptionUnderlyingAssetRepository
    ){
        this.timeSeriesRepository = timeSeriesRepository;
        this.timePointRepository = timePointRepository;
        this.stockOptionUnderlyingAssetRepository = stockOptionUnderlyingAssetRepository;
        underlyingAssetsList = this.stockOptionUnderlyingAssetRepository.findAll();
    }

    @RequestMapping("/ivseries/{id}")
    @CrossOrigin(origins="http://localhost:4200")
    public List<IVSeries> findStockBySEHKCode(@PathVariable String id,
                                              @RequestParam(value="startDate", required=false) String startDate,
                                              @RequestParam(value="endDate", required=false) String endDate)
    {
        List<TimeSeries> searchResult = null;
        List<TimePoint> childSearchResult = null;

        Optional<StockOptionUnderlyingAsset> asset = underlyingAssetsList.stream().filter(x -> x.getTicker().compareToIgnoreCase(id) == 0/*String.valueOf(x.getTicker()).compareTo(id.trim())==0*/).findFirst();

        if (asset.isPresent()) {

            Date[] startEndDate = Utilities.parseStartDateAndEndDate(startDate, endDate);

            boolean initChild = false;
            if (startEndDate != null && startEndDate.length == 2
                    && startEndDate[0] != null && startEndDate[1] != null) {

                searchResult = timeSeriesRepository.findBySeriesNameLikeAndTimePointListTimePointDateBetween(asset.get().getFullName()+"%", startEndDate[0], startEndDate[1]);

                childSearchResult = timePointRepository.findTimePointByTimeSeriesInAndTimePointDateBetween(searchResult,
                        startEndDate[0], startEndDate[1]);

                for (TimeSeries timeSeries : searchResult) {
                    List<TimePoint> timePointList =
                            childSearchResult.stream().filter(x -> x.getTimeSeries().getId() == timeSeries.getId()).collect(Collectors.toList());
                    timeSeries.setTimePointList(timePointList);
                }

                initChild = true;
            } else {
                searchResult = timeSeriesRepository.findBySeriesNameLike(asset.get().getFullName()+"%");
            }

            if (searchResult != null)
                return convertToIVSeries(searchResult, !initChild);
            else
                return Collections.emptyList();
        }
        else
            return Collections.emptyList();
    }

    private List<IVSeries> convertToIVSeries(List<TimeSeries> timeSeriesList, boolean skipChild){
        return timeSeriesList.stream().map(x -> convertToIVSeries(x, skipChild)).collect(Collectors.toList());
    }

    private IVSeries convertToIVSeries(TimeSeries timeSeries, boolean skipChild){
        IVSeries result =  new IVSeries(timeSeries.getId(), timeSeries.getSeriesName());
        if(!skipChild) {
            List<IVSeriesTimePoint> children = convertToIVSeriesTimePointList(timeSeries.getTimePointList());
            result.setTimePointList(children);
        }
        return result;
    }

    private List<IVSeriesTimePoint> convertToIVSeriesTimePointList(List<TimePoint> timePointList){
        return timePointList.stream().map(x -> convertToIVSeriesTimePoint(x)).collect(Collectors.toList());
    }

    private IVSeriesTimePoint convertToIVSeriesTimePoint(TimePoint timePoint){
        return new IVSeriesTimePoint(timePoint.getId(), timePoint.getTimePointDate(), new Float(timePoint.getValue()));
    }

}
