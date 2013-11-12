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
@Table(name = "vw_marketindex")
@Proxy(lazy = false)
public class MarketIndex extends EntityBase {

	private String marketIndex;
	
	private LocalDate endDate;
	
	private Long securityId;
	
	private String tickerSymbol;
	
	private BigDecimal weightedRatio;

    private MarketIndex() {
        // used by persistence library
    }

    @Column(name = "Market_Index")
	public String getMarketIndex() {
		return marketIndex;
	}

	public void setMarketIndex(String marketIndex) {
		this.marketIndex = marketIndex;
	}

	@Column(name = "EndDate")
	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	@Column(name = "security_id")
	public Long getSecurityId() {
		return securityId;
	}

	public void setSecurityId(Long securityId) {
		this.securityId = securityId;
	}

	@Column(name = "Ticker_Symbol")
	public String getTickerSymbol() {
		return tickerSymbol;
	}

	public void setTickerSymbol(String tickerSymbol) {
		this.tickerSymbol = tickerSymbol;
	}

	@Id    // Fake Id
	@Column(name = "WeightedRatio")
	public BigDecimal getWeightedRatio() {
		return weightedRatio;
	}

	public void setWeightedRatio(BigDecimal weightedRatio) {
		this.weightedRatio = weightedRatio;
	}

}
