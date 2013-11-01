package com.datayes.invest.pms.dao.account;

public interface SystemIdMappingDao {

    Long findPmsId(String otherSystemId, String idName, String otherSystemName);
}