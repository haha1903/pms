package com.datayes.invest.pms.dao.account.impl;

import com.datayes.invest.pms.dao.account.MarketDataSnapshotDao;
import com.datayes.invest.pms.entity.account.MarketDataSnapshot;

import javax.persistence.Query;
import java.util.List;


public class MarketDataSnapshotDaoImpl extends GenericAccountMasterDaoImpl<MarketDataSnapshot, Long>
    implements MarketDataSnapshotDao {

    protected MarketDataSnapshotDaoImpl() {
        super(MarketDataSnapshot.class);
    }

    @Override
    public MarketDataSnapshot findBySecurityId(Long securityId) {
        Query q = getEntityManager().createQuery(
                "from MarketDataSnapshot where securityId = :securityId");
        q.setParameter("securityId", securityId);

        List<MarketDataSnapshot> list = (List<MarketDataSnapshot>) q.getResultList();

        if(!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<MarketDataSnapshot> findAll() {
        Query q = getEntityManager().createQuery(
                "from MarketDataSnapshot");

        List<MarketDataSnapshot> list = (List<MarketDataSnapshot>) q.getResultList();
        return list;
    }
}
