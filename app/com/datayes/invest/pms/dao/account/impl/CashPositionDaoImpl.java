package com.datayes.invest.pms.dao.account.impl;

import java.util.List;

import javax.persistence.TypedQuery;

import com.datayes.invest.pms.dao.account.CashPositionDao;
import com.datayes.invest.pms.entity.account.CashPosition;

public class CashPositionDaoImpl extends AccountRelatedDaoImpl<CashPosition, Long> implements CashPositionDao {

	protected CashPositionDaoImpl() {
		super(CashPosition.class);
	}

	public CashPosition findByAccountIdLedgerId(Long accountId, Long ledgerId) {
		String query = "from CashPosition where accountId = :accountId and ledgerId = :ledgerId";
		TypedQuery<CashPosition> q = getEntityManager().createQuery(query, CashPosition.class);
		enableCache(q);
		q.setParameter("accountId", accountId);
		q.setParameter("ledgerId", ledgerId);
		List<CashPosition> list = (List<CashPosition>) q.getResultList();
		return list.isEmpty() ? null : list.get(0);
	}

	@Override
	public void save(CashPosition entity) {
        if (entity.getId() == null) {
            // TODO Fix this
            entity.setId(1L);
        }
        
        if (entity.getId().equals(Long.valueOf(96L))) {
            System.out.println("stop");
        }

		super.save(entity);
	}
}
