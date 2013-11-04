package com.datayes.invest.pms.dao.account;

import java.util.List;

import org.joda.time.LocalDate;

import com.datayes.invest.pms.entity.account.CarryingValueHist;

public interface CarryingValueHistDao extends GenericAccountMasterDao<CarryingValueHist, CarryingValueHist.PK> {

    List<CarryingValueHist> findByPositionIdListTypeIdAsOfDate(List<Long> positionIdList, Long typeId, LocalDate asOfDate);
    
    List<CarryingValueHist> findByPositionIdListAsOfDate(List<Long> positionIdList, LocalDate asOfDate);
    
    void deleteByAccountIdAsOfDate(Long accountId, LocalDate asOfDate);

    void deleteByAccountId(Long accountId);
}
