package com.datayes.invest.pms.dao.account.impl;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.datayes.invest.pms.dao.account.FeeDao;
import com.datayes.invest.pms.entity.account.Fee;

public class FeeDaoImpl extends AccountRelatedDaoImpl<Fee, Long> implements FeeDao {

	protected FeeDaoImpl() {
		super(Fee.class);
	}

	public List<Fee> findByAccountId(Long accountId) {
		TypedQuery<Fee> q = getEntityManager().createQuery(
				"from " + classOfEntity.getName()
						+ " where accountId = :accountId order by securityId desc, tradeSideCode desc", Fee.class);
		enableCache(q);

		return q.setParameter("accountId", accountId).getResultList();
	}

	@Override
	public void deleteByAccountId(Long accountId) {
		Query q = getEntityManager().createQuery("delete from Fee where accountId = :accountId");
		q.setParameter("accountId", accountId);
		q.executeUpdate();
	}
}
