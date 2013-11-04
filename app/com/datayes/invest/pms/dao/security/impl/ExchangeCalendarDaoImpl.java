package com.datayes.invest.pms.dao.security.impl;

import org.joda.time.LocalDate;

import com.datayes.invest.pms.dao.security.ExchangeCalendarDao;
import com.datayes.invest.pms.entity.security.ExchangeCalendar;

import java.util.List;

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

    @Override
    public List<ExchangeCalendar> findNextDaysByExchangeCode(LocalDate date, String exchangeCode, int numDays) {
        String query = String
                .format("from %s where date<=:end and date>:start", classOfEntity.getName());

        return getEntityManager().createQuery(query, ExchangeCalendar.class)
                .setParameter("start", date).setParameter("end", date.plusDays(numDays))
                .getResultList();
    }

    @Override
    public List<ExchangeCalendar> findPreviousDaysByExchangeCode(LocalDate date, String exchangeCode, int numDays) {
        String query = String
                .format("from %s where date<:end and date>=:start order by date desc", classOfEntity.getName());

        return getEntityManager().createQuery(query, ExchangeCalendar.class)
                .setParameter("start", date.minusDays(numDays)).setParameter("end", date)
                .getResultList();
    }
}
