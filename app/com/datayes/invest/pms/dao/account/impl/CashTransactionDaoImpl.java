package com.datayes.invest.pms.dao.account.impl;

import com.datayes.invest.pms.dao.account.CashTransactionDao;
import com.datayes.invest.pms.entity.account.CashTransaction;

public class CashTransactionDaoImpl extends AccountRelatedDaoImpl<CashTransaction, Long>
    implements CashTransactionDao {

    protected CashTransactionDaoImpl() {
        super(CashTransaction.class);
    }
}
