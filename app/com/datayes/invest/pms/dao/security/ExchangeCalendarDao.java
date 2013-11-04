package com.datayes.invest.pms.dao.security;

import org.joda.time.LocalDate;

import com.datayes.invest.pms.entity.security.ExchangeCalendar;

import java.util.List;

public interface ExchangeCalendarDao extends GenericSecurityMasterDao<ExchangeCalendar, Long> {

	ExchangeCalendar findByDateExchangeCode(LocalDate date, String exchangeCode);

    List<ExchangeCalendar> findNextDaysByExchangeCode(LocalDate date, String exchangeCode, int numDays);

    List<ExchangeCalendar> findPreviousDaysByExchangeCode(LocalDate date, String exchangeCode, int numDays);

}
