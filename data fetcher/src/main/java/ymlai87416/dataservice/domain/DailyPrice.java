package ymlai87416.dataservice.domain;

import javax.persistence.*;
import java.sql.Date;

/**
 * Created by Tom on 6/10/2016.
 */
public class DailyPrice {
    private long id;
    private long version;
    private DataVendor dataVendor;
    private Symbol symbol;
    private java.sql.Date priceDate;
    private java.sql.Date createdDate;
    private java.sql.Date lastUpdatedDate;
    private double openPrice;
    private double highPrice;
    private double lowPrice;
    private double closePrice;
    private double adjClosePrice;
    private long volume;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Version
    @Column(name = "version")
    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="data_vendor_id")
    public DataVendor getDataVendor() {
        return dataVendor;
    }

    public void setDataVendor(DataVendor dataVendor) {
        this.dataVendor = dataVendor;
    }

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="symbol_id")
    public Symbol getSymbol() {
        return symbol;
    }

    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }

    @Column(name = "price_date")
    public Date getPriceDate() {
        return priceDate;
    }

    public void setPriceDate(Date priceDate) {
        this.priceDate = priceDate;
    }

    @Column(name = "created_date")
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Column(name = "last_updated_date")
    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    @Column(name = "open_price")
    public double getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(double openPrice) {
        this.openPrice = openPrice;
    }

    @Column(name = "high_price")
    public double getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(double highPrice) {
        this.highPrice = highPrice;
    }

    @Column(name = "low_price")
    public double getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(double lowPrice) {
        this.lowPrice = lowPrice;
    }

    @Column(name = "close_price")
    public double getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(double closePrice) {
        this.closePrice = closePrice;
    }

    @Column(name = "adj_close_price")
    public double getAdjClosePrice() {
        return adjClosePrice;
    }

    public void setAdjClosePrice(double adjClosePrice) {
        this.adjClosePrice = adjClosePrice;
    }

    @Column(name = "volume")
    public long getVolume() {
        return volume;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }
}
