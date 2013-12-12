package com.datayes.invest.pms.util

import com.datayes.invest.pms.dao.account.impl.AccountDaoImpl

/**
 * Created by changhai on 13-12-12.
 */
trait SpecDao extends SpecUtil {
  val accountDao = newInstance(classOf[AccountDaoImpl])
}
