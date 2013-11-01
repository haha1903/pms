package com.datayes.invest.pms.service.calendar;

import org.joda.time.LocalDate;

public interface CalendarService {

    boolean isTradeDay(LocalDate date);
    
    LocalDate previousTradeDay(LocalDate date);
    
    LocalDate sameOrPreviousTradeDay(LocalDate date);
    
    LocalDate nextCalendarDay(LocalDate date);
}
