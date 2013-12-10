package com.datayes.invest.pms.dao.account.impl;

import com.datayes.invest.pms.dao.account.PositionIdGenerator;
import com.datayes.invest.pms.dao.account.SecurityPositionDao;
import com.datayes.invest.pms.entity.account.SecurityPosition;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import javax.inject.Inject;
import javax.persistence.TypedQuery;
import java.util.List;

public class SecurityPositionDaoImpl extends AccountRelatedDaoImpl<SecurityPosition, Long> implements
		SecurityPositionDao {
    
    @Inject
    private PositionIdGenerator idGenerator;

	protected SecurityPositionDaoImpl() {
		super(SecurityPosition.class);
	}

	public SecurityPosition findByAccountIdSecurityIdLedgerId(Long accountId, Long securityId, Long ledgerId) {
		String query = "from SecurityPosition where accountId=:accountId and securityId=:securityId and ledgerId=:ledgerId";
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
            Long id = idGenerator.getNextId();
            entity.setId(id);
        }
        
		super.save(entity);
	}
}
