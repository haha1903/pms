package com.datayes.invest.pms.entity.account;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Proxy;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import scala.math.BigDecimal;

import com.datayes.invest.pms.entity.EntityBase;
import com.datayes.invest.pms.util.BeanUtil;

@Entity
@Table(name = "POSITION_VALUATION_HIST")
@Proxy(lazy = false)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PositionValuationHist extends EntityBase {

    private PK pk;
    
    private BigDecimal valueAmount;
    
    private BigDecimal marketPrice;
    
    private String currencyCode;
    
    private LocalDateTime adjustTs;
    
    @SuppressWarnings("unused")
    private PositionValuationHist() {
        
    }

    public PositionValuationHist(PK pk, String currencyCode) {
        this.pk = pk;
        this.currencyCode = currencyCode;
    }

    @Id
    public PK getPK() {
        return pk;
    }

    public void setPK(PK pk) {
        this.pk = pk;
    }

    @Column(name = "VALUE_AMOUNT")
    public BigDecimal getValueAmount() {
        return valueAmount;
    }

    public void setValueAmount(BigDecimal valueAmount) {
        this.valueAmount = valueAmount;
    }
    
    @Column(name = "MARKET_PRICE")
    public BigDecimal getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(BigDecimal marketPrice) {
        this.marketPrice = marketPrice;
    }

    @Column(name = "CURRENCY_CD")
    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    @Column(name = "ADJUST_TS")
    public LocalDateTime getAdjustTs() {
        return adjustTs;
    }

    public void setAdjustTs(LocalDateTime adjustTs) {
        this.adjustTs = adjustTs;
    }
    
    @Embeddable
    public static class PK implements Serializable {

        private Long positionId;

        private Long typeId;

        private LocalDate asOfDate;
        
        private PK() {
        }

        public PK(Long positionId, Long typeId, LocalDate asOfDate) {
            this.positionId = positionId;
            this.typeId = typeId;
            this.asOfDate = asOfDate;
        }

        @Column(name = "POSITION_ID")
        public Long getPositionId() {
            return positionId;
        }

        private void setPositionId(Long positionId) {
            this.positionId = positionId;
        }

        @Column(name = "POS_VAL_TYPE_ID")
        public Long getTypeId() {
            return typeId;
        }

        private void setTypeId(Long typeId) {
            this.typeId = typeId;
        }

        @Column(name = "AS_OF_DATE")
        public LocalDate getAsOfDate() {
            return asOfDate;
        }

        private void setAsOfDate(LocalDate asOfDate) {
            this.asOfDate = asOfDate;
        }

        @Override
        public boolean equals(Object obj) {
            return BeanUtil.equals(this, obj);
        }

        @Override
        public int hashCode() {
            return BeanUtil.hashCode(this);
        }
    }
}
