package com.datayes.invest.pms.dao.account.cacheimpl;

import java.util.LinkedList;
import java.util.List;

import org.joda.time.LocalDate;

import com.datayes.invest.pms.dao.account.CarryingValueHistDao;
import com.datayes.invest.pms.dao.account.cacheimpl.cache.Key;
import com.datayes.invest.pms.entity.account.CarryingValueHist;

public class CarryingValueHistDaoCacheImpl extends DaoCacheImpl<CarryingValueHist, CarryingValueHist.PK> implements CarryingValueHistDao {

	protected CarryingValueHistDaoCacheImpl() {
        super(CarryingValueHist.class);
    }

	@Override
	public void save(CarryingValueHist entity) {
		update(entity);
	}

	@Override
	public void update(CarryingValueHist entity) {
		getCache().put(key(entity), entity);
	}

	@Override
	public void delete(CarryingValueHist entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void detach(CarryingValueHist entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public CarryingValueHist findById(CarryingValueHist.PK pk) {
		Key key = key(pk.getPositionId(), pk.getTypeId(), pk.getAsOfDate());
		return getCache().get(key);
	}

	@Override
	public List<CarryingValueHist> findByPositionIdListTypeIdAsOfDate(List<Long> positionIdList, Long typeId,
			LocalDate asOfDate) {
		List<CarryingValueHist> result = new LinkedList<CarryingValueHist>();
		for (Long positionId : positionIdList) {
		    CarryingValueHist.PK pk = new CarryingValueHist.PK(positionId, typeId, asOfDate);
			CarryingValueHist hist = findById(pk);
			if (hist != null) {
				result.add(hist);
			}
		}
		return result;
	}

	@Override
	public List<CarryingValueHist> findByPositionIdListAsOfDate(List<Long> positionIdList, LocalDate asOfDate) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteByAccountIdAsOfDate(Long accountId, LocalDate asOfDate) {		
	}

	@Override
	public void deleteByAccountId(Long accountId) {
		throw new UnsupportedOperationException();
	}

    private Key key(Long positonId, Long typeId, LocalDate asOfDate) {
        return new Key(positonId, typeId, asOfDate);
    }

    private Key key(CarryingValueHist object) {
        CarryingValueHist.PK pk = object.getPK();
        return key(pk.getPositionId(), pk.getTypeId(), pk.getAsOfDate());
    }

}
