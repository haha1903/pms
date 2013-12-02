package com.datayes.invest.pms.entity.account;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import scala.math.BigDecimal;

@Entity
@Table(name = "SEC_TRANSACTION")
@PrimaryKeyJoinColumn(name = "TRANSACTION_ID")
@Proxy(lazy = false)
public class SecurityTransaction extends Transaction {

    private Long securityId;

    private BigDecimal amount;

    private BigDecimal avgPrice;

    private String settleCurrCode;

    private Long traderId;

    private Long brokerId;

    private String tradeSideCode;

    private BigDecimal fxRate2;

    private BigDecimal commissions;

    private BigDecimal fees;

    private LocalDateTime executionDate;

    private LocalDate settlementDate;
    
    private LocalDate returnDate;
    
    private Integer assetClassId;
    
    private BigDecimal interest;

    private String reason;

    @SuppressWarnings("unused")
    private SecurityTransaction() {
        // for hibernate
    }
    
    public SecurityTransaction(Long _accountId, LocalDate _asOfDate, Integer _transactionSourceId, String _transactionClassCode,
                    Long _securityId, String _tradeSideCode) {
        super(_accountId, _asOfDate, _transactionSourceId, _transactionClassCode);
        this.securityId = _securityId;
        this.tradeSideCode = _tradeSideCode;
    }

    public SecurityTransaction(Long _accountId, LocalDate _asOfDate, Integer _transactionSourceId,
                    String _transactionClassCode, Long _orderId, String _sourceTransactionId,
                    LocalDateTime _sourceTransactionDate, String _transactionStatus, LocalDateTime _statusChangeDate,
                    Long _securityId, BigDecimal _amount, BigDecimal _avgPrice, String _settleCurrCode,
                    Long _traderId, Long _brokerId, String _tradeSideCode, BigDecimal _fxRate2, BigDecimal _commissions,
                    BigDecimal _fees, LocalDateTime _executionDate, LocalDate _settlementDate, String _reason) {
        super(_accountId, _asOfDate, _transactionSourceId, _transactionClassCode, _orderId, _sourceTransactionId,
                _sourceTransactionDate, _transactionStatus, _statusChangeDate);
        this.securityId = _securityId;
        this.amount = _amount;
        this.avgPrice = _avgPrice;
        this.settleCurrCode = _settleCurrCode;
        this.traderId = _traderId;
        this.brokerId = _brokerId;
        this.tradeSideCode = _tradeSideCode;
        this.fxRate2 = _fxRate2;
        this.commissions = _commissions;
        this.fees = _fees;
        this.executionDate = _executionDate;
        this.settlementDate = _settlementDate;
        this.reason = _reason;
    }

    @Column(name = "SECURITY_ID")
    public Long getSecurityId() {
        return securityId;
    }

    public void setSecurityId(Long securityId) {
        this.securityId = securityId;
    }

    @Column(name = "AMOUNT")
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Column(name = "AVG_PRICE")
    public BigDecimal getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(BigDecimal avgPrice) {
        this.avgPrice = avgPrice;
    }

    @Column(name = "SETTLE_CURR_CD")
    public String getSettleCurrCode() {
        return settleCurrCode;
    }

    public void setSettleCurrCode(String settleCurrCode) {
        this.settleCurrCode = settleCurrCode;
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

    @Column(name = "TRADE_SIDE_CD")
    public String getTradeSideCode() {
        return tradeSideCode;
    }

    public void setTradeSideCode(String tradeSideCode) {
        this.tradeSideCode = tradeSideCode;
    }

    @Column(name = "FX_RATE2")
    public BigDecimal getFxRate2() {
        return fxRate2;
    }

    public void setFxRate2(BigDecimal fxRate2) {
        this.fxRate2 = fxRate2;
    }

    @Column(name = "COMMISSIONS")
    public BigDecimal getCommissions() {
        return commissions;
    }

    public void setCommissions(BigDecimal commissions) {
        this.commissions = commissions;
    }

    @Column(name = "FEES")
    public BigDecimal getFees() {
        return fees;
    }

    public void setFees(BigDecimal fees) {
        this.fees = fees;
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

    @Column(name = "TRAN_REASON")
    public String getTransactionReason() {
        return reason;
    }

    public void setTransactionReason(String transactionReason) {
        this.reason = transactionReason;
    }

    @Column(name = "RETURN_DATE")
	public LocalDate getReturnDate() {
		return returnDate;
	}

	public void setReturnDate(LocalDate returnDate) {
		this.returnDate = returnDate;
	}

    @Column(name = "ASSET_CLASS_ID")
	public Integer getAssetClassId() {
		return assetClassId;
	}

	public void setAssetClassId(Integer assetClassId) {
		this.assetClassId = assetClassId;
	}

	@Column(name = "INTEREST")
	public BigDecimal getInterest() {
		return interest;
	}

	public void setInterest(BigDecimal interest) {
		this.interest = interest;
	}
	
}
