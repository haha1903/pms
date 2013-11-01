package com.datayes.invest.pms.service.calendar.impl;

import org.joda.time.LocalDate;

import com.datayes.invest.pms.service.calendar.CalendarService;

public class CalendarServiceImpl implements CalendarService {

    @Override
    public boolean isTradeDay(LocalDate date) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public LocalDate previousTradeDay(LocalDate date) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LocalDate sameOrPreviousTradeDay(LocalDate date) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LocalDate nextCalendarDay(LocalDate date) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LocalDate previousCalendarDay(LocalDate date) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LocalDate previousTradeDay(LocalDate date, String exchangeCode, int numOfDays) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LocalDate nextTradeDay(LocalDate date) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LocalDate nextTradeDay(LocalDate date, String exchangeCode, int numOfDays) {
        // TODO Auto-generated method stub
        return null;
    }
}
