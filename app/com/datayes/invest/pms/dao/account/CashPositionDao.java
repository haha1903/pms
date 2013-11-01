package com.datayes.invest.pms.dao.account;

import com.datayes.invest.pms.entity.account.CashPosition;


public interface CashPositionDao extends AccountRelatedGenericDao<CashPosition, Long> {

    CashPosition findByAccountIdLedgerId(Long accountId, Long ledgerId);
}
