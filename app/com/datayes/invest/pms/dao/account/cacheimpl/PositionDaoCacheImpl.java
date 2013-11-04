package com.datayes.invest.pms.dao.account.cacheimpl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.datayes.invest.pms.dao.account.PositionDao;
import com.datayes.invest.pms.dao.account.cacheimpl.cache.CacheWorkspace;
import com.datayes.invest.pms.entity.account.CashPosition;
import com.datayes.invest.pms.entity.account.Position;
import com.datayes.invest.pms.entity.account.SecurityPosition;


public class PositionDaoCacheImpl implements PositionDao {
    
    @Inject
    private CacheWorkspace cacheWorkspace;

    @Override
    public List<Position> findByAccountId(Long accountId) {
        List<Position> positions = new ArrayList<Position>();
        for (Object obj : cacheWorkspace.get(CashPosition.class).getAll()) {
            CashPosition p = (CashPosition) obj;
            if (p.getAccountId().equals(accountId)) {
                positions.add(p);
            }
        }
        for (Object obj : cacheWorkspace.get(SecurityPosition.class).getAll()) {
            SecurityPosition p = (SecurityPosition) obj;
            if (p.getAccountId().equals(accountId)) {
                positions.add(p);
            }
        }
        return positions;
    }

    @Override
    public Position findById(Long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void save(Position entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(Position entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(Position entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void detach(Position entity) {
        throw new UnsupportedOperationException();
    }

}
