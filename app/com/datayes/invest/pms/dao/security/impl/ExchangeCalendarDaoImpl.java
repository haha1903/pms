package com.datayes.invest.pms.dao.security.impl;

import org.joda.time.LocalDate;

import com.datayes.invest.pms.dao.security.ExchangeCalendarDao;
import com.datayes.invest.pms.entity.security.ExchangeCalendar;

public class ExchangeCalendarDaoImpl extends GenericSecurityMasterDaoImpl<ExchangeCalendar, Long> implements
		ExchangeCalendarDao {

	protected ExchangeCalendarDaoImpl() {
		super(ExchangeCalendar.class);
	}

	@Override
	public ExchangeCalendar findByDateExchangeCode(LocalDate date, String exchangeCode) {
		String query = String
				.format("from %s where date=:date and exchangeCode=:exchangeCode", classOfEntity.getName());
		return getEntityManager().createQuery(query, ExchangeCalendar.class).setParameter("date", date)
				.setParameter("exchangeCode", exchangeCode).getSingleResult();
	}
}
