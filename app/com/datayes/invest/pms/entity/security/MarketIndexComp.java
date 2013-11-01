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
@Table(name = "market_index_comp")
@Proxy(lazy = false)
@SuppressWarnings("unused")
public class MarketIndexComp extends EntityBase {

    private Long marketIndexId;

    private Long securityId;

    private LocalDate endDate;

    private MarketIndexComp() {
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

    /* TODO comment this out as it does not work */

    @Column(name = "COMP_END_DATE")
    public LocalDate getEndDate() {
        return endDate;
    }

    private void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
