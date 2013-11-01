package com.datayes.invest.pms.persist;

public interface PersistService {
    
    Transaction beginTransaction(PersistUnit unit);
    
    Transaction currentTransaction();
    
    void initialize();
}
