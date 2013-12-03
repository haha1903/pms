package com.datayes.invest.pms.entity.security;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;
import org.joda.time.LocalDate;

import scala.math.BigDecimal;

import com.datayes.invest.pms.entity.EntityBase;

@Entity
@Table(name = "equity_dividend")
@Proxy(lazy = false)
public class EquityDividend extends EntityBase {

    private Long id;
    
    private Long securityId;
    
    private BigDecimal actualCashDivirmb;
    
    private BigDecimal bonusShareRatio;
    
    private BigDecimal tranAddShareRatio;
    
    private LocalDate exDividate;

    private LocalDate rightRegdate;

    @Id
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "security_id")
    public Long getSecurityId() {
        return securityId;
    }

    public void setSecurityId(Long securityId) {
        this.securityId = securityId;
    }

    @Column(name = "actual_cash_divirmb")
    public BigDecimal getActualCashDivirmb() {
        return actualCashDivirmb;
    }

    public void setActualCashDivirmb(BigDecimal actualCashDivirmb) {
        this.actualCashDivirmb = actualCashDivirmb;
    }

    @Column(name = "bonus_share_ratio")
    public BigDecimal getBonusShareRatio() {
        return bonusShareRatio;
    }

    public void setBonusShareRatio(BigDecimal bonusShareRatio) {
        this.bonusShareRatio = bonusShareRatio;
    }

    @Column(name = "tranadd_share_ratio")
    public BigDecimal getTranAddShareRatio() {
        return tranAddShareRatio;
    }

    public void setTranAddShareRatio(BigDecimal tranAddShareRatio) {
        this.tranAddShareRatio = tranAddShareRatio;
    }

    @Column(name = "exdividate")
    public LocalDate getExDividate() {
        return exDividate;
    }

    public void setExDividate(LocalDate exDividate) {
        this.exDividate = exDividate;
    }

    @Column(name = "right_regdate")
    public LocalDate getRightRegdate() {
        return rightRegdate;
    }

    public void setRightRegdate(LocalDate rightRedate) {
        this.rightRegdate = rightRedate;
    }
}
