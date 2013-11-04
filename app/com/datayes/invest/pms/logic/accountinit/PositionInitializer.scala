package com.datayes.invest.pms.logic.accountinit

import com.datayes.invest.pms.logging.Logging
import javax.inject.Inject
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import scala.math.BigDecimal
import com.datayes.invest.pms.dao.account.PositionDao
import com.datayes.invest.pms.dao.account.PositionInitDao
import com.datayes.invest.pms.dao.account.PositionHistDao
import com.datayes.invest.pms.dbtype.PositionClass
import com.datayes.invest.pms.dbtype.LedgerType
import com.datayes.invest.pms.entity.account.SecurityPosition
import com.datayes.invest.pms.entity.account.CashPosition
import com.datayes.invest.pms.entity.account.PositionInit
import com.datayes.invest.pms.entity.account.PositionHist

class PositionInitializer extends Logging {

  @Inject
  private var positionDao: PositionDao = null

  @Inject
  private var positionInitDao: PositionInitDao = null

  @Inject
  private var positionHistDao: PositionHistDao = null

  private[accountinit] def initializePosition(accountId: Long,
                         positionSourceData: PositionSourceData,
                         asOfDate: LocalDateTime,
                         quantity: BigDecimal,
                         settleQuant: BigDecimal): Long = {
    val positionId = createPosition(accountId, positionSourceData)
    createPositionInit(positionId, positionSourceData)
    createPositionHist(positionId, asOfDate.toLocalDate(), quantity, settleQuant)

    positionId
  }

  private[accountinit] def createSharePositionAndInitAndHist(accountId: Long,
                                        share: BigDecimal,
                                        currencyCode: String,
                                        asOfDate: LocalDateTime): Long = {

    val shareSrcData = new PositionSourceData(
      positionClass = PositionClass.CASH,
      ledgerType = LedgerType.SHARE,
      openDate = asOfDate,
      currencyCode = currencyCode,
      quantity = share,
      carryingValue = None,
      securityId = None,
      exchangeCode = "XSHG")

    val positionId = createPosition(accountId, shareSrcData)
    createPositionInit(positionId, shareSrcData)
    createPositionHist(positionId, asOfDate.toLocalDate(), share, null)
    positionId
  }

  private def createPosition(accountId: Long,
                     posSrcData: PositionSourceData): Long = {
    
    val position = posSrcData.positionClass match{
      case PositionClass.SECURITY =>  new SecurityPosition(
        accountId,
        posSrcData.positionClass.getDbValue,
        posSrcData.ledgerType.getDbValue,
        posSrcData.exchangeCode,
        posSrcData.currencyCode,
        posSrcData.openDate,
        "ACTIVE",
        posSrcData.openDate,
        posSrcData.securityId.get)

      case PositionClass.CASH => new CashPosition(
        accountId,
        posSrcData.positionClass.getDbValue,
        posSrcData.ledgerType.getDbValue,
        posSrcData.exchangeCode,
        posSrcData.currencyCode,
        posSrcData.openDate,
        "ACTIVE",
        posSrcData.openDate)

      case _ => throw new RuntimeException("Unable to create position with class code: " +
        posSrcData.positionClass + " please check")
    }

    positionDao.save(position)
    logger.info("position #{} created", position.getId)
    position.getId
  }

  private def createPositionInit(positionId: Long, posSrcData: PositionSourceData): Unit = {

    val positionInit = new PositionInit(
      positionId,
      posSrcData.quantity.bigDecimal,
      {
        if(posSrcData.carryingValue.isDefined)
         posSrcData.carryingValue.get.bigDecimal
        else
          null
      })

    positionInitDao.save(positionInit)
    logger.info("position init #{} created", positionInit.getPositionId)
  }

  private def createPositionHist(positionId: Long,
                         asOfDate: LocalDate,
                         quantity: BigDecimal,
                         settleQuant: BigDecimal): Unit = {

    val pk = new PositionHist.PK(positionId, asOfDate)

    val positionHist = new PositionHist(
      pk,
      quantity,
      if(settleQuant != null) settleQuant else null
      )

    positionHistDao.save(positionHist)
  }



}
