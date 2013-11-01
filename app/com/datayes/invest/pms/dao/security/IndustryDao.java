package com.datayes.invest.pms.dao.security;

import com.datayes.invest.pms.entity.security.Industry;

import java.util.List;

public interface IndustryDao extends GenericSecurityMasterDao<Industry, Integer> {

    public List<Industry> findByDataSourceIdClassLevel(int dataSourceId, int classLevel);

}
