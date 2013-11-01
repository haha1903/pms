package com.datayes.invest.pms.persist;

public interface Transaction {

    void commit();
    
    void rollback();
}
