package com.datayes.invest.pms.dao.security.impl;

import java.util.List;

import javax.persistence.TypedQuery;

import com.datayes.invest.pms.dao.security.SecurityDao;
import com.datayes.invest.pms.entity.security.Security;

public class SecurityDaoImpl extends GenericSecurityMasterDaoImpl<Security, Long> implements SecurityDao {

	protected SecurityDaoImpl() {
		super(Security.class);
	}

	public List<Security> findByTickerSymbol(String tickerSymbol) {
		TypedQuery<Security> q = getEntityManager().createQuery("from Security where tickerSymbol=:tickerSymbol",
				Security.class);
		q.setParameter("tickerSymbol", tickerSymbol);
		enableCache(q);
		return q.getResultList();
	}
}
