package com.datayes.invest.pms.dao.account;

import com.datayes.invest.pms.entity.account.PositionYield;
import org.joda.time.LocalDate;

import java.util.List;


public interface PositionYieldDao extends GenericAccountMasterDao<PositionYield, Long>{

    List<PositionYield> findByPositionIdsAsOfDate(List<Long> positionIds, LocalDate asOfDate);
    
    void deleteByAccountId(Long accountId);
}
