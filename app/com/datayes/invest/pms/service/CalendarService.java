package com.datayes.invest.pms.service;

import org.joda.time.LocalDate;

public interface CalendarService {

    boolean isTradeDay(LocalDate date);
    
    LocalDate previousTradeDay(LocalDate date);
    
    LocalDate sameOrPreviousTradeDay(LocalDate date);
}
