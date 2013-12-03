package com.datayes.invest.pms.entity.account;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Proxy;
import org.joda.time.LocalDateTime;

import com.datayes.invest.pms.entity.EntityBase;


@Entity
@Table(name = "POSITION")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="POSITION_CLASS_CD", discriminatorType= DiscriminatorType.STRING)
@Proxy(lazy = false)
@Cache(usage = CacheConcurrencyStrategy.NONE)
public abstract class Position extends EntityBase {

    private Long Id;

    private Long accountId;

    private String positionClassCode;

    private String currencyCode;

    private Long ledgerId;

    private String exchangeCode;

    private LocalDateTime openDate;

    private String positionStatus;

    private LocalDateTime statusChangeDate;
    
    private LocalDateTime lastUpDateTime;

    protected Position() {
        // used by hibernate
    }

    public Position(Long _accountId, String _positionClassCode, Long _ledgerId, String _exchangeCode,
        String _currencyCode, LocalDateTime _openDate, String _positionStatus, LocalDateTime _statusChangeDate) {

        this.accountId = _accountId;
        this.positionClassCode = _positionClassCode;
        this.ledgerId = _ledgerId;
        this.exchangeCode = _exchangeCode;
        this.currencyCode = _currencyCode;
        this.openDate = _openDate;
        this.positionStatus = _positionStatus;
        this.statusChangeDate = _statusChangeDate;
    }

	@Id
	@Column(name = "POSITION_ID")
//	@GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return Id;
    }

    public void setId(Long positionId) {
        this.Id = positionId;
    }

    @Column(name = "ACCOUNT_ID")
    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    @Column(name = "POSITION_CLASS_CD", insertable = false, updatable = false)
    public String getPositionClassCode() {
        return positionClassCode;
    }

    public void setPositionClassCode(String positionClassCode) {
        this.positionClassCode = positionClassCode;
    }

    @Column(name = "CURRENCY_CD")
    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    @Column(name = "LEDGER_ID")
    public Long getLedgerId() {
        return ledgerId;
    }

    public void setLedgerId(Long ledgerId) {
        this.ledgerId = ledgerId;
    }

    @Column(name = "EXCHANGE_CD")
    public String getExchangeCode() {
        return exchangeCode;
    }

    public void setExchangeCode(String exchangeCode) {
        this.exchangeCode = exchangeCode;
    }

    @Column(name = "OPEN_DATE")
    public LocalDateTime getOpenDate() {
        return openDate;
    }

    public void setOpenDate(LocalDateTime openDate) {
        this.openDate = openDate;
    }

    @Column(name = "POSITION_STATUS")
    public String getPositionStatus() {
        return positionStatus;
    }

    public void setPositionStatus(String positionStatus) {
        this.positionStatus = positionStatus;
    }

    @Column(name = "STATUS_CHANGE_DATE")
    public LocalDateTime getStatusChangeDate() {
        return statusChangeDate;
    }

    public void setStatusChangeDate(LocalDateTime statusChangeDate) {
        this.statusChangeDate = statusChangeDate;
    }
    
    @Column(name = "LAST_UPDATE")
    public LocalDateTime getLastUpdate() {
        return lastUpDateTime;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpDateTime = lastUpdate;
    }
}