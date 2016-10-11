package ymlai87416.dataservice.domain;

import javax.persistence.*;
import java.sql.Date;

/**
 * Created by Tom on 12/10/2016.
 */
@Entity
@Table(name = "time_point")
public class TimePoint {
    private Long id;
    private long version;
    private long seriesId;
    private java.sql.Date timePointDate;
    private double value;
    private java.sql.Date createdDate;
    private java.sql.Date updatedDate;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
    @JoinColumn(name="series_id")
    public long getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(long seriesId) {
        this.seriesId = seriesId;
    }

    @Column(name = "time_point_date")
    public Date getTimePointDate() {
        return timePointDate;
    }

    public void setTimePointDate(Date timePointDate) {
        this.timePointDate = timePointDate;
    }

    @Column(name = "value")
    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Column(name = "created_date")
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Column(name = "last_updated_date")
    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }
}
