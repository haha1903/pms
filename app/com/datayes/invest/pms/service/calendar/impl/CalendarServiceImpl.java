package com.datayes.invest.pms.service.calendar.impl;

import com.datayes.invest.pms.dao.security.ExchangeCalendarDao;
import com.datayes.invest.pms.entity.security.ExchangeCalendar;
import com.datayes.invest.pms.util.DefaultValues;
import com.google.inject.Inject;
import org.joda.time.LocalDate;

import com.datayes.invest.pms.service.calendar.CalendarService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CalendarServiceImpl implements CalendarService {
    @Inject
    private ExchangeCalendarDao exchangeCalendarDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(CalendarServiceImpl.class);


    @Override
    public boolean isTradeDay(LocalDate date) {
        return isTradeDay(date, DefaultValues.SH_STOCK_EXCHANGE_CODE());
    }

    @Override
    public boolean isTradeDay(LocalDate date, String exchangeCode) {
        if(null == date) {
            throw new RuntimeException("CalendarServiceImpl.isTradeDay(): \"AsOfDay is null: " +
                    date.toString() + "\"");
        }

        ExchangeCalendar exchangeCalendar =
                exchangeCalendarDao.findByDateExchangeCode(date, exchangeCode);
        if(null == exchangeCalendar) {
            LOGGER.error("Cannot find asOfDay in DB: {}", date.toString());
            return false;
        }

        return !exchangeCalendar.isTradeHoliday();
    }

    private int adjustNumOfDays(int numOfDays) {
        int adjustNum = numOfDays;
        if(numOfDays <= DefaultValues.CALENDAR_FIND_NUMBER()) {
            adjustNum += DefaultValues.CALENDAR_FIND_NUMBER();
        }
        else {
            adjustNum *= DefaultValues.CALENDAR_FIND_RATIO();
        }

        return adjustNum;
    }

    private LocalDate findNumTradeDay(List<ExchangeCalendar> exchangeCalendars, int numOfDays) {
        int count = 0;

        // Loop on all days and find the "numOfDays" trade day
        for(ExchangeCalendar ec : exchangeCalendars) {
            if(!ec.isTradeHoliday()) {
                if(numOfDays == ++count) {
                    return ec.getDate();
                }
            }
        }

        return null;
    }

    @Override
    public LocalDate previousTradeDay(LocalDate date, String exchangeCode, int numOfDays) {
        if(null == date) {
            throw new RuntimeException("CalendarServiceImpl.previousTradeDay(): \"AsOfDay is null: " +
                    date.toString() + "\"");
        }

        // Define more days for looping
        int adjustNum  = adjustNumOfDays(numOfDays);

        // Get days from DB
        List<ExchangeCalendar> exchangeCalendars =
                exchangeCalendarDao.findPreviousDaysByExchangeCode(date, exchangeCode, adjustNum);
        if(null == exchangeCalendars || exchangeCalendars.isEmpty()) {
            LOGGER.error("Cannot not find {} Previous Trade Days in DB: {}", adjustNum, date.toString());
            return null;
        }

        // Find correct day
        return findNumTradeDay(exchangeCalendars, numOfDays);
    }

    @Override
    public LocalDate previousTradeDay(LocalDate date) {
        return previousTradeDay(date, DefaultValues.SH_STOCK_EXCHANGE_CODE(), 1);
    }

    @Override
    public LocalDate sameOrPreviousTradeDay(LocalDate date) {
        if(null == date) {
            throw new RuntimeException("CalendarServiceImpl.sameOrPreviousTradeDay(): \"AsOfDay is null: " +
                    date.toString() + "\"");
        }

        if(isTradeDay(date)) {
            return date;
        }

        return previousTradeDay(date);
    }

    @Override
    public LocalDate nextTradeDay(LocalDate date, String exchangeCode, int numOfDays) {
        if(null == date) {
            throw new RuntimeException("CalendarServiceImpl.previousTradeDay(): \"AsOfDay is null: " +
                    date.toString() + "\"");
        }

        // Define more days for looping
        int adjustNum  = adjustNumOfDays(numOfDays);

        // Get days from DB
        List<ExchangeCalendar> exchangeCalendars =
                exchangeCalendarDao.findNextDaysByExchangeCode(date, exchangeCode, adjustNum);
        if(null == exchangeCalendars || exchangeCalendars.isEmpty()) {
            LOGGER.error("Cannot not find {} Previous Trade Days in DB: {}", adjustNum, date.toString());
            return null;
        }

        // Find correct day
        return findNumTradeDay(exchangeCalendars, numOfDays);
    }

    @Override
    public LocalDate nextTradeDay(LocalDate date) {
        return nextTradeDay(date, DefaultValues.SH_STOCK_EXCHANGE_CODE(), 1);
    }


    @Override
    public LocalDate nextCalendarDay(LocalDate date) {
        if(null == date) {
            throw new RuntimeException("CalendarServiceImpl.nextCalendarDay(): \"AsOfDay is null: " +
                    date.toString() + "\"");
        }

        return date.plusDays(1);
    }

    @Override
    public LocalDate previousCalendarDay(LocalDate date) {
        if(null == date) {
            throw new RuntimeException("CalendarServiceImpl.nextCalendarDay(): \"AsOfDay is null: " +
                    date.toString() + "\"");
        }

        return date.minusDays(1);
    }







}
