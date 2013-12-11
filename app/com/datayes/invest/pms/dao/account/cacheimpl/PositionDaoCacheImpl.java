package com.datayes.invest.pms.dao.account.cacheimpl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.datayes.invest.pms.dao.account.PositionDao;
import com.datayes.invest.pms.dao.account.cacheimpl.cache.CacheWorkspace;
import com.datayes.invest.pms.entity.account.CashPosition;
import com.datayes.invest.pms.entity.account.Position;
import com.datayes.invest.pms.entity.account.SecurityPosition;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;


public class PositionDaoCacheImpl implements PositionDao {
    
    @Override
    public List<Position> findByAccountId(Long accountId) {
        CacheWorkspace cacheWs = CacheWorkspace.current();
        List<Position> positions = new ArrayList<Position>();
        for (Object obj : cacheWs.get(CashPosition.class).getAll()) {
            CashPosition p = (CashPosition) obj;
            if (p.getAccountId().equals(accountId)) {
                positions.add(p);
            }
        }
        for (Object obj : cacheWs.get(SecurityPosition.class).getAll()) {
            SecurityPosition p = (SecurityPosition) obj;
            if (p.getAccountId().equals(accountId)) {
                positions.add(p);
            }
        }
        return positions;
    }

    @Override
    public List<Position> findByAccountIdBeforeAsOfDate(Long accountId, LocalDate asOfDate) {
        LocalDateTime startDateTime = new LocalDateTime(asOfDate.plusDays(1).toDateTimeAtStartOfDay());
        CacheWorkspace cacheWs = CacheWorkspace.current();
        List<Position> positions = new ArrayList<>();
        for (Object obj : cacheWs.get(CashPosition.class).getAll()) {
            CashPosition p = (CashPosition) obj;
            if (p.getAccountId().equals(accountId)) {
                if ( p.getOpenDate().isBefore(startDateTime) ) {
                    positions.add(p);
                }
            }
        }
        for (Object obj : cacheWs.get(SecurityPosition.class).getAll()) {
            SecurityPosition p = (SecurityPosition) obj;
            if (p.getAccountId().equals(accountId)) {
                if ( p.getOpenDate().isBefore(startDateTime) ) {
                    positions.add(p);
                }
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
