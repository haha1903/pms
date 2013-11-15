package com.datayes.invest.pms.entity.account;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;
import org.joda.time.LocalDate;

import scala.math.BigDecimal;

import com.datayes.invest.pms.util.BeanUtil;


@Entity
@Table(name = "MARKET_DATA")
@Proxy(lazy = false)
public class MarketData implements Cloneable {

    private Long securityId;
    
    private LocalDate asOfDate;
    
    private Timestamp timestamp;
    
    private BigDecimal price;
    
    private BigDecimal previousPrice;
    
    private Timestamp receivedTime;

    private Timestamp lastUpdate;

    private String source = null;
    
    private MarketData() {
    }

    public MarketData(Long securityId,
                      LocalDate asOfDate,
                      Timestamp timestamp,
                      BigDecimal price,
                      BigDecimal previousPrice,
                      Timestamp receivedTime,
                      String source) {
        this.securityId = securityId;
        this.asOfDate = asOfDate;
        this.timestamp = timestamp;
        this.price = price;
        this.previousPrice = previousPrice;
        this.receivedTime = receivedTime;
        this.source = source;
    }

    @Id
    @Column(name = "SECURITY_ID")
    public Long getSecurityId() {
        return securityId;
    }

    public void setSecurityId(Long securityId) {
        this.securityId = securityId;
    }
    
    @Column(name = "AS_OF_DATE")
    public LocalDate getAsOfDate() {
        return asOfDate;
    }
    
    public void setAsOfDate(LocalDate asOfDate) {
        this.asOfDate = asOfDate;
    }

    @Column(name = "TIMESTAMP")
    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Column(name = "PRICE")
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Column(name = "PREVIOUS_PRICE")
    public BigDecimal getPreviousPrice() {
        return previousPrice;
    }

    public void setPreviousPrice(BigDecimal previousPrice) {
        this.previousPrice = previousPrice;
    }

    @Column(name = "LAST_UPDATE")
    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Column(name = "RECEIVED_TIME")
    public Timestamp getReceivedTime() {
        return receivedTime;
    }

    public void setReceivedTime(Timestamp receivedTime) {
        this.receivedTime = receivedTime;
    }

    @Column(name = "SOURCE")
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
    
    @Override
    public boolean equals(Object o) {
        return BeanUtil.equals(this, o);
    }
    
    @Override
    public String toString() {
        return BeanUtil.toString(this);
    }

    @Override
    public MarketData clone() {
        return BeanUtil.clone(this);
    }
}
