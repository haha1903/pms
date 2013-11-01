package com.datayes.invest.pms.dao.account;

import com.datayes.invest.pms.entity.account.Fee;

public interface FeeDao extends AccountRelatedGenericDao<Fee, Long> {

    void deleteByAccountId(Long accountId);
}
