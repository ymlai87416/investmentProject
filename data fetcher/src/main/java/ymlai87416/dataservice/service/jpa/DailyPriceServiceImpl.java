package ymlai87416.dataservice.service.jpa;

import ymlai87416.dataservice.service.DailyPriceService;

/**
 * Created by Tom on 6/10/2016.
 */
public class DailyPriceServiceImpl implements DailyPriceService{
    public getAllDailyPrice(Symbol symbol);

    public getDailyPriceBySymbolAndDateRange(Symbol symbol, Date startDate, Date endDate);

    public deleteDailyPrice(DailyPrice dailyPrice);

    public saveDailyPrice(DailyPrice dailyPrice);

    public deleteDailyPriceBySymbol(Symbol symbol);

}
