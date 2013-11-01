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

@SuppressWarnings("serial")
@Entity
@Table(name = "CARRYING_VALUE_HIST")
@Proxy(lazy = false)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CarryingValueHist extends EntityBase implements Serializable{

    private PK pk;
    
    private Long accountId;
    
    private BigDecimal valueAmount;
    
    private String currencyCode;
    
    private LocalDateTime adjustTs;
    
    private LocalDateTime lastUpdate;
    
    public CarryingValueHist() {
        
    }

    public CarryingValueHist(PK pk, Long _accountId, BigDecimal _valueAmount, String _currencyCode,
                             LocalDateTime _adjustTs) {
        this.pk = pk;
        this.accountId = _accountId;
        this.valueAmount = _valueAmount;
        this.currencyCode = _currencyCode;
        this.adjustTs = _adjustTs;
    }

    public CarryingValueHist(PK pk, CarryingValueHist carryingValuationHist) {
        this.pk = pk;
        this.accountId = carryingValuationHist.getAccountId();
        this.valueAmount = carryingValuationHist.getValueAmount();
        this.currencyCode = carryingValuationHist.getCurrencyCode();
        this.adjustTs = carryingValuationHist.getAdjustTs();
    }

    @Id
    public PK getPK() {
        return pk;
    }

    private void setPK(PK pk) {
        this.pk = pk;
    }

    @Column(name = "ACCOUNT_ID")
    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    @Column(name = "VALUE_AMOUNT")
    public BigDecimal getValueAmount() {
        return valueAmount;
    }

    public void setValueAmount(BigDecimal valueAmount) {
        this.valueAmount = valueAmount;
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
    
    @Column(name = "LAST_UPDATE")
    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
    
    /*
     * Composite key
     */

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

        @Column(name = "CAR_VAL_TYPE_ID")
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
