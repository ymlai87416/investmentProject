package com.ymlai87416.stockoption.server.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class StockOption extends Asset{

    public StockOption(Long id, String ticker, String name) {
        super(id, ticker, name);
    }

    static SimpleDateFormat dateFormat = new SimpleDateFormat("MMMYY");

    public char getOptionType(){
        return ticker.charAt(3);
    }

    public Optional<Float> getStrikePrice(){
        try {
            return Optional.of(ticker.substring(4, ticker.length() - 5)).map(Float::parseFloat);
        }
        catch(Exception ex){
            return Optional.empty();
        }
    }

    public Optional<Date> getDateTime(){
        try {
            Date result = dateFormat.parse(ticker.substring(ticker.length() - 5));
            return Optional.of(result);
        }
        catch(Exception ex){
            return Optional.empty();
        }
    }

}
