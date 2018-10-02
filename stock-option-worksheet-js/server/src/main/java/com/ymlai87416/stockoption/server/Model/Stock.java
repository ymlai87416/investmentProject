package com.ymlai87416.stockoption.server.model;

import java.util.Optional;

public class Stock extends Asset{

    public Stock(Long id, String ticker, String name) {
        super(id, ticker, name);
    }

    public Optional<Long> getSehkCode(){
        try{
            return Optional.of(ticker).map(x -> x.replace(".HK", "")).map(Long::parseLong);
        }
        catch(Exception ex){
            return Optional.empty();
        }
    }


    @Override
    public boolean equals(Object other){
        if(other instanceof Stock){
            return ((Stock)other).id == this.id;
        }
        else
            return false;
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
}
