package com.datayes.invest.pms.dao.account.impl;

import com.datayes.invest.pms.dao.account.CashPositionDao;
import com.datayes.invest.pms.dao.account.PositionIdGenerator;
import com.datayes.invest.pms.entity.account.CashPosition;

import javax.inject.Inject;
import javax.persistence.TypedQuery;
import java.util.List;

public class CashPositionDaoImpl extends AccountRelatedDaoImpl<CashPosition, Long> implements CashPositionDao {
    
    @Inject
    private PositionIdGenerator idGenerator;

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
            Long id = idGenerator.getNextId();
            entity.setId(id);
        }
        
		super.save(entity);
	}
}
