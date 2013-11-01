package com.datayes.invest.pms.entity.security;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;
import org.joda.time.LocalDateTime;

@Entity
@Table(name = "future")
@PrimaryKeyJoinColumn(name="SECURITY_ID")
@Proxy(lazy = false)
public class Future extends Security {
    
    private String currencyCode;
    
    private LocalDateTime deliveryDate;
    
    private String contractMultiplier;
    
    @Column(name = "CURRENCY_CD")
    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    @Column(name = "DELIVERY_DATE")
    public LocalDateTime getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDateTime deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    @Column(name = "CONTRACT_MULTIPLIER")
    public String getContractMultiplier() {
        return contractMultiplier;
    }

    public void setContractMultiplier(String contractMultiplier) {
        this.contractMultiplier = contractMultiplier;
    }
}
