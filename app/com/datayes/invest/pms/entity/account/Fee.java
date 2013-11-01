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
import org.joda.time.LocalDateTime;

import scala.math.BigDecimal;

import com.datayes.invest.pms.entity.EntityBase;

@Entity
@Table(name = "FEE")
@Proxy(lazy = false)
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Fee extends EntityBase {

	private Long id;

	private Long accountId;

	private Long rateTypeId;

	private LocalDateTime startDate;

	private LocalDateTime endDate;

	private String tradeSideCode;

	private Long securityId;

	private BigDecimal rates;

	private String remark;

	public Fee(Long _accountId, Long _rateTypeId, LocalDateTime _startDate, LocalDateTime _endDate,
			String _tradeSideCode, Long _securityId, BigDecimal _rates, String _remark) {
		this.accountId = _accountId;
		this.rateTypeId = _rateTypeId;
		this.startDate = _startDate;
		this.endDate = _endDate;
		this.tradeSideCode = _tradeSideCode;
		this.securityId = _securityId;
		this.rates = _rates;
		this.remark = _remark;
	}

	@SuppressWarnings("unused")
	private Fee() {

	}

	@Id
	@Column(name = "FEE_ID")
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

	@Column(name = "RATE_TYPE_ID")
	public Long getRateTypeId() {
		return rateTypeId;
	}

	public void setRateTypeId(Long rateTypeId) {
		this.rateTypeId = rateTypeId;
	}

	@Column(name = "START_DATE")
	public LocalDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}

	@Column(name = "END_DATE")
	public LocalDateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}

	@Column(name = "TRADE_SIDE_CD")
	public String getTradeSideCode() {
		return tradeSideCode;
	}

	public void setTradeSideCode(String tradeSideCode) {
		this.tradeSideCode = tradeSideCode;
	}

	@Column(name = "RATES")
	public BigDecimal getRates() {
		return rates;
	}

	public void setRates(BigDecimal rates) {
		this.rates = rates;
	}

	@Column(name = "REMARK")
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Column(name = "SECURITY_ID")
	public Long getSecurityId() {
		return securityId;
	}

	public void setSecurityId(Long securityId) {
		this.securityId = securityId;
	}

}
