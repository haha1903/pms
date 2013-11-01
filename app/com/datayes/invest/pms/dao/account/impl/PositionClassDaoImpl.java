package com.datayes.invest.pms.dao.account.impl;

import java.util.List;

import com.datayes.invest.pms.dao.account.PositionClassDao;
import com.datayes.invest.pms.entity.account.PositionClass;

public class PositionClassDaoImpl extends GenericAccountMasterDaoImpl<PositionClass, String>
    implements PositionClassDao {

    protected PositionClassDaoImpl() {
        super(PositionClass.class);
    }

    @SuppressWarnings("unchecked")
    public List<PositionClass> findAll() {
        List<PositionClass> all = (List<PositionClass>) getEntityManager().createQuery(
            "from PositionClass").getResultList();
        return all;
    }
}
