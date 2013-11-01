package com.datayes.invest.pms.entity.account;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Proxy;
import org.joda.time.LocalDateTime;

import scala.math.BigDecimal;

@Entity
@Table(name = "CASH_TRANSACTION")
@PrimaryKeyJoinColumn(name = "TRANSACTION_ID")
@Proxy(lazy = false)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CashTransaction extends Transaction {
    
    private String typeCode;
    
    private String methodCode;
    
    private String reasonCode;
    
    private Long partyId;
    
    private Long intAcctId;
    
    private String extAcctCode;
    
    private BigDecimal amount;
    
    private String currency2Code;
    
    private BigDecimal fxRate1;
    
    @SuppressWarnings("unused")
    private CashTransaction() {
        // for hibernate
    }
    
    public CashTransaction(Long _accountId, Integer _transactionSourceId, String _transactionClassCode,
                    String _typeCode, String _methodCode, String _reasonCode) {
        super(_accountId, _transactionSourceId, _transactionClassCode);
        this.typeCode = _typeCode;
        this.methodCode = _methodCode;
        this.reasonCode = _reasonCode;
    }
    
    public CashTransaction(Long _accountId, Integer _transactionSourceId, String _transactionClassCode, 
                    Long _orderId, String _sourceTransactionId, LocalDateTime _sourceTransactionDate, 
                    String _transactionStatus, LocalDateTime _statusChangeDate, 
                    String _typeCode, String _methodCode, String _reasonCode, Long _partyId, 
                    Long _intAcctId, String _extAcctCode, BigDecimal _amount, String _currency2Code, BigDecimal _fxRate1) {
        super(_accountId, _transactionSourceId, "CASH", _orderId, _sourceTransactionId, _sourceTransactionDate,
                        _transactionStatus, _statusChangeDate);
        this.typeCode = _typeCode;
        this.methodCode = _methodCode;
        this.reasonCode = _reasonCode;
        this.partyId = _partyId;
        this.intAcctId = _intAcctId;
        this.extAcctCode = _extAcctCode;
        this.amount = _amount;
        this.currency2Code = _currency2Code;
        this.fxRate1 = _fxRate1;
    }

    @Column(name = "CASH_TRAN_TYPE_CD")
    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    @Column(name = "CASH_TRAN_METH_CD")
    public String getMethodCode() {
        return methodCode;
    }

    public void setMethodCode(String methodCode) {
        this.methodCode = methodCode;
    }

    @Column(name = "CASH_TRAN_REASON_CD")
    public String getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    @Column(name = "PARTY_ID")
    public Long getPartyId() {
        return partyId;
    }

    public void setPartyId(Long partyId) {
        this.partyId = partyId;
    }

    @Column(name = "INT_ACCT_ID")
    public Long getIntAcctId() {
        return intAcctId;
    }

    public void setIntAcctId(Long intAcctId) {
        this.intAcctId = intAcctId;
    }

    @Column(name = "EXT_ACCT_CD")
    public String getExtAcctCode() {
        return extAcctCode;
    }

    public void setExtAcctCode(String extAcctCode) {
        this.extAcctCode = extAcctCode;
    }

    @Column(name = "AMOUNT")
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Column(name = "CURRENCY2_CD")
    public String getCurrency2Code() {
        return currency2Code;
    }

    public void setCurrency2Code(String currency2Code) {
        this.currency2Code = currency2Code;
    }

    @Column(name = "FX_RATE1")
    public BigDecimal getFxRate1() {
        return fxRate1;
    }

    public void setFxRate1(BigDecimal fxRate1) {
        this.fxRate1 = fxRate1;
    }
}
