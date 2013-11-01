package com.datayes.invest.pms.dao.account.impl;

import java.util.List;

import javax.persistence.TypedQuery;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import com.datayes.invest.pms.dao.account.SecurityPositionDao;
import com.datayes.invest.pms.entity.account.CashPosition;
import com.datayes.invest.pms.entity.account.SecurityPosition;

public class SecurityPositionDaoImpl extends AccountRelatedDaoImpl<SecurityPosition, Long> implements
		SecurityPositionDao {

	protected SecurityPositionDaoImpl() {
		super(SecurityPosition.class);
	}

	public SecurityPosition findSecurityPosition(Long accountId, Long securityId, Long ledgerId) {
		String query = String.format(
				"from %s where accountId=:accountId and securityId=:securityId and ledgerId=:ledgerId",
				classOfEntity.getName());

		TypedQuery<SecurityPosition> q = getEntityManager().createQuery(query, SecurityPosition.class);
		enableCache(q);
		
		List<SecurityPosition> list = q
				.setParameter("accountId", accountId).setParameter("securityId", securityId)
				.setParameter("ledgerId", ledgerId).getResultList();
		return list.isEmpty() ? null : list.get(0);
	}

	public SecurityPosition findByAccountIdSecurityIdLedgerIdOpenDate(Long accountId, Long securityId, Long ledgerId,
			LocalDate openDate) {
		String query = String
				.format("from %s where accountId=:accountId and securityId=:securityId and ledgerId=:ledgerId and openDate=:openDate",
						classOfEntity.getName());

		TypedQuery<SecurityPosition> q = getEntityManager().createQuery(query, SecurityPosition.class);
		enableCache(q);
		
		List<SecurityPosition> list = q
				.setParameter("accountId", accountId).setParameter("securityId", securityId)
				.setParameter("ledgerId", ledgerId)
				.setParameter("openDate", new LocalDateTime(openDate.toDateTimeAtStartOfDay())).getResultList();
		return list.isEmpty() ? null : list.get(0);
	}
	
	@Override
	public void save(SecurityPosition entity) {
        if (entity.getId() == null) {
            // TODO
            entity.setId(1L);
        }
        if (entity.getId().equals(Long.valueOf(96L))) {
            System.out.println("stop");
        }

		super.save(entity);
	}
	
}
