package com.datayes.invest.pms.dao.security;

import java.io.Serializable;
import java.util.List;

public interface GenericSecurityMasterDao<T, K extends Serializable> {

    T findById(K id);
    
    List<T> findWithPagination(int pageSize, int page);
    
    long findCount();

    void detach(T entity);
}
