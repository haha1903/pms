package com.datayes.invest.pms.dao.security.impl;

import com.datayes.invest.pms.dao.security.InstitutionIndustryDao;
import com.datayes.invest.pms.entity.security.InstitutionIndustry;

import javax.persistence.Query;
import java.util.List;

public class InstitutionIndustryDaoImpl extends EntityManagerProvider
    implements InstitutionIndustryDao {

    @SuppressWarnings("unchecked")
    @Override
    public List<InstitutionIndustry> findByPartyIdDataSourceName(Long partyId, String dataSourceName) {
        // TODO data source name does not work. When it's in the query, no result returned
        Query q = getEntityManager().createQuery("select i from InstitutionIndustry i where " +
            "i.partyId = :partyId and i.isCurrent = '1'");
        q.setParameter("partyId", partyId);
        q.setHint("org.hibernate.cacheable", true);
        
        List<InstitutionIndustry> list = (List<InstitutionIndustry>) q.getResultList();
        return list;
    }
}
