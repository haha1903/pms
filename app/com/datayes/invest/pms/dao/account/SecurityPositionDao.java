package com.datayes.invest.pms.dao.account;

import org.joda.time.LocalDate;

import com.datayes.invest.pms.entity.account.SecurityPosition;

public interface SecurityPositionDao extends AccountRelatedGenericDao<SecurityPosition, Long> {

	SecurityPosition findSecurityPosition(Long accountId, Long securityId, Long ledgerId);

	SecurityPosition findByAccountIdSecurityIdLedgerIdOpenDate(Long accountId, Long securityId, Long ledgerId,
			LocalDate openDate);
}