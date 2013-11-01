package com.datayes.invest.pms.dao.account;

import com.datayes.invest.pms.entity.account.AccountValuationType;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * System: Ubuntu
 * User: baoan @datayes
 * Date: 8/27/13
 * Time: 3:20 PM
 */
public interface AccountValuationTypeDao extends GenericAccountMasterDao<AccountValuationType, Long> {
    public List<AccountValuationType> findAll();
}
