package com.datayes.invest.pms.dao.security;


import java.io.Serializable;
import java.util.List;

public class GenericSecurityMasterMockDaoImpl <T, K extends Serializable>
        implements GenericSecurityMasterDao<T, K> {


    @Override
    public T findById(K id) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<T> findWithPagination(int pageSize, int page) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long findCount() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void detach(T entity) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
