package com.datayes.invest.pms.tools.importer.order;

import com.datayes.invest.pms.dao.security.SecurityDao;
import com.datayes.invest.pms.dbtype.TradeSide;
import com.datayes.invest.pms.entity.security.Security;
import com.datayes.invest.pms.logic.order.OrderManager;
import com.datayes.invest.pms.service.order.Order;
import com.datayes.invest.pms.service.order.OrderBasket;
import com.datayes.invest.pms.tools.importer.TickerResolver;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.pms.ClientException;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;

public class OrderImporter {

    private static Logger LOGGER = LoggerFactory.getLogger(OrderImporter.class);

    private static final String HEADER_TICKER = "Ticker";
    private static final String HEADER_TRADE_SIDE = "TradeSide";
    private static final String HEADER_AMOUNT = "Amount";
    private static final String HEADER_PRICE = "Price";

    @Inject
    private OrderManager orderManager;

    @Inject
    private SecurityDao securityDao;

    public OrderBasket importCsv(Long accountId, File file) {
        OrderBasket basket = parseCsv(file);
        fillAccountId(accountId, basket);
        OrderBasket retBasket = orderManager.createOrders(basket);
        return retBasket;
    }

    private void fillAccountId(Long accountId, OrderBasket basket) {
        for (Order o : basket.getOrders()) {
            o.setAccountId(accountId);
        }
    }

    private OrderBasket parseCsv(File file) {
        BufferedReader reader = null;
        OrderBasket basket = new OrderBasket();
        try {
            reader = new BufferedReader(new FileReader(file));
            String[] header = readHeader(reader);
            String line = reader.readLine();
            while (line != null) {
                String[] values = line.split(",");
                Order order = createOrder(header, values);
                basket.getOrders().add(order);
                line = reader.readLine();
            }
            return basket;
        } catch (Throwable th) {
            throw new ClientException("Error parsing order csv file: " + th.getMessage(), th);
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    private Order createOrder(String[] header, String[] values) {

        Order order = new Order();

        for (int i = 0; i < header.length; i++) {
            String h = header[i];
            String s = values[i];

            if (HEADER_TICKER.equals(h)) {
                TickerResolver tickerResolver = new TickerResolver(securityDao);
                Security security = tickerResolver.loadSecurity(s);
                if (security == null) {
                    throw new ClientException("Failed to load security by sybmol " + s);
                }
                order.setSecurityId(security.getId());

            } else if (HEADER_TRADE_SIDE.equals(h)) {
                try {
                    TradeSide tradeSide = TradeSide.valueOf(s);
                    order.setTradeSide(tradeSide);
                } catch (IllegalArgumentException e) {
                    throw new ClientException("Invalid trade side: " + s);
                }

            } else if (HEADER_AMOUNT.equals(h)) {
                try {
                    Long amount = Long.parseLong(s);
                    order.setAmount(amount);
                } catch (NumberFormatException e) {
                    throw new ClientException(e);
                }

            } else if (HEADER_PRICE.equals(h)) {
                try {
                    BigDecimal price = new BigDecimal(s);
                    order.setPriceLimit(price);
                } catch (NumberFormatException e) {
                    throw new ClientException(e);
                }

            } else {
                LOGGER.debug("Invalid order csv header: " + h);
            }
        }

        return order;
    }

    private String[] readHeader(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        String[] values = line.split(",");
        for (int i = 0; i < values.length; i++) {
            values[i] = values[i].trim();
        }
        return values;
    }

    /*private boolean areValuesEmpty(String[] values) {
        for (String s : values) {
            if (s != null && ! s.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }*/
}
