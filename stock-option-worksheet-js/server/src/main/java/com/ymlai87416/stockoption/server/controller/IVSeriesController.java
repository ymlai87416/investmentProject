package com.ymlai87416.stockoption.server.controller;

import com.ymlai87416.stockoption.server.model.IVSeries;
import com.ymlai87416.stockoption.server.model.Stock;
import com.ymlai87416.stockoption.server.model.StockOption;
import com.ymlai87416.stockoption.server.model.StockStatistic;
import com.ymlai87416.stockoption.server.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
public class IVSeriesController {

    /**
     * findBySEHKCode                       : /ivseries/{id}
     * findBySEHKCodeWithTimePoint          : /ivseries/{id}?history=1?startDate=yyyymmdd&endDate=yyyymmdd
     * sehk2IvName                          : /ivseries/list
     */

    private TimeSeriesService timeSeriesService;
    private TimePointService timePointService;

    @Autowired
    private IVSeriesController(TimeSeriesService timeSeriesService,
                               TimePointService timePointService
    ){
        this.timeSeriesService = timeSeriesService;
        this.timePointService = timePointService;
    }

    @RequestMapping("/ivseries/{id}")
    @CrossOrigin(origins="http://localhost:4200")
    public List<IVSeries> findStockBySEHKCode(@PathVariable String id)
    {
        return null;
    }

    @RequestMapping("/ivseries/list")
    @CrossOrigin(origins="http://localhost:4200")
    public List<Mapping> getStockStatistic(@PathVariable String id)
    {
        return null;
    }
}
