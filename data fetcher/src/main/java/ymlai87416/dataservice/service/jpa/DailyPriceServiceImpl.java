package ymlai87416.dataservice.service.jpa;

import ymlai87416.dataservice.domain.DailyPrice;
import ymlai87416.dataservice.domain.Symbol;
import ymlai87416.dataservice.service.DailyPriceService;

import java.util.Date;
import java.util.List;

/**
 * Created by Tom on 6/10/2016.
 */
public class DailyPriceServiceImpl implements DailyPriceService{

    @Override
    public List<DailyPrice> getAllDailyPrice(Symbol symbol) {
        return null;
    }

    @Override
    public List<DailyPrice> getDailyPriceBySymbolAndDateRange(Symbol symbol, Date startDate, Date endDate) {
        return null;
    }

    @Override
    public void deleteDailyPrice(DailyPrice dailyPrice) {

    }

    @Override
    public void saveDailyPrice(DailyPrice dailyPrice) {

    }

    @Override
    public void deleteDailyPriceBySymbol(Symbol symbol) {

    }
}
