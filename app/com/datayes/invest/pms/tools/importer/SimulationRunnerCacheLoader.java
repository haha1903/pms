package com.datayes.invest.pms.tools.importer;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import com.datayes.invest.pms.dao.account.cacheimpl.cache.CacheWorkspace;
import com.datayes.invest.pms.dao.account.cacheimpl.cache.Key;
import com.datayes.invest.pms.dao.account.impl.AccountValuationHistDaoImpl;
import com.datayes.invest.pms.dao.account.impl.CarryingValueHistDaoImpl;
import com.datayes.invest.pms.dao.account.impl.PositionDaoImpl;
import com.datayes.invest.pms.dao.account.impl.PositionHistDaoImpl;
import com.datayes.invest.pms.dao.account.impl.PositionValuationHistDaoImpl;
import com.datayes.invest.pms.dao.account.impl.PositionYieldDaoImpl;
import com.datayes.invest.pms.entity.account.AccountValuationHist;
import com.datayes.invest.pms.entity.account.CarryingValueHist;
import com.datayes.invest.pms.entity.account.CashPosition;
import com.datayes.invest.pms.entity.account.Position;
import com.datayes.invest.pms.entity.account.PositionHist;
import com.datayes.invest.pms.entity.account.PositionValuationHist;
import com.datayes.invest.pms.entity.account.PositionYield;
import com.datayes.invest.pms.entity.account.SecurityPosition;

public class SimulationRunnerCacheLoader {

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
	
	@Inject
	private PositionYieldDaoImpl positionYieldDao;
	
	public void load(CacheWorkspace cacheWorkspace, Long accountId, LocalDate asOfDate) {
		List<Long> positionIds = loadPositions(cacheWorkspace, accountId, asOfDate);

		loadPositionHists(cacheWorkspace, positionIds, asOfDate);
		loadPositionValuationHists(cacheWorkspace, positionIds, asOfDate);
		loadAccountValuationHists(cacheWorkspace, accountId, asOfDate);
		loadCarryingValueHists(cacheWorkspace, positionIds, asOfDate);
		loadPositionYields(cacheWorkspace, positionIds, asOfDate);
	}
	
	private void loadPositionYields(CacheWorkspace cacheWorkspace, List<Long> positionIds, LocalDate asOfDate) {
        List<PositionYield> yields = positionYieldDao.findByPositionIdsAsOfDate(positionIds, asOfDate);
        for (PositionYield y : yields) {
            Key key = new Key(y.getPositionId(), asOfDate);
            cacheWorkspace.get(PositionYield.class).preload(key, y);
        }
    }

    private void loadCarryingValueHists(CacheWorkspace cacheWorkspace, List<Long> positionIds, LocalDate asOfDate) {
		List<CarryingValueHist> hists = carryingValueHistDao.findByPositionIdListAsOfDate(positionIds, asOfDate);
		for (CarryingValueHist hist : hists) {
			Key key = new Key(hist.getPK().getPositionId(), hist.getPK().getTypeId(), asOfDate);
			cacheWorkspace.get(CarryingValueHist.class).preload(key, hist);
		}
	}

	private void loadAccountValuationHists(CacheWorkspace cacheWorkspace, Long accountId, LocalDate asOfDate) {
		List<AccountValuationHist> hists = accountValuationHistDao.findByAccountIdAsOfDate(accountId, asOfDate);
		for (AccountValuationHist h : hists) {
			Key key = new Key(h.getPK());
			cacheWorkspace.get(AccountValuationHist.class).preload(key, h);
		}
	}

	private void loadPositionValuationHists(CacheWorkspace cacheWorkspace, List<Long> positionIds, LocalDate asOfDate) {
		List<PositionValuationHist> hists = positionValuationHistDao
				.findByPositionIdListAsOfDate(positionIds, asOfDate);

		for (PositionValuationHist h : hists) {
			System.out.print(h.getPK().getPositionId() + ", ");
		}

		for (PositionValuationHist h : hists) {
			Key key = new Key(h.getPK());
			if (h.getPK().getPositionId() == 130L) {
				System.out.println("ValuationCacheLoader positionId = " + 130L);
			}
			cacheWorkspace.get(PositionValuationHist.class).preload(key, h);
		}
	}

	private void loadPositionHists(CacheWorkspace cacheWorkspace, List<Long> positionIds, LocalDate asOfDate) {
		List<PositionHist> positionHists = positionHistDao.findByPositionIdListAsOfDate(positionIds, asOfDate);
		for (PositionHist ph : positionHists) {
			Key key = new Key(ph.getPK());
			cacheWorkspace.get(PositionHist.class).preload(key, ph);
		}
	}

	private List<Long> loadPositions(CacheWorkspace cacheWorkspace, Long accountId, LocalDate asOfDate) {
		List<Position> positions = positionDao.findByAccountId(accountId);
		List<Long> positionIds = new ArrayList<>();

		for (Position p : positions) {
			if (p.getOpenDate().toLocalDate().isAfter(asOfDate)) {
				continue;
			}
			if (p instanceof SecurityPosition) {
				SecurityPosition sp = (SecurityPosition) p;
				Key key = new Key(accountId, sp.getSecurityId(), sp.getLedgerId());
				cacheWorkspace.get(SecurityPosition.class).preload(key, sp);
			} else if (p instanceof CashPosition) {
				CashPosition cp = (CashPosition) p;
				Key key = new Key(accountId, cp.getLedgerId());
				cacheWorkspace.get(CashPosition.class).preload(key, cp);
			} else {
				throw new RuntimeException("Unknown position type: " + p);
			}
			positionIds.add(p.getId());
		}

		return positionIds;
	}
}
