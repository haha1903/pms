package com.datayes.invest.pms.tools.importer;

import com.datayes.invest.pms.dao.security.SecurityDao;
import com.datayes.invest.pms.entity.security.Equity;
import com.datayes.invest.pms.entity.security.Security;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TickerResolver {

    private static Logger LOGGER = LoggerFactory.getLogger(TickerResolver.class);

    private final SecurityDao securityDao;

    public TickerResolver(SecurityDao securityDao) {
        this.securityDao = securityDao;
    }

    public Security loadSecurity(String tickerSymbol) {
        String fixedTicker = fixSecuritySymbol(tickerSymbol);
        List<Security> list = securityDao.findByTickerSymbol(fixedTicker);
        if (list == null || list.isEmpty()) {
            return null;
        }
        if (list.size() > 1) {
            LOGGER.debug("Multiple securities found for ticker " + fixedTicker);
            for (Security sec : list) {
                if (isAGuStock(sec)) {
                    return sec;
                }
            }
        } else {
            return list.get(0);
        }
        return null;
    }

    private String fixSecuritySymbol(String tickerSymbol) {
        try {
            Integer.valueOf(tickerSymbol);
        } catch (NumberFormatException e) {
            return tickerSymbol;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6 - tickerSymbol.length(); i++) {
            sb.append("0");
        }
        return sb.toString() + tickerSymbol;
    }

    private boolean isAGuStock(Security security) {
        return (security instanceof Equity) && ((Equity) security).getTypeCode() == 1;
    }
}
