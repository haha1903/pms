package com.datayes.invest.pms.dao.account.impl;

import com.datayes.invest.pms.dao.account.PositionValuationTypeDao;
import com.datayes.invest.pms.entity.account.PositionValuationType;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * System: Ubuntu
 * User: baoan @datayes
 * Date: 8/27/13
 * Time: 4:29 PM
 */
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
