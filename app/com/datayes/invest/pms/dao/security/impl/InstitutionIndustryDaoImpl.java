package com.datayes.invest.pms.dao.security.impl;

import com.datayes.invest.pms.dao.security.InstitutionIndustryDao;
import com.datayes.invest.pms.entity.security.InstitutionIndustry;

import javax.persistence.TypedQuery;

import java.util.List;

public class InstitutionIndustryDaoImpl extends EntityManagerProvider
    implements InstitutionIndustryDao {

    @Override
    public List<InstitutionIndustry> findByPartyIdDataSourceId(Long partyId, Integer dataSourceId) {
        // TODO data source name does not work. When it's in the query, no result returned
        TypedQuery<InstitutionIndustry> q = getEntityManager().createQuery("from InstitutionIndustry where " +
            "partyId = :partyId and dataSourceId = :dataSourceId and isCurrent = '1'", InstitutionIndustry.class);
        q.setParameter("partyId", partyId);
        q.setParameter("dataSourceId", dataSourceId);
        
        List<InstitutionIndustry> list = q.getResultList();
        return list;
    }
}
