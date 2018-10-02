package com.ymlai87416.stockoption.server.model;

import java.util.Date;

public class StockStatistic {

    private Long stockId;
    private Date startDate;
    private Date endDate;
    private Float minPrice;
    private Float maxPrice;
    private Float meanPrice;
    private Float stdPrice;

    public Long getStockId() {
        return stockId;
    }

    public void setStockId(Long stockId) {
        this.stockId = stockId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Float getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Float minPrice) {
        this.minPrice = minPrice;
    }

    public Float getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Float maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Float getMeanPrice() {
        return meanPrice;
    }

    public void setMeanPrice(Float meanPrice) {
        this.meanPrice = meanPrice;
    }

    public Float getStdPrice() {
        return stdPrice;
    }

    public void setStdPrice(Float stdPrice) {
        this.stdPrice = stdPrice;
    }
}
