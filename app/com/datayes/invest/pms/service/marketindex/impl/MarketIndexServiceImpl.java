package com.datayes.invest.pms.service.marketindex.impl;

import com.datayes.invest.pms.dao.security.MarketIndexCompDao;
import com.datayes.invest.pms.dao.security.MarketIndexDao;
import com.datayes.invest.pms.dao.security.MarketIndexWeightDao;
import com.datayes.invest.pms.entity.security.MarketIndexComp;
import com.datayes.invest.pms.entity.security.MarketIndexWeight;
import com.datayes.invest.pms.persist.Persist;
import com.datayes.invest.pms.persist.Transaction;
import com.datayes.invest.pms.service.marketindex.MarketIndex;
import com.datayes.invest.pms.service.marketindex.MarketIndexComponent;
import com.datayes.invest.pms.service.marketindex.MarketIndexService;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

class LoadWorker implements Callable<MarketIndex> {

    private Long marketIndexId;

    @Inject
    private MarketIndexDao marketIndexDao = null;

    @Inject
    private MarketIndexCompDao marketIndexCompDao = null;

    @Inject
    private MarketIndexWeightDao marketIndexWeightDao = null;


    public LoadWorker(Long marketIndexId) {
        this.marketIndexId = marketIndexId;
    }

    private Map<Long, MarketIndexComponent> createComponentMap(List<MarketIndexComponent> list) {
        Map<Long, MarketIndexComponent> map = new HashMap<>();

        for(MarketIndexComponent component : list) {
            map.put(component.getSecurityId(), component);
        }

        return map;
    }


    private MarketIndex doload() {
        com.datayes.invest.pms.entity.security.MarketIndex index = marketIndexDao.findById(marketIndexId);
        if(null == index) {
            return null;
        }
        else {
            List<MarketIndexComp> compEntities = marketIndexCompDao.findCurrentByMarketIndexId(marketIndexId);
            List<MarketIndexComponent> components = new LinkedList<>();

            for(MarketIndexComp comp : compEntities) {
                MarketIndexWeight miw = marketIndexWeightDao
                        .findLatestByMarketIndexIdSecurityId(marketIndexId, comp.getSecurityId());

                Double weight;
                if(null == miw) {
                    weight = 0.0d;
                }
                else {
                    weight = miw.getWeight();
                }

                components.add(new MarketIndexComponent(comp.getSecurityId(), weight));
            }

            return new MarketIndex(marketIndexId, index.getName(), createComponentMap(components));
        }
    }

    @Override
    public MarketIndex call() throws Exception {
        Transaction tx1 = Persist.beginTransaction();

        MarketIndex marketIndex = doload();

        tx1.commit();

        return marketIndex;
    }
}

public class MarketIndexServiceImpl implements MarketIndexService {
    private Map<Long, MarketIndex> cache = new HashMap<>();

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private static final Logger LOGGER = LoggerFactory.getLogger(MarketIndexServiceImpl.class);


    private MarketIndex load(Long marketIndexId) throws ExecutionException, InterruptedException {
        Future<MarketIndex> f = executor.submit(new LoadWorker(marketIndexId));

        return f.get();
    }

    @Override
    public MarketIndex get(Long marketIndexId) {
        MarketIndex marketIndex = cache.get(marketIndexId);
        if(null == marketIndex) {
            try {
                marketIndex = load(marketIndexId);
                cache.put(marketIndexId, marketIndex);
            }
            catch (ExecutionException | InterruptedException e) {
                LOGGER.error(e.getMessage());
                marketIndex = null;
            }
        }

        return marketIndex;
    }
}
