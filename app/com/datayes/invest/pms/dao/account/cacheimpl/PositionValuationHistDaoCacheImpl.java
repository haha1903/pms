package com.datayes.invest.pms.dao.account.cacheimpl;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;

import com.datayes.invest.pms.dao.account.PositionValuationHistDao;
import com.datayes.invest.pms.dao.account.cacheimpl.cache.Key;
import com.datayes.invest.pms.entity.account.PositionValuationHist;


public class PositionValuationHistDaoCacheImpl extends DaoCacheImpl<PositionValuationHist, PositionValuationHist.PK> implements PositionValuationHistDao {

    protected PositionValuationHistDaoCacheImpl() {
        super(PositionValuationHist.class);
    }

    @Override
    public PositionValuationHist findById(PositionValuationHist.PK pk) {
        Key key = new Key(pk);
        PositionValuationHist h = (PositionValuationHist) getCache().get(key);
        return h;
    }

    @Override
    public void save(PositionValuationHist entity) {
        update(entity);
    }

    @Override
    public void update(PositionValuationHist entity) {
        Key key = new Key(entity.getPK());
        getCache().put(key, entity);
    }

    @Override
    public List<PositionValuationHist> findByPositionIdListTypeIdAsOfDate(List<Long> positionIdList, Long typeId, LocalDate asOfDate) {
        List<PositionValuationHist> hists = new ArrayList<PositionValuationHist>();
        for (Long posId : positionIdList) {
            PositionValuationHist.PK pk = new PositionValuationHist.PK(posId, typeId, asOfDate);
            PositionValuationHist h = findById(pk);
            if (h != null)
                hists.add(h);
        }
        return hists;
    }

    @Override
    public List<PositionValuationHist> findByPositionIdListAsOfDate(List<Long> positionIdList, LocalDate asOfDate) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteByPositionId(Long positionId) {
        throw new UnsupportedOperationException();
    }
}
