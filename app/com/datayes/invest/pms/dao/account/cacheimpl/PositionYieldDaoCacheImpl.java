package com.datayes.invest.pms.dao.account.cacheimpl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import com.datayes.invest.pms.dao.account.IdGenerator;
import com.datayes.invest.pms.dao.account.PositionYieldDao;
import com.datayes.invest.pms.dao.account.cacheimpl.cache.Key;
import com.datayes.invest.pms.entity.account.PositionYield;

public class PositionYieldDaoCacheImpl extends DaoCacheImpl<PositionYield, Long> implements PositionYieldDao {
    
    @Inject
    private IdGenerator idGenerator;

    protected PositionYieldDaoCacheImpl() {
        super(PositionYield.class);
    }

    @Override
    public PositionYield findById(Long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void save(PositionYield entity) {
        if (entity.getId() == null) {
            Long id = idGenerator.getNextPositionYieldId();
            entity.setId(id);
        }
        update(entity);
    }

    @Override
    public void update(PositionYield entity) {
        Key key = new Key(entity.getPositionId(), entity.getAsOfDate());
        getCache().put(key, entity);
    }

    @Override
    public List<PositionYield> findByPositionIdsAsOfDate(List<Long> positionIds, LocalDate asOfDate) {
        List<PositionYield> list = new ArrayList<PositionYield>();
        for (Long positionId : positionIds) {
            Key key = new Key(positionId, asOfDate);
            PositionYield y = getCache().get(key);
            if (y != null) {
                list.add(y);
            }
        }
        return list;
    }

    @Override
    public void deleteByAccountId(Long accountId) {
        throw new UnsupportedOperationException();
    }

}
