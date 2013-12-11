package com.datayes.invest.pms.dao.account;

import com.datayes.invest.pms.entity.account.Position;
import org.joda.time.LocalDate;

import java.util.List;

public interface PositionDao extends AccountRelatedGenericDao<Position, Long> {
    List<Position> findByAccountIdBeforeAsOfDate(Long accountId, LocalDate asOfDate);
}
