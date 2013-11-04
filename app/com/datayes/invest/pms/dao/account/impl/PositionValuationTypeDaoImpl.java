package com.datayes.invest.pms.dao.account.impl;

import com.datayes.invest.pms.dao.account.PositionValuationTypeDao;
import com.datayes.invest.pms.entity.account.PositionValuationType;

import java.util.List;


public class PositionValuationTypeDaoImpl extends GenericAccountMasterDaoImpl<PositionValuationType, Long>
    implements PositionValuationTypeDao{

    protected PositionValuationTypeDaoImpl(){
        super(PositionValuationType.class);
    }

    @SuppressWarnings("unchecked")
    public List<PositionValuationType> findAll(){
        List<PositionValuationType> results = (List<PositionValuationType>) getEntityManager().createQuery(
                "from PositionValuationType").getResultList();
        return results;
    }
}
