package com.datayes.invest.pms.persist;

public interface PersistService {
    
    Transaction beginTransaction();
    
    Transaction currentTransaction();
    
    void initialize();
}
