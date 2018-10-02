package com.ymlai87416.stockoption.server.controller;

import com.ymlai87416.stockoption.server.model.Stock;
import com.ymlai87416.stockoption.server.model.StockOption;
import com.ymlai87416.stockoption.server.model.StockStatistic;
import com.ymlai87416.stockoption.server.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
public class StockController {

    /**
     * findBySEHKCode                       : /stock/{id}
     * findBySEHKCodeWithHistory            : /stock/{id}?history=1
     * getStockStatistic                    : /stock/{id}/stats?toDate=yyyymmdd
     */

    private SymbolService symbolService;
    private DailyPriceService dailyPriceService;

    @Autowired
    private StockController(DailyPriceService dailyPriceService,
                                  SymbolService symbolService
    ){
        this.dailyPriceService = dailyPriceService;
        this.symbolService = symbolService;
    }

    @RequestMapping("/stock/{id}")
    @CrossOrigin(origins="http://localhost:4200")
    public List<Stock> findStockBySEHKCode(@PathVariable String id)
    {
        return null;
    }

    @RequestMapping("/stock/{id}/stats")
    @CrossOrigin(origins="http://localhost:4200")
    public StockStatistic getStockStatistic(@PathVariable String id)
    {
        return null;
    }
}
