package com.datayes.invest.pms.entity.security;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;
import org.joda.time.LocalDate;

import com.datayes.invest.pms.entity.EntityBase;

@Entity
@Table(name = "exchange_cal")
@Proxy(lazy = false)
public class ExchangeCalendar extends EntityBase {
	private Long id;

    private String exchangeCode;

	private boolean isTradeHoliday;

	private LocalDate date;

	@Id
	@Column(name = "id")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "exchange_cd")
	public String getExchangeCode() {
		return exchangeCode;
	}

	public void setExchangeCode(String exchangeCode) {
		this.exchangeCode = exchangeCode;
	}

	@Column(name = "cal_date")
	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	@Column(name = "is_trade_holiday")
	public boolean isTradeHoliday() {
		return isTradeHoliday;
	}

	public void setTradeHoliday(boolean isTradeHoliday) {
		this.isTradeHoliday = isTradeHoliday;
	}
}
