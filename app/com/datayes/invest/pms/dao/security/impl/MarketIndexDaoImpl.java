package com.datayes.invest.pms.dao.security.impl;

import com.datayes.invest.pms.dao.security.MarketIndexDao;
import com.datayes.invest.pms.entity.security.MarketIndex;

import java.util.List;

import javax.persistence.TypedQuery;

import org.joda.time.LocalDate;

public class MarketIndexDaoImpl extends EntityManagerProvider implements MarketIndexDao {

	@Override
	public List<MarketIndex> findByMarketIndexEndDate(String marketIndex, LocalDate endDate) {
		TypedQuery<MarketIndex> query = getEntityManager().createQuery(
				"from MarketIndex where marketIndex = :marketIndex and endDate = :endDate", MarketIndex.class);
		query.setParameter("marketIndex", marketIndex);
		query.setParameter("endDate", endDate);
		List<MarketIndex> list = query.getResultList();
		return list;
	}
}
