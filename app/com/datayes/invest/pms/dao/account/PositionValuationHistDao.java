package com.datayes.invest.pms.dao.account;

import java.util.List;

import org.joda.time.LocalDate;

import com.datayes.invest.pms.entity.account.PositionValuationHist;

public interface PositionValuationHistDao extends GenericAccountMasterDao<PositionValuationHist, PositionValuationHist.PK> {

    List<PositionValuationHist> findByPositionIdListTypeIdAsOfDate(List<Long> positionIdList, Long typeId, LocalDate asOfDate);
    
    List<PositionValuationHist> findByPositionIdListAsOfDate(List<Long> positionIdList, LocalDate asOfDate);

    void deleteByPositionId(Long positionId);
}
