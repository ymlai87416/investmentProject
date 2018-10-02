package com.ymlai87416.stockoption.server.controller;

import com.ymlai87416.stockoption.server.domain.DailyPrice;
import com.ymlai87416.stockoption.server.domain.StockOptionUnderlyingAsset;
import com.ymlai87416.stockoption.server.domain.Symbol;
import com.ymlai87416.stockoption.server.model.StockOption;
import com.ymlai87416.stockoption.server.model.StockOptionHistory;
import com.ymlai87416.stockoption.server.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class StockOptionController {

    /**
     * findBySEHKCode                       : /stockOption/sehk/{id}
     * findBySEHKCodeWithHistory            : /stockOption/sehk/{id}?history=1
     * findByOptionCodeWithHistory          : /stockOption/code/{id}?history=1
     * findAvailableDateBySEHKCode          : /stockOption/sehk/{id}/listDate
     * findLatestAvailableDateBySEHKCode    : /stockOption/sehk/{id}/listDate?latest=1
     * getAllStockOption                    : /stockOption
     * getAllStockOptionUnderlyingAsset     : /stockOption/underlyingAsset
     */

    private SymbolService symbolService;
    private DailyPriceService dailyPriceService;
    private StockOptionUnderlyingAssetService stockOptionUnderlyingAssetService;
    List<StockOptionUnderlyingAsset> underlyingAssetsList;

    @Autowired
    private StockOptionController(DailyPriceService dailyPriceService,
                                 SymbolService symbolService, StockOptionUnderlyingAssetService stockOptionUnderlyingAssetService
    ){
        this.dailyPriceService = dailyPriceService;
        this.symbolService = symbolService;
        this.stockOptionUnderlyingAssetService = stockOptionUnderlyingAssetService;
        underlyingAssetsList = this.stockOptionUnderlyingAssetService.listAllStockOptionUnderlyingAsset();
    }

    private boolean tickerMatch(String tickerStr, int tickerNum){
        try{
            int parseInt = Integer.parseInt(tickerStr.replace(".HK", ""));
            return parseInt == tickerNum;
        }
        catch(Exception ex){
            return false;
        }
    }

    @RequestMapping("/stockOption/sehk/{id}")
    @CrossOrigin(origins="http://localhost:4200")
    public List<StockOption> findStockOptionBySEHKCode(@PathVariable String id) throws Exception
    {
        try {
            int tickerNum = Integer.parseInt(id);
            Optional<StockOptionUnderlyingAsset> asset = underlyingAssetsList.stream().filter(x -> tickerMatch(x.getTicker(), tickerNum)).findFirst();

            if(asset.isPresent()) {
                Symbol example = new Symbol();
                example.setInstrument("HK Stock Option");
                example.setTicker(asset.get().getShortForm() + "%");
                return symbolService.searchSymbol(example, true);
            }
            else
                return Collections.emptyList();
        }
        catch(Exception ex){
            throw new Exception("Invalid SEHK code.");
        }
    }

    @RequestMapping("/stockOption/code/{id}")
    @CrossOrigin(origins="http://localhost:4200")
    public List<StockOption> findStockOptionByOptionCode(@PathVariable String id)
    {
        return null;
    }

    @RequestMapping("/stockOption/sehk/{id}/listDate")
    @CrossOrigin(origins="http://localhost:4200")
    public List<Date> findStockOptionDateBySEHKCode(@PathVariable String id)
    {
        return symbolService.searchSymbol();
    }

    @RequestMapping("/stockOption/underlyingAsset")
    @CrossOrigin(origins="http://localhost:4200")
    public List<StockOptionUnderlyingAsset> getAllStockOptionUnderlyingAsset()
    {
        return underlyingAssetsList;
    }

    private List<StockOption> convertToStockOptionList(List<Symbol> symbolList){
        List<StockOption> result = new ArrayList<StockOption>();

        return symbolList.stream().map(x -> convertToStockOption(x)).collect(Collectors.toList());
    }

    private StockOption convertToStockOption(Symbol symbol){
        return new StockOption(symbol.getId(), symbol.getTicker(), symbol.getName());
    }

    private List<StockOptionHistory> convertToStockOptionHistoryList(List<DailyPrice> dailyPriceList){
        List<StockOptionHistory> result = new ArrayList<StockOptionHistory>();

        dailyPriceList.stream().map(x -> )
    }

    private StockOptionHistory convertToStockOptionHistory(StockOptionHistory stockOptionHistory){
        return new StockOption(stockOptionHistory.getId(), stockOptionHistory.getTicker(), stockOptionHistory.getName());
    }

}
