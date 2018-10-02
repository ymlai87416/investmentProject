package com.ymlai87416.stockoption.server.service;

import com.ymlai87416.stockoption.server.domain.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Tom on 6/10/2016.
 */
public interface DailyPriceService {
    List<DailyPrice> getAllDailyPrice(Symbol symbol);

    List<DailyPrice> getDailyPriceBySymbolAndDateRange(Symbol symbol, Date startDate, Date endDate);

    List<DailyPrice> getDailyPriceBySymbolListAndDate(List<Symbol> symbolList, Date date);

    int deleteDailyPrice(DailyPrice dailyPrice);

    DailyPrice saveDailyPrice(DailyPrice dailyPrice);

    List<DailyPrice> saveDailyPriceInBatch(List<DailyPrice> dailyPriceList);

    int deleteDailyPriceBySymbol(Symbol symbol);

    int deleteAllDailyPrice();

    List<DailyPrice> searchDailyPrice(DailyPrice dailyPrice);

    java.sql.Date getLastestDailyPriceDateForSymbol(Symbol symbol);
}
