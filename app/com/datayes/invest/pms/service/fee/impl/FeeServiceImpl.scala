package com.datayes.invest.pms.service.fee.impl

import javax.inject.Inject
import com.datayes.invest.pms.dao.account.FeeDao
import com.datayes.invest.pms.dbtype.RateType
import com.datayes.invest.pms.dbtype.TradeSide
import com.datayes.invest.pms.service.fee.FeeService
import scala.math.BigDecimal
import com.datayes.invest.pms.logging.Logging
import scala.collection.mutable
import com.datayes.invest.pms.entity.account.Fee
import scala.collection.JavaConversions._
import java.util.HashMap
import java.util.LinkedList
import scala.math.BigDecimal
import java.lang.{ Long => JLong }
import com.datayes.invest.pms.util.BigDecimalConstants


class FeeServiceImpl extends FeeService with Logging {

  @Inject
  private var feeDao: FeeDao = null

  private val map = mutable.Map.empty[Long, FeeHelper]
  
  def getRate(accountId: JLong, rateType: RateType, tradeSide: TradeSide, securityId: JLong): BigDecimal = {
    get(accountId) match {
      case Some(feeHelper) =>
        feeHelper.getFee(rateType, tradeSide, securityId)
      case None =>
        logger.warn("Failed to find rate for account: {}, rateType: {}, tradeSide: {}, securityId: {}",
          accountId, rateType, tradeSide, securityId)
        BigDecimalConstants.ZERO
    }
  }

  private def get(accountId: Long): Option[FeeHelper] = map.get(accountId) match {
    case Some(helper) => Some(helper)
    case None =>
      val list = Some(load(accountId))
      val helper = new FeeHelper(list)
      map.put(accountId, helper)
      Some(helper)
  }

  private def load(accountId: Long): List[Fee] = {
    val list = feeDao.findByAccountId(accountId)
    val retList = if (list != null) {
      list.foreach(feeDao.detach(_))
      list.toList
    } else {
      List.empty[Fee]
    }
    logger.debug("{} fees for account #{}", retList.size, accountId)
    retList
  }

}

class FeeHelper(fees: Option[List[Fee]]) {
  private var allFees: HashMap[Long, InternalFees] = init(fees)

  private def init(fees: Option[List[Fee]]): HashMap[Long, InternalFees] = {
    val allFees = new HashMap[Long, InternalFees]
    if (fees.isDefined) {
      for (fee <- fees.get) {
        var oneCache = allFees.get(fee.getRateTypeId)
        if (oneCache == null) {
          oneCache = new InternalFees(
            new LinkedList[Fee],
            new LinkedList[Fee],
            new LinkedList[Fee],
            new LinkedList[Fee])
          allFees.put(fee.getRateTypeId, oneCache)
        }

        if (fee.getSecurityId != null && fee.getTradeSideCode != null) {
          oneCache.level3.add(fee)
        } else if (fee.getSecurityId != null) {
          oneCache.level2.add(fee)
        } else if (fee.getTradeSideCode != null) {
          oneCache.level1.add(fee)
        } else {
          oneCache.level0.add(fee)
        }
      }
    }
    allFees
  }

  def getFee(rateType: RateType, side: TradeSide, securityId: Long): BigDecimal = {
    val oneCache = allFees.get(rateType.getDbValue)
    if (oneCache == null) {
      return BigDecimal.valueOf(0)
    }

    var result: Option[Fee] = None
    if (!oneCache.level3.isEmpty) {
      result = oneCache.level3.find { fee => fee.getTradeSideCode == side.getDbValue && fee.getSecurityId == securityId }
    } else if (!oneCache.level2.isEmpty) {
      result = oneCache.level2.find { fee => fee.getSecurityId == securityId }
    } else if (!oneCache.level1.isEmpty) {
      result = oneCache.level1.find { fee => fee.getTradeSideCode == side.getDbValue }
    }

    if (!result.isDefined && !oneCache.level0.isEmpty) {
      result = Some(oneCache.level0.get(0))
    }

    result match {
      case Some(fee) => fee.getRates
      case None => BigDecimal.valueOf(0)
    }
  }

  case class InternalFees(
    level3: LinkedList[Fee],
    level2: LinkedList[Fee],
    level1: LinkedList[Fee],
    level0: LinkedList[Fee])
}
