package com.datayes.invest.pms.dao.security;

import org.joda.time.LocalDate;

import com.datayes.invest.pms.entity.security.ExchangeCalendar;

public interface ExchangeCalendarDao extends GenericSecurityMasterDao<ExchangeCalendar, Long> {

	ExchangeCalendar findByDateExchangeCode(LocalDate date, String exchangeCode);

}
