package com.datayes.invest.pms.entity.account;

import com.datayes.invest.pms.util.BeanUtil;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Proxy;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "ORDERS")
@Proxy(lazy = false)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Order {

    private PK pk;

    private Boolean isCurrent;

    private Long basketId;

    private Long accountId;

    private Long securityId;

    private LocalDate asOfDate;

    private LocalDateTime asOfTime;

    private BigDecimal amount;

    private String tradeSideCode;

    private BigDecimal priceLimit;

    private BigDecimal priceGuideline;

    private Boolean stpFlag;

    private String stpAlgorithm;

    private LocalDateTime stpStartTime;

    private LocalDateTime stpEndTime;

    private String stpBrokerCapacity;

    private String orderStatus;

    private LocalDateTime statusChangeDate;

    private String comments;

    public Order() {
    }

    @Id
    public PK getPk() {
        return pk;
    }

    public void setPk(PK pk) {
        this.pk = pk;
    }

    @Column(name = "IS_CURRENT")
    public Boolean getIsCurrent() {
        return isCurrent;
    }

    public void setIsCurrent(Boolean isCurrent) {
        this.isCurrent = isCurrent;
    }

    @Column(name = "ORDER_BASKET_ID")
    public Long getBasketId() {
        return basketId;
    }

    public void setBasketId(Long basketId) {
        this.basketId = basketId;
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

    @Column(name = "AS_OF_DATE")
    public LocalDate getAsOfDate() {
        return asOfDate;
    }

    public void setAsOfDate(LocalDate asOfDate) {
        this.asOfDate = asOfDate;
    }

    @Column(name = "AS_OF_TIME")
    public LocalDateTime getAsOfTime() {
        return asOfTime;
    }

    public void setAsOfTime(LocalDateTime asOfTime) {
        this.asOfTime = asOfTime;
    }

    @Column(name = "AMOUNT")
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Column(name = "TRADE_SIDE_CD")
    public String getTradeSideCode() {
        return tradeSideCode;
    }

    public void setTradeSideCode(String tradeSideCode) {
        this.tradeSideCode = tradeSideCode;
    }

    @Column(name = "PRICE_LIMIT")
    public BigDecimal getPriceLimit() {
        return priceLimit;
    }

    public void setPriceLimit(BigDecimal priceLimit) {
        this.priceLimit = priceLimit;
    }

    @Column(name = "PRICE_GUIDELINE")
    public BigDecimal getPriceGuideline() {
        return priceGuideline;
    }

    public void setPriceGuideline(BigDecimal priceGuideline) {
        this.priceGuideline = priceGuideline;
    }

    @Column(name = "STP_FLAG")
    public Boolean getStpFlag() {
        return stpFlag;
    }

    public void setStpFlag(Boolean stpFlag) {
        this.stpFlag = stpFlag;
    }

    @Column(name = "STP_ALGORITHM")
    public String getStpAlgorithm() {
        return stpAlgorithm;
    }

    public void setStpAlgorithm(String stpAlgorithm) {
        this.stpAlgorithm = stpAlgorithm;
    }

    @Column(name = "STP_START_TIME")
    public LocalDateTime getStpStartTime() {
        return stpStartTime;
    }

    public void setStpStartTime(LocalDateTime stpStartTime) {
        this.stpStartTime = stpStartTime;
    }

    @Column(name = "STP_END_TIME")
    public LocalDateTime getStpEndTime() {
        return stpEndTime;
    }

    public void setStpEndTime(LocalDateTime stpEndTime) {
        this.stpEndTime = stpEndTime;
    }

    @Column(name = "STP_BROKER_CAPACITY")
    public String getStpBrokerCapacity() {
        return stpBrokerCapacity;
    }

    public void setStpBrokerCapacity(String stpBrokerCapacity) {
        this.stpBrokerCapacity = stpBrokerCapacity;
    }

    @Column(name = "ORDER_STATUS_CD")
    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    @Column(name = "STATUS_CHANGE_DATE")
    public LocalDateTime getStatusChangeDate() {
        return statusChangeDate;
    }

    public void setStatusChangeDate(LocalDateTime statusChangeDate) {
        this.statusChangeDate = statusChangeDate;
    }

    @Column(name = "COMMENTS")
    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Embeddable
    public static class PK implements Serializable {

        private Long id;

        private Long seqNo;

        private PK() {
        }

        public PK(Long id, Long seqNo) {
            this.id = id;
            this.seqNo = seqNo;
        }

        @Column(name = "ORDER_ID")
        public Long getId() {
            return id;
        }

        private void setId(Long id) {
            this.id = id;
        }

        @Column(name = "SEQ_NO")
        public Long getSeqNo() {
            return seqNo;
        }

        private void setSeqNo(Long seqNo) {
            this.seqNo = seqNo;
        }

        @Override
        public boolean equals(Object o) {
            return BeanUtil.equals(this, o);
        }

        @Override
        public int hashCode() {
            return BeanUtil.hashCode(this);
        }

        @Override
        public String toString() {
            return BeanUtil.toString(this);
        }
    }
}