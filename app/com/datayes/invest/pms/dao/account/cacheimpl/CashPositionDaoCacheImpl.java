package com.datayes.invest.pms.dao.account.cacheimpl;

import com.datayes.invest.pms.dao.account.CashPositionDao;
import com.datayes.invest.pms.dao.account.PositionIdGenerator;
import com.datayes.invest.pms.dao.account.cacheimpl.cache.Key;
import com.datayes.invest.pms.entity.account.CashPosition;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CashPositionDaoCacheImpl extends DaoCacheImpl<CashPosition, Long> implements CashPositionDao {
    
    @Inject
    private PositionIdGenerator positionIdGenerator;

	protected CashPositionDaoCacheImpl() {
        super(CashPosition.class);
    }

	@Override
	public List<CashPosition> findByAccountId(Long accountId) {
	    if (accountId == null) {
	        return Collections.emptyList();
	    }
	    List<CashPosition> list = new ArrayList<CashPosition>();
		for (CashPosition p : getCache().getAll()) {
		    if (accountId.equals(p.getAccountId())) {
		        list.add(p);
		    }
		}
		return list;
	}

	@Override
	public CashPosition findById(Long id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void save(CashPosition entity) {
	    Long positionId = entity.getId();
        if (positionId == null) {
            long id = positionIdGenerator.getNextId();
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
