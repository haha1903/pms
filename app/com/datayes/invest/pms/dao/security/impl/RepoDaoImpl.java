package com.datayes.invest.pms.dao.security.impl;

import javax.persistence.TypedQuery;

import com.datayes.invest.pms.dao.security.RepoDao;
import com.datayes.invest.pms.entity.security.Repo;

public class RepoDaoImpl extends GenericSecurityMasterDaoImpl<Repo, Long> implements RepoDao {

	protected RepoDaoImpl() {
		super(Repo.class);
	}

	public Repo findByTickerSymbol(String tickerSymbol) {
		String query = String.format("from %s where tickerSymbol=:tickerSymbol", classOfEntity.getName());
		TypedQuery<Repo> q = getEntityManager().createQuery(query, Repo.class);
		return enableCache(q).setParameter("tickerSymbol", tickerSymbol).getSingleResult();				
	}
}
