package com.ymlai87416.stockoption.server.model;

public class StockOptionMapping {
    private int sehkCode;
    private String shortName;
    private String fullName;

    public StockOptionMapping(){
        
    }

    public int getSehkCode() {
        return sehkCode;
    }

    public void setSehkCode(int sehkCode) {
        this.sehkCode = sehkCode;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
