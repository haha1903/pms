package com.datayes.invest.pms.dao.account.cacheimpl;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import com.datayes.invest.pms.dao.account.IdGenerator;
import com.datayes.invest.pms.dao.account.SecurityPositionDao;
import com.datayes.invest.pms.dao.account.cacheimpl.cache.Key;
import com.datayes.invest.pms.entity.account.SecurityPosition;


public class SecurityPositionDaoCacheImpl extends DaoCacheImpl<SecurityPosition, Long> implements SecurityPositionDao {
    
    protected SecurityPositionDaoCacheImpl() {
        super(SecurityPosition.class);
    }

    @Inject
    private IdGenerator idGenerator;

	@Override
	public List<SecurityPosition> findByAccountId(Long accountId) {
		List<SecurityPosition> result = new LinkedList<SecurityPosition>();
		for (SecurityPosition pos : getCache().getAll()) {
			if (pos.getAccountId().equals(accountId)) {
				result.add(pos);
			}
		}

		return result;
	}

	@Override
	public SecurityPosition findById(Long id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void save(SecurityPosition entity) {
		Long positionId = entity.getId();
		if (positionId == null) {
		    long id = idGenerator.getNextPositionId();
		    entity.setId(id);
		}
        if (entity.getId().equals(Long.valueOf(96L))) {
            System.out.println("stop");
        }
		getCache().put(key(entity), entity);
	}

	@Override
	public void update(SecurityPosition entity) {
		getCache().put(key(entity), entity);
	}

	@Override
	public SecurityPosition findByAccountIdSecurityIdLedgerId(Long accountId, Long securityId, Long ledgerId) {
		Key key = key(accountId, securityId, ledgerId);
		return getCache().get(key);
	}

    private Key key(Long accountId, Long securityId, Long ledgerId) {
        return new Key(accountId, securityId, ledgerId);
    }

    private Key key(SecurityPosition object) {
        return key(object.getAccountId(), object.getSecurityId(), object.getLedgerId());
    }

}
