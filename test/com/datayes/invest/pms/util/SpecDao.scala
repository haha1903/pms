package com.datayes.invest.pms.util

import com.datayes.invest.pms.dao.account.impl._
import com.datayes.invest.pms.dao.security.impl.SecurityDaoImpl

/**
 * Created by changhai on 13-12-12.
 */
trait SpecDao extends SpecUtil {
  val accountDao = newInstance(classOf[AccountDaoImpl])
  val securityDao = newInstance(classOf[SecurityDaoImpl])
  val cashPositionDao = newInstance(classOf[CashPositionDaoImpl])
  val securityPositionDao = newInstance(classOf[SecurityPositionDaoImpl])
  val positionHistDao = newInstance(classOf[PositionHistDaoImpl])
  val carryingValueHistDao = newInstance(classOf[CarryingValueHistDaoImpl])
}
