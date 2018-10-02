package com.ymlai87416.stockoption.server.model;

import java.util.Date;

public class StockHistory {
    private Long id;
    private Long stockId;
    private Date priceDate;
    private Float openPrice;
    private Float dailyHigh;
    private Float dailyLow;
    private Float closePrice;
    private Float adjClosePrice;
    private Long volume;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStockId() {
        return stockId;
    }

    public void setStockId(Long stockId) {
        this.stockId = stockId;
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

    public Float getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(Float closePrice) {
        this.closePrice = closePrice;
    }

    public Float getAdjClosePrice() {
        return adjClosePrice;
    }

    public void setAdjClosePrice(Float adjClosePrice) {
        this.adjClosePrice = adjClosePrice;
    }

    public Long getVolume() {
        return volume;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
    }
}
