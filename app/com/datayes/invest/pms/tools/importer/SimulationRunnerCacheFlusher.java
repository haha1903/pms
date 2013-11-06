package com.datayes.invest.pms.tools.importer;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datayes.invest.pms.dao.account.cacheimpl.cache.Cache;
import com.datayes.invest.pms.dao.account.cacheimpl.cache.CacheWorkspace;
import com.datayes.invest.pms.dao.account.cacheimpl.cache.Element;
import com.datayes.invest.pms.dao.account.cacheimpl.cache.Key;
import com.datayes.invest.pms.dao.account.impl.AccountValuationHistDaoImpl;
import com.datayes.invest.pms.dao.account.impl.CarryingValueHistDaoImpl;
import com.datayes.invest.pms.dao.account.impl.PositionDaoImpl;
import com.datayes.invest.pms.dao.account.impl.PositionHistDaoImpl;
import com.datayes.invest.pms.dao.account.impl.PositionValuationHistDaoImpl;
import com.datayes.invest.pms.entity.account.AccountValuationHist;
import com.datayes.invest.pms.entity.account.CarryingValueHist;
import com.datayes.invest.pms.entity.account.CashPosition;
import com.datayes.invest.pms.entity.account.Position;
import com.datayes.invest.pms.entity.account.PositionHist;
import com.datayes.invest.pms.entity.account.PositionValuationHist;
import com.datayes.invest.pms.entity.account.SecurityPosition;
import com.datayes.invest.pms.persist.Persist;
import com.datayes.invest.pms.persist.Transaction;

public class SimulationRunnerCacheFlusher {
	private final static Logger logger = LoggerFactory.getLogger(SimulationRunnerCacheFlusher.class);

	@Inject
	private PositionDaoImpl positionDao;

	@Inject
	private PositionHistDaoImpl positionHistDao;

	@Inject
	private PositionValuationHistDaoImpl positionValuationHistDao;

	@Inject
	private AccountValuationHistDaoImpl accountValuationHistDao;

	@Inject
	private CarryingValueHistDaoImpl carryingValueHistDao;

	private ExecutorService threadExecutor = Executors.newSingleThreadExecutor();

	private List<Future<?>> threadList = new LinkedList<Future<?>>();

	public void waitForCompletion() {
		logger.debug("wait for thread to complete flush");
		Iterator<Future<?>> iterator = threadList.iterator();
		while (iterator.hasNext()) {
			Future<?> future = iterator.next();
			try {
				future.get();
			} catch (Exception e) {
				e.printStackTrace();
			}
			iterator.remove();
		}
	}

	public void flush(final CacheWorkspace cacheWorkspace, final Long accountId, final LocalDate asOfDate) {
		threadList.add(threadExecutor.submit(new Runnable() {
			@Override
			public void run() {
				logger.debug("flush for date: " + asOfDate);
				
				Transaction tx = Persist.beginTransaction();

				try {
    				Set<Long> positionIds = flushPosition(cacheWorkspace, accountId);
    				flushPositionHist(cacheWorkspace, positionIds, asOfDate);
    				flushPositionValuationHist(cacheWorkspace, positionIds, asOfDate);
    				flushAccountValuationHist(cacheWorkspace, accountId, asOfDate);
    				flushCarryingValueHist(cacheWorkspace, accountId, asOfDate);
    				
    				tx.commit();
				} catch (Throwable th) {
				    logger.error("Exception occurred flushing cached data", th);
				    tx.rollback();
				}
			}
		}));
	}

	private void flushCarryingValueHist(CacheWorkspace cacheWorkspace, Long accountId, LocalDate asOfDate) {
		Cache cache = cacheWorkspace.get(CarryingValueHist.class);
		Iterator<Key> iter = cache.keysIterator();
		while (iter.hasNext()) {
			Key key = iter.next();
			Element e = (Element) cache.getElement(key);
			CarryingValueHist vh = (CarryingValueHist) e.getValue();
			if (vh.getAccountId() != accountId || !vh.getPK().getAsOfDate().equals(asOfDate)) {
				continue;
			}
			if (e.getState() == Element.State.CREATED) {
				carryingValueHistDao.save(vh);
			}
			if (e.getState() == Element.State.UPDATED) {
				carryingValueHistDao.update(vh);
			}
			iter.remove();
		}
	}

	private void flushAccountValuationHist(CacheWorkspace cacheWorkspace, Long accountId, LocalDate asOfDate) {
		Cache cache = cacheWorkspace.get(AccountValuationHist.class);
		Iterator<Key> iter = cache.keysIterator();
		while (iter.hasNext()) {
			Key key = iter.next();
			Element e = (Element) cache.getElement(key);
			AccountValuationHist vh = (AccountValuationHist) e.getValue();
			AccountValuationHist.PK pk = vh.getPK();
			if (pk.getAccountId() != accountId || ! pk.getAsOfDate().equals(asOfDate)) {
				continue;
			}
			if (e.getState() == Element.State.CREATED) {
				accountValuationHistDao.save(vh);
			}
			if (e.getState() == Element.State.UPDATED) {
				accountValuationHistDao.update(vh);
			}
			iter.remove();
		}
	}

	private void flushPositionValuationHist(CacheWorkspace cacheWorkspace, Set<Long> positionIds, LocalDate asOfDate) {
		Cache cache = cacheWorkspace.get(PositionValuationHist.class);
		Iterator<Key> iter = cache.keysIterator();
		while (iter.hasNext()) {
			Key key = iter.next();
			Element e = (Element) cache.getElement(key);
			PositionValuationHist vh = (PositionValuationHist) e.getValue();
			Long posId = vh.getPK().getPositionId();
			LocalDate date = vh.getPK().getAsOfDate();
			if (!positionIds.contains(posId) || !date.equals(asOfDate)) {
				continue;
			}
			if (e.getState() == Element.State.CREATED) {
				positionValuationHistDao.save(vh);
			}
			if (e.getState() == Element.State.UPDATED) {
				positionValuationHistDao.update(vh);
			}
			iter.remove();
		}
	}

	private void flushPositionHist(CacheWorkspace cacheWorkspace, Set<Long> positionIds, LocalDate asOfDate) {
		Cache cache = cacheWorkspace.get(PositionHist.class);
		Iterator<Key> iter = cache.keysIterator();
		while (iter.hasNext()) {
			Key key = iter.next();
			Element e = (Element) cache.getElement(key);
			PositionHist ph = (PositionHist) e.getValue();
			Long posId = ph.getPK().getPositionId();
			LocalDate date = ph.getPK().getAsOfDate();
			if (!positionIds.contains(posId) || !date.equals(asOfDate)) {
				continue;
			}
			if (e.getState() == Element.State.CREATED) {
				positionHistDao.save(ph);
			}
			if (e.getState() == Element.State.UPDATED) {
				positionHistDao.update(ph);
			}
			iter.remove();
		}
	}

	private Set<Long> flushPosition(CacheWorkspace cacheWorkspace, Long accountId) {
		Set<Long> positionIds = new HashSet<>();

		{
			Cache secPosCache = cacheWorkspace.get(SecurityPosition.class);
			Iterator<Key> iter = secPosCache.keysIterator();
			while (iter.hasNext()) {
				Key key = iter.next();
				Element e = (Element) secPosCache.getElement(key);
				Position p = (Position) e.getValue();
				if (p.getAccountId() != accountId) {
					continue;
				}
				positionIds.add(p.getId());
				if (e.getState() == Element.State.CREATED) {
					positionDao.save(p);
					e.setState(Element.State.LOADED);
				}
				if (e.getState() == Element.State.UPDATED) {
					positionDao.update(p);
					e.setState(Element.State.LOADED);
				}
			}
		}

		{
			Cache cashPosCache = cacheWorkspace.get(CashPosition.class);
			Iterator<Key> iter = cashPosCache.keysIterator();
			while (iter.hasNext()) {
				Key key = iter.next();
				Element e = (Element) cashPosCache.getElement(key);
				Position p = (Position) e.getValue();
				if (p.getAccountId() != accountId) {
					continue;
				}
				positionIds.add(p.getId());
				if (e.getState() == Element.State.CREATED) {
					positionDao.save(p);
					e.setState(Element.State.LOADED);
				}
				if (e.getState() == Element.State.UPDATED) {
					positionDao.update(p);
					e.setState(Element.State.LOADED);
				}
			}
		}

		return positionIds;
	}
}
