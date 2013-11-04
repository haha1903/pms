package com.datayes.invest.pms.dao.account.cacheimpl;

import java.util.List;

import org.joda.time.LocalDate;

import com.datayes.invest.pms.dao.account.AccountValuationHistDao;
import com.datayes.invest.pms.dao.account.cacheimpl.cache.Key;
import com.datayes.invest.pms.entity.account.AccountValuationHist;

public class AccountValuationHistDaoCacheImpl extends DaoCacheImpl<AccountValuationHist, AccountValuationHist.PK> implements AccountValuationHistDao {

	protected AccountValuationHistDaoCacheImpl() {
        super(AccountValuationHist.class);
    }
	
	@Override
	public AccountValuationHist findById(AccountValuationHist.PK pk) {
	    Key key = key(pk.getAccountId(), pk.getTypeId(), pk.getAsOfDate());
	    return getCache().get(key);
	}

    @Override
    public void save(AccountValuationHist entity) {
        update(entity);
    }

	@Override
    public void update(AccountValuationHist entity) {
	    Key key = key(entity);
		getCache().put(key(entity), entity);
    }

    @Override
    public List<AccountValuationHist> findByAccountIdAsOfDate(Long accountId, LocalDate asOfDate) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<AccountValuationHist> findByAccountIdTypeIdBeforeDate(Long accountId, Long typeId, LocalDate beforeDate) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteByAccountId(Long accountId) {
        throw new UnsupportedOperationException();
    }
    
    private Key key(Long accountId, Long typeId, LocalDate asOfDate) {
        return new Key(accountId, typeId, asOfDate);
    }

    private Key key(AccountValuationHist entity) {
        return key(entity.getAccountId(), entity.getTypeId(), entity.getAsOfDate());
    }

}
