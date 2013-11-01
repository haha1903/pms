package com.datayes.invest.pms.dao.account;

import java.io.Serializable;
import java.util.List;

public interface GenericAccountMasterDao<T, K extends Serializable> {

    T findById(K id);
    
    void save(T entity);
    
    void update(T entity);
    
    void delete(T entity);

    void detach(T entity);
    
    List<T> findWithPagination(int pageSize, int page);
    
    long findCount();
}
