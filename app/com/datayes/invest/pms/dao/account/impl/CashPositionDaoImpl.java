package com.datayes.invest.pms.dao.account.impl;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.TypedQuery;

import com.datayes.invest.pms.dao.account.CashPositionDao;
import com.datayes.invest.pms.dao.account.IdGenerator;
import com.datayes.invest.pms.entity.account.CashPosition;

public class CashPositionDaoImpl extends AccountRelatedDaoImpl<CashPosition, Long> implements CashPositionDao {
    
    @Inject
    private IdGenerator idGenerator;

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
            Long id = idGenerator.getNextPositionId();
            entity.setId(id);
        }
        
		super.save(entity);
	}
}
