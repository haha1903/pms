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
@Table(name = "market_index_weight")
@Proxy(lazy = false)
@SuppressWarnings("unused")
public class MarketIndexWeight extends EntityBase {

    private Long marketIndexId;

    private Long securityId;

    private LocalDate effectiveDate;

    private Double weight;

    private MarketIndexWeight() {
        // used by persistence library
    }


    @Id
    @Column(name = "MARKET_INDEX_ID")
    public Long getMarketIndexId() {
        return marketIndexId;
    }

    private void setMarketIndexId(Long marketIndexId) {
        this.marketIndexId = marketIndexId;
    }

    @Column(name = "SECURITY_ID")
    public Long getSecurityId() {
        return securityId;
    }

    private void setSecurityId(Long securityId) {
        this.securityId = securityId;
    }

    @Column(name = "WEIGHT_EFFECT_DATE")
    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    private void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    @Column(name = "COMP_WEIGHT")
    public Double getWeight() {
        return weight;
    }

    private void setWeight(Double weight) {
        this.weight = weight;
    }
}
