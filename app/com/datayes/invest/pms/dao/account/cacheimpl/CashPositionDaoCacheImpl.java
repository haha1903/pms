package com.datayes.invest.pms.dao.account.cacheimpl;

import java.util.List;

import javax.inject.Inject;

import com.datayes.invest.pms.dao.account.CashPositionDao;
import com.datayes.invest.pms.dao.account.IdGenerator;
import com.datayes.invest.pms.dao.account.cacheimpl.cache.Key;
import com.datayes.invest.pms.entity.account.CashPosition;

public class CashPositionDaoCacheImpl extends DaoCacheImpl<CashPosition, Long> implements CashPositionDao {
    
    @Inject
    private IdGenerator idGenerator;

	protected CashPositionDaoCacheImpl() {
        super(CashPosition.class);
    }

	@Override
	public List<CashPosition> findByAccountId(Long accountId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public CashPosition findById(Long id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void save(CashPosition entity) {
	    Long positionId = entity.getId();
        if (positionId == null) {
            long id = idGenerator.getNextPositionId();
            entity.setId(id);
        }
        Key k = key(entity);
        getCache().put(k, entity);
	}

	@Override
	public void update(CashPosition entity) {
	    Key k = key(entity);
		getCache().put(k, entity);
	}

	@Override
	public CashPosition findByAccountIdLedgerId(Long accountId, Long ledgerId) {
		Key key = key(accountId, ledgerId);
		return getCache().get(key);
	}
	
    private Key key(CashPosition p) {
        return key(p.getAccountId(), p.getLedgerId());
    }

    private Key key(Long accountId, Long ledgerId) {
        return new Key(accountId, ledgerId);
    }

}
