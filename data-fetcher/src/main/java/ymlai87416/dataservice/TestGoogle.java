package ymlai87416.dataservice;

import com.github.kevinsawicki.stocks.DateUtils;
import com.github.kevinsawicki.stocks.StockQuoteRequest;

import java.io.IOException;
import java.util.Locale;

/**
 * Created by ymlai on 22/5/2017.
 */
public class TestGoogle {
    public static void main(String[] args) throws IOException{
        Locale.setDefault(new Locale("en", "EN"));
        StockQuoteRequest request = new StockQuoteRequest();
        request.setSymbol("HKG%3A0005");
        request.setStartDate(DateUtils.yearStart());
        request.setEndDate(DateUtils.yearEnd());

        while(request.next())
            System.out.println(request.getDate() + ": " + request.getClose());
    }
}
