package com.datayes.invest.pms.dao.account;

import com.datayes.invest.pms.entity.account.PositionValuationType;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * System: Ubuntu
 * User: baoan @datayes
 * Date: 8/27/13
 * Time: 4:31 PM
 */
public interface PositionValuationTypeDao extends GenericAccountMasterDao<PositionValuationType, Long> {
    public List<PositionValuationType> findAll();
}
