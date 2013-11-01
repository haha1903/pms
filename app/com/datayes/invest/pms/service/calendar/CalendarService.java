package com.datayes.invest.pms.service.calendar;

import org.joda.time.LocalDate;

public interface CalendarService {

    boolean isTradeDay(LocalDate date);
    
    LocalDate previousTradeDay(LocalDate date);
    
    LocalDate previousTradeDay(LocalDate date, String exchangeCode, int numOfDays);
    
    LocalDate nextTradeDay(LocalDate date);
    
    LocalDate nextTradeDay(LocalDate date, String exchangeCode, int numOfDays);
    
    LocalDate sameOrPreviousTradeDay(LocalDate date);
    
    LocalDate nextCalendarDay(LocalDate date);
    
    LocalDate previousCalendarDay(LocalDate date);
}
