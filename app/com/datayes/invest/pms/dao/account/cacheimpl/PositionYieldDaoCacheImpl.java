package com.datayes.invest.pms.dao.account.cacheimpl;

import com.datayes.invest.pms.dao.account.PositionYieldDao;
import com.datayes.invest.pms.dao.account.PositionYieldIdGenerator;
import com.datayes.invest.pms.dao.account.cacheimpl.cache.Key;
import com.datayes.invest.pms.entity.account.PositionYield;
import org.joda.time.LocalDate;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class PositionYieldDaoCacheImpl extends DaoCacheImpl<PositionYield, Long> implements PositionYieldDao {
    
    @Inject
    private PositionYieldIdGenerator idGenerator;

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
            Long id = idGenerator.getNextId();
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
