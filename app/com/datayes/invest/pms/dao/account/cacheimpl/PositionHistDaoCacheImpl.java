package com.datayes.invest.pms.dao.account.cacheimpl;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;

import com.datayes.invest.pms.dao.account.PositionHistDao;
import com.datayes.invest.pms.dao.account.cacheimpl.cache.Key;
import com.datayes.invest.pms.entity.account.PositionHist;


public class PositionHistDaoCacheImpl extends DaoCacheImpl<PositionHist, PositionHist.PK> implements PositionHistDao {

	protected PositionHistDaoCacheImpl() {
        super(PositionHist.class);
    }

    @Override
	public void save(PositionHist entity) {
		update(entity);
	}

	@Override
	public void update(PositionHist entity) {
	    Key key = new Key(entity.getPK());
		getCache().put(key, entity);
	}

    @Override
    public PositionHist findById(PositionHist.PK pk) {
        Key key = new Key(pk);
        return getCache().get(key);
    }
    
	@Override
	public PositionHist findByPositionIdAsOfDate(Long positionId, LocalDate asOfDate) {
	    PositionHist.PK pk = new PositionHist.PK(positionId, asOfDate);
		Key key = new Key(pk);
		return getCache().get(key);
	}

	@Override
	public List<PositionHist> findByPositionIdListAsOfDate(List<Long> positionIdList, LocalDate asOfDate) {
		List<PositionHist> hists = new ArrayList<PositionHist>();
		for (Long positionId : positionIdList) {
			PositionHist ph = findByPositionIdAsOfDate(positionId, asOfDate);
			if (ph != null) {
				hists.add(ph);
			}
		}
		return hists;
	}

	@Override
	public void deleteByPositionId(Long accountId) {
		throw new UnsupportedOperationException();
	}

}
