package com.ymlai87416.stockoption.server.model;

import java.util.Date;

public class IVSeriesTimePoint {

    private Long id;
    private Date date;
    private Float value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }
}