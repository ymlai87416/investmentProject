package ymlai87416.dataservice.service.jpa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ymlai87416.dataservice.domain.DailyPrice;
import ymlai87416.dataservice.domain.TimePoint;
import ymlai87416.dataservice.domain.TimeSeries;
import ymlai87416.dataservice.service.TimePointService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

/**
 * Created by Tom on 12/10/2016.
 */
@Service("jpaTimePointService")
@Repository
@Transactional
public class TimePointServiceImpl implements TimePointService {
    private Logger log = LoggerFactory.getLogger(TimePointServiceImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<TimePoint> getAllTimePoint(TimeSeries symbol) {
        return null;
    }

    @Override
    public List<TimePoint> getTimePointBySymbolAndDateRange(TimeSeries timeSeries, Date startDate, Date endDate) {
        return null;
    }

    @Override
    public List<TimePoint> getTimePointBySymbolListAndDate(List<TimeSeries> timeSeriesList, Date date) {
        return null;
    }

    @Override
    public int deleteTimePoint(TimePoint timePoint) {
        return 0;
    }

    @Override
    public TimePoint saveTimePoint(TimePoint timePoint) {
        return null;
    }

    @Override
    public List<TimePoint> saveTimePointInBatch(List<TimePoint> timePointList) {
        return null;
    }

    @Override
    public int deleteTimePointByTimeSeries(TimeSeries timeSeries) {
        return 0;
    }

    @Override
    public int deleteAllTimePoint() {
        return 0;
    }

    @Override
    public List<DailyPrice> searchTimePoint(TimePoint timePoint) {
        return null;
    }

    @Override
    public java.sql.Date getLastestTimePointDateForSymbol(TimeSeries timeSeries) {
        return null;
    }
}
