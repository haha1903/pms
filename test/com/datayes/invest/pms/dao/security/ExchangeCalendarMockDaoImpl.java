package com.datayes.invest.pms.dao.security;

import com.datayes.invest.pms.dao.security.impl.GenericSecurityMasterDaoImpl;
import com.datayes.invest.pms.entity.security.ExchangeCalendar;
import org.joda.time.LocalDate;

import java.util.*;


public class ExchangeCalendarMockDaoImpl extends GenericSecurityMasterDaoImpl<ExchangeCalendar, Long>
        implements ExchangeCalendarDao {
    private Map<LocalDate, ExchangeCalendar> mockData = new LinkedHashMap<>();

    public ExchangeCalendarMockDaoImpl() {
        super(ExchangeCalendar.class);
    }

    public ExchangeCalendar findByDateExchangeCode(LocalDate date, String exchangeCode) {
        ExchangeCalendar exchangeCalendar = mockData.get(date);

        if(exchangeCalendar != null) {
            if(exchangeCalendar.getExchangeCode().equals(exchangeCode)) {
                return exchangeCalendar;
            }
        }

        return null;
    }

    @Override
    public List<ExchangeCalendar> findNextDaysByExchangeCode(LocalDate date, String exchangeCode, int numDays) {
        List<ExchangeCalendar> exchangeCalendars = new ArrayList<>();

        LocalDate limit = date.plusDays(numDays);

        Iterator<Map.Entry<LocalDate, ExchangeCalendar>> iterator = mockData.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry<LocalDate, ExchangeCalendar> entry = iterator.next();

            ExchangeCalendar exchangeCalendar = entry.getValue();
            LocalDate tempDate = exchangeCalendar.getDate();
            if((tempDate.isBefore(limit) && tempDate.isAfter(date)) || tempDate.equals(limit) || tempDate.equals(date)) {
                exchangeCalendars.add(exchangeCalendar);
            }
        }

        return exchangeCalendars;
    }

    @Override
    public List<ExchangeCalendar> findPreviousDaysByExchangeCode(LocalDate date, String exchangeCode, int numDays) {
        List<ExchangeCalendar> exchangeCalendars = new ArrayList<>();

        LocalDate limit = date.minusDays(numDays);

        Iterator<Map.Entry<LocalDate, ExchangeCalendar>> iterator = mockData.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry<LocalDate, ExchangeCalendar> entry = iterator.next();

            ExchangeCalendar exchangeCalendar = entry.getValue();
            LocalDate tempDate = exchangeCalendar.getDate();
            if((tempDate.isAfter(limit) && tempDate.isBefore(date)) || tempDate.equals(limit) || tempDate.equals(date)) {
                exchangeCalendars.add(exchangeCalendar);
            }
        }

        return exchangeCalendars;
    }


    public void addMockData(LocalDate date, boolean isTradeHoliday) {
        ExchangeCalendar exchangeCalendar1 = new ExchangeCalendar();
        exchangeCalendar1.setDate(date);
        exchangeCalendar1.setExchangeCode("XSHG");
        exchangeCalendar1.setTradeHoliday(isTradeHoliday);
        mockData.put(date, exchangeCalendar1);
    }
}
