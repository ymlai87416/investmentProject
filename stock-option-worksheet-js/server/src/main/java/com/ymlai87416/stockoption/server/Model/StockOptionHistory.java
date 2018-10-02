package com.ymlai87416.stockoption.server.model;

import java.util.Date;

public class StockOptionHistory {

    private Long id;
    private Long stockOptionId;
    private Date priceDate;
    private Float openPrice;
    private Float dailyHigh;
    private Float dailyLow;
    private Float settlePrice;
    private Long openInterest;
    private Float iv;

    public StockOptionHistory(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStockOptionId() {
        return stockOptionId;
    }

    public void setStockOptionId(Long stockOptionId) {
        this.stockOptionId = stockOptionId;
    }

    public Date getPriceDate() {
        return priceDate;
    }

    public void setPriceDate(Date priceDate) {
        this.priceDate = priceDate;
    }

    public Float getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(Float openPrice) {
        this.openPrice = openPrice;
    }

    public Float getDailyHigh() {
        return dailyHigh;
    }

    public void setDailyHigh(Float dailyHigh) {
        this.dailyHigh = dailyHigh;
    }

    public Float getDailyLow() {
        return dailyLow;
    }

    public void setDailyLow(Float dailyLow) {
        this.dailyLow = dailyLow;
    }

    public Float getSettlePrice() {
        return settlePrice;
    }

    public void setSettlePrice(Float settlePrice) {
        this.settlePrice = settlePrice;
    }

    public Long getOpenInterest() {
        return openInterest;
    }

    public void setOpenInterest(Long openInterest) {
        this.openInterest = openInterest;
    }

    public Float getIv() {
        return iv;
    }

    public void setIv(Float iv) {
        this.iv = iv;
    }

}
