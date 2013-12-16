package com.datayes.invest.pms.service.marketindex;

import com.datayes.invest.pms.util.BeanUtil;

import scala.math.BigDecimal;

public class MarketIndexComponent {

	private Long securityId;
	
	private BigDecimal weight;
	
	private MarketIndexComponent() {
	    // for BeanUtil
	}

	public MarketIndexComponent(Long securityId, BigDecimal weight) {
		this.securityId = securityId;
		this.weight = weight;
	}

	public Long getSecurityId() {
		return securityId;
	}
	
	private void setSecurityId(Long securityId) {
	    this.securityId = securityId;
	}

	public BigDecimal getWeight() {
		return weight;
	}
	
	private void setWeight(BigDecimal weight) {
	    this.weight = weight;
	}
	
	@Override
	public String toString() {
		return BeanUtil.toString(this);
	}
}
