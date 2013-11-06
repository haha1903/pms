package com.datayes.invest.pms.persist.hibernate;

import com.datayes.invest.pms.persist.Transaction;

public class DummyTransactionImpl implements Transaction {

    @Override
    public void commit() {
    }

    @Override
    public void rollback() {
    }
}
