package com.datayes.invest.pms.entity.account;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Proxy;

import scala.math.BigDecimal;

import com.datayes.invest.pms.entity.EntityBase;

@Entity
@Table(name = "POSITION_INIT")
@Proxy(lazy = false)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PositionInit extends EntityBase {

    private Long positionId;

    private BigDecimal quantity;

    private BigDecimal carryingValue;

    private PositionInit() {

    }

    public PositionInit (Long positionId, BigDecimal quantity, BigDecimal carryingValue) {
        this.positionId = positionId;
        this.quantity = quantity;
        this.carryingValue = carryingValue;
    }

    @Id
    @Column(name="POSITION_ID")
    public Long getPositionId() {
        return positionId;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    @Column(name="QUANTITY")
    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    @Column(name="CARRYING_VALUE")
    public BigDecimal getCarryingValue() {
        return carryingValue;
    }

    public void setCarryingValue(BigDecimal carryingValue) {
        this.carryingValue = carryingValue;
    }
}
