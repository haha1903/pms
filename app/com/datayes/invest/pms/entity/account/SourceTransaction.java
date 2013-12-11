package com.datayes.invest.pms.entity.account;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Proxy;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import scala.math.BigDecimal;

import com.datayes.invest.pms.entity.EntityBase;

@Entity
@Table(name = "SOURCE_TRANSACTION")
@Proxy(lazy = false)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SourceTransaction extends EntityBase {
    
    private Long id;
    
    private Long accountId;
    
    private Long securityId;
    
    private String sourceTransactionId;

    private Long orderId;
    
    private Long traderId;
    
    private Long brokerId;
    
    private LocalDateTime executionDate;
    
    private LocalDate settlementDate;
    
    private String tradeSideCode;
    
    private BigDecimal price;
    
    private BigDecimal amount;
    
    private Integer transactionSourceId;
    
    private String transactionClassCode;
    
    @SuppressWarnings("unused")
    private SourceTransaction() {
        
    }
    
    public SourceTransaction(Long accountId, Long securityId, String sourceTransactionId, Long orderId,
                    Long traderId, Long brokerId, LocalDateTime executionDate, LocalDate settlementDate,
                    String tradeSideCode, BigDecimal price, BigDecimal amount, 
                    Integer transactionSourceId, String transactionClassCode) {
        this.accountId = accountId;
        this.securityId = securityId;
        this.sourceTransactionId = sourceTransactionId;
        this.orderId = orderId;
        this.traderId = traderId;
        this.brokerId = brokerId;
        this.executionDate = executionDate;
        this.settlementDate = settlementDate;
        this.tradeSideCode = tradeSideCode;
        this.price = price;
        this.amount = amount;
        this.transactionSourceId = transactionSourceId;
        this.transactionClassCode = transactionClassCode;
    }

    @Id
    @Column(name = "ID")
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

    @Column(name = "SECURITY_ID")
    public Long getSecurityId() {
        return securityId;
    }

    public void setSecurityId(Long securityId) {
        this.securityId = securityId;
    }

    @Column(name = "SOURCE_TRANSACTION_ID")
    public String getSourceTransactionId() {
        return sourceTransactionId;
    }

    public void setSourceTransactionId(String sourceTransactionId) {
        this.sourceTransactionId = sourceTransactionId;
    }

    @Column(name = "ORDER_ID")
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    @Column(name = "TRADER_ID")
    public Long getTraderId() {
        return traderId;
    }

    public void setTraderId(Long traderId) {
        this.traderId = traderId;
    }

    @Column(name = "BROKER_ID")
    public Long getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(Long brokerId) {
        this.brokerId = brokerId;
    }

    @Column(name = "EXECUTION_DATE")
    public LocalDateTime getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(LocalDateTime executionDate) {
        this.executionDate = executionDate;
    }

    @Column(name = "SETTLEMENT_DATE")
    public LocalDate getSettlementDate() {
        return settlementDate;
    }

    public void setSettlementDate(LocalDate settlementDate) {
        this.settlementDate = settlementDate;
    }

    @Column(name = "TRADE_SIDE_CD")
    public String getTradeSideCode() {
        return tradeSideCode;
    }

    public void setTradeSideCode(String tradeSideCode) {
        this.tradeSideCode = tradeSideCode;
    }

    @Column(name = "PRICE")
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Column(name = "AMOUNT")
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Column(name = "TRANSACTION_SOURCE_ID")
    public Integer getTransactionSourceId() {
        return transactionSourceId;
    }

    public void setTransactionSourceId(Integer transactionSourceId) {
        this.transactionSourceId = transactionSourceId;
    }

    @Column(name = "TRANSACTION_CLASS_CD")
    public String getTransactionClassCode() {
        return transactionClassCode;
    }

    public void setTransactionClassCode(String transactionClassCode) {
        this.transactionClassCode = transactionClassCode;
    }
}
