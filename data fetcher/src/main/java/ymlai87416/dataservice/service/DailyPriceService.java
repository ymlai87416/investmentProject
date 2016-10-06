package ymlai87416.dataservice.service;

import ymlai87416.dataservice.domain.DailyPrice;
import ymlai87416.dataservice.domain.Symbol;

import java.util.Date;
import java.util.List;

/**
 * Created by Tom on 6/10/2016.
 */
public interface DailyPriceService {
    List<DailyPrice> getAllDailyPrice(Symbol symbol);

    List<DailyPrice> getDailyPriceBySymbolAndDateRange(Symbol symbol, Date startDate, Date endDate);

    void deleteDailyPrice(DailyPrice dailyPrice);

    void saveDailyPrice(DailyPrice dailyPrice);

    void deleteDailyPriceBySymbol(Symbol symbol);


}
