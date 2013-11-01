package com.datayes.invest.pms.entity.account;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
@Table(name = "TRANSACTION")
@Inheritance(strategy=InheritanceType.JOINED)
@Proxy(lazy = false)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Transaction extends EntityBase {
    
    private Long id;
    
    private Long accountId;
    
    private Integer transactionSourceId;
    
    private String transactionClassCode;
    
    private Long orderId;
    
    private String sourceTransactionId;
    
    private LocalDateTime sourceTransactionDate;
    
    private String transactionStatus;
    
    private LocalDateTime statusChangeDate;
    
    protected Transaction() {
     // for hibernate
    }
    
    protected Transaction(Long _accountId, Integer _transactionSourceId, String _transactionClassCode) {
        this.accountId = _accountId;
        this.transactionSourceId = _transactionSourceId;
        this.transactionClassCode = _transactionClassCode;
    }
    
    public Transaction(Long _accountId, Integer _transactionSourceId, String _transactionClassCode, 
                    Long _orderId, String _sourceTransactionId, LocalDateTime _sourceTransactionDate, 
                    String _transactionStatus, LocalDateTime _statusChangeDate) {
        this.accountId = _accountId;
        this.transactionSourceId = _transactionSourceId;
        this.transactionClassCode = _transactionClassCode;
        this.orderId = _orderId;
        this.sourceTransactionId = _sourceTransactionId;
        this.sourceTransactionDate = _sourceTransactionDate;
        this.transactionStatus = _transactionStatus;
        this.statusChangeDate = _statusChangeDate;
    }

    @Id
    @Column(name = "TRANSACTION_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "ACCOUNT_ID")
    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    @Column(name = "TRAN_SOURCE_ID")
    public int getTransactionSourceId() {
        return transactionSourceId;
    }

    public void setTransactionSourceId(int transactionSourceId) {
        this.transactionSourceId = transactionSourceId;
    }

    @Column(name = "TRANSACTION_CLASS_CD")
    public String getTransactionClassCode() {
        return transactionClassCode;
    }

    public void setTransactionClassCode(String transactionClassCode) {
        this.transactionClassCode = transactionClassCode;
    }

    @Column(name = "ORDER_ID")
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    @Column(name = "SOURCE_TRAN_ID")
    public String getSourceTransactionId() {
        return sourceTransactionId;
    }

    public void setSourceTransactionId(String sourceTransactionId) {
        this.sourceTransactionId = sourceTransactionId;
    }

    @Column(name = "SOURCE_TRAN_DT")
    public LocalDateTime getSourceTransactionDate() {
        return sourceTransactionDate;
    }

    public void setSourceTransactionDate(LocalDateTime sourceTransactionDate) {
        this.sourceTransactionDate = sourceTransactionDate;
    }

    @Column(name = "TRAN_STATUS")
    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    @Column(name = "STATUS_CHANGE_DATE")
    public LocalDateTime getStatusChangeDate() {
        return statusChangeDate;
    }

    public void setStatusChangeDate(LocalDateTime statusChangeDate) {
        this.statusChangeDate = statusChangeDate;
    }

}
