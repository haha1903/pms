package com.datayes.invest.pms.entity.security;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;
import org.joda.time.LocalDate;

import com.datayes.invest.pms.entity.EntityBase;

@Entity
@Table(name = "price_volume")
@Proxy(lazy = false)
@SuppressWarnings({ "unused", "serial" })
public class PriceVolume extends EntityBase implements Serializable {

    private Long securityId;

    private LocalDate tradeDate;

    private Double priceOpen;

    private Double priceClose;

    private Double priceHigh;

    private Double priceLow;

    private Double pricePreviousClose;

    private PriceVolume() {
        // used by persistence
    }

    public PriceVolume(Long securityId, LocalDate tradeDate, Double priceClose, Double pricePreviousClose) {
        this.securityId = securityId;
        this.tradeDate = tradeDate;
        this.priceClose = priceClose;
        this.pricePreviousClose = pricePreviousClose;
    }

    @Id
    @Column(name = "SECURITY_ID")
    public Long getSecurityId() {
        return securityId;
    }

    private void setSecurityId(Long securityId) {
        this.securityId = securityId;
    }


    @Id
    @Column(name = "TRADE_DATE")
    public LocalDate getTradeDate() {
        return tradeDate;
    }

    private void setTradeDate(LocalDate tradeDate) {
        this.tradeDate = tradeDate;
    }

    @Column(name = "PRICE_OPEN")
    public Double getPriceOpen() {
        return priceOpen;
    }

    private void setPriceOpen(Double priceOpen) {
        this.priceOpen = priceOpen;
    }

    @Column(name = "PRICE_CLOSE")
    public Double getPriceClose() {
        return priceClose;
    }

    private void setPriceClose(Double priceClose) {
        this.priceClose = priceClose;
    }

    @Column(name = "PRICE_HIGH")
    public Double getPriceHigh() {
        return priceHigh;
    }

    private void setPriceHigh(Double priceHigh) {
        this.priceHigh = priceHigh;
    }

    @Column(name = "PRICE_LOW")
    public Double getPriceLow() {
        return priceLow;
    }

    private void setPriceLow(Double priceLow) {
        this.priceLow = priceLow;
    }

    @Column(name = "PRICE_PREVIOUS_CLOSE")
    public Double getPricePreviousClose() {
        return pricePreviousClose;
    }

    private void setPricePreviousClose(Double pricePreviousClose) {
        this.pricePreviousClose = pricePreviousClose;
    }
}
