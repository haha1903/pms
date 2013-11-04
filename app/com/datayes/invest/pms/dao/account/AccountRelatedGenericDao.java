package com.datayes.invest.pms.dao.account;

import java.io.Serializable;
import java.util.List;

public interface AccountRelatedGenericDao<T, K extends Serializable> extends GenericAccountMasterDao<T, K> {

    List<T> findByAccountId(Long accountId);
}
