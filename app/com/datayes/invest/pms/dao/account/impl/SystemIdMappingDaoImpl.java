package com.datayes.invest.pms.dao.account.impl;

import java.util.List;

import javax.persistence.TypedQuery;

import com.datayes.invest.pms.dao.account.SystemIdMappingDao;
import com.datayes.invest.pms.entity.account.SystemIdMapping;

public class SystemIdMappingDaoImpl extends EntityManagerProvider implements SystemIdMappingDao {

	@Override
	public Long findPmsId(String otherSystemId, String idName, String otherSystemName) {
		TypedQuery<SystemIdMapping> query = getEntityManager().createQuery(
				"from SystemIdMapping where otherSystemId=:otherSystemId "
						+ "and idName=:idName and otherSystemName=:otherSystemName ", SystemIdMapping.class);
		query.setParameter("otherSystemId", otherSystemId).setParameter("idName", idName)
				.setParameter("otherSystemName", otherSystemName);
		query.setHint("org.hibernate.cacheable", true);
		
		List<SystemIdMapping> list = query.getResultList();
		return list.isEmpty() ? null : list.get(0).getPmsId();
	}
}
