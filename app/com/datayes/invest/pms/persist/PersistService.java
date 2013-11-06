package com.datayes.invest.pms.persist;

public interface PersistService {
    
    Transaction beginTransaction();
    
    Transaction getTransaction();
    
    Transaction currentTransaction();
    
    void initialize();
}
