package com.datayes.invest.pms.dao.account;

import java.util.List;

import org.joda.time.LocalDate;

import com.datayes.invest.pms.entity.account.PositionHist;

public interface PositionHistDao extends GenericAccountMasterDao<PositionHist, PositionHist.PK> {
    
    PositionHist findByPositionIdAsOfDate(Long positionId, LocalDate asOfDate);

    List<PositionHist> findByPositionIdListAsOfDate(List<Long> positionIdList, LocalDate asOfDate);

    void deleteByPositionId(Long accountId);
}
