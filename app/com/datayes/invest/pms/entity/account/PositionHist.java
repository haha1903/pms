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
@Table(name = "POSITION_HIST")
@Proxy(lazy = false)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PositionHist extends EntityBase implements Serializable {

    private PK pk;

    private BigDecimal quantity;
    
    private BigDecimal settleQty;
    
    private LocalDateTime lastUpdate;
    
    @SuppressWarnings("unused")
    private PositionHist() {
        // used by persistence library   
    }
    
    public PositionHist(PK _pk, BigDecimal _quantity, BigDecimal _settleQty) {
        this.pk = _pk;
        this.quantity = _quantity;
        this.settleQty = _settleQty;
    }

    public PositionHist(PK pk, PositionHist positionHist) {
        this.pk = pk;
        this.quantity = positionHist.getQuantity();
        this.settleQty = positionHist.getSettleQty();
    }

    @Id
    public PK getPK() {
        return pk;
    }

    private void setPK(PK pk) {
        this.pk = pk;
    }

    @Column(name = "QUANTITY")
    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    @Column(name = "SETTLE_QTY")
    public BigDecimal getSettleQty() {
        return settleQty;
    }

    public void setSettleQty(BigDecimal settleQty) {
        this.settleQty = settleQty;
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

        private LocalDate asOfDate;
        
		private PK() {
		}

		public PK(Long positionId, LocalDate asOfDate) {
			this.positionId = positionId;
			this.asOfDate = asOfDate;
		}

        @Column(name = "POSITION_ID")
        public Long getPositionId() {
            return positionId;
        }

        private void setPositionId(Long positionId) {
            this.positionId = positionId;
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
