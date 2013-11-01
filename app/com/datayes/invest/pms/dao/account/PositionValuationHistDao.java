package com.datayes.invest.pms.dao.account;

import java.util.List;

import org.joda.time.LocalDate;

import com.datayes.invest.pms.entity.account.PositionValuationHist;

public interface PositionValuationHistDao extends GenericAccountMasterDao<PositionValuationHist, Long> {
    
    PositionValuationHist findByPositionIdAsOfDate(Long positionId, Long typeId, LocalDate asOfDate);

    List<PositionValuationHist> findByPositionIdListAsOfDate(List<Long> positionIdList, Long typeId, LocalDate asOfDate);
    
    List<PositionValuationHist> findByPositionIdListAsOfDate(List<Long> positionIdList, LocalDate asOfDate);

    void deleteByPositionId(Long positionId);
}
