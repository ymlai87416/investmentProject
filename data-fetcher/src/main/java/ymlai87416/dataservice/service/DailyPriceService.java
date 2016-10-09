package ymlai87416.dataservice.service;

import ymlai87416.dataservice.domain.DailyPrice;
import ymlai87416.dataservice.domain.Symbol;

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
}
