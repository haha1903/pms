package com.datayes.invest.pms.dao.account.impl;

import com.datayes.invest.pms.dao.account.PositionInitDao;
import com.datayes.invest.pms.entity.account.PositionInit;

import javax.persistence.Query;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * System: Ubuntu
 * User: baoan @datayes
 * Date: 9/2/13
 * Time: 8:18 PM
 */
public class PositionInitDaoImpl extends GenericAccountMasterDaoImpl<PositionInit, Long>
        implements PositionInitDao {

    protected PositionInitDaoImpl() {
        super(PositionInit.class);
    }
}
