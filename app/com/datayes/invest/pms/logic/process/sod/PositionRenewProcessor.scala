package com.datayes.invest.pms.logic.process.sod

import org.joda.time.LocalDate
import com.datayes.invest.pms.logging.Logging
import scala.collection.JavaConversions._
import javax.inject.Inject
import com.datayes.invest.pms.dao.account.PositionDao
import com.datayes.invest.pms.dao.account.PositionInitDao
import com.datayes.invest.pms.dao.account.PositionHistDao
import com.datayes.invest.pms.dao.account.CarryingValueHistDao
import com.datayes.invest.pms.dao.account.SecurityTransactionDao
import com.datayes.invest.pms.logic.transaction.impl.RepoTransactionLogic
import com.datayes.invest.pms.entity.account.Account
import com.datayes.invest.pms.logic.process.Processor
import com.datayes.invest.pms.entity.account.Position
import com.datayes.invest.pms.entity.account.PositionHist
import com.datayes.invest.pms.entity.account.SecurityPosition
import com.datayes.invest.pms.entity.account.CashPosition
import com.datayes.invest.pms.entity.account.CarryingValueHist
import com.datayes.invest.pms.util.DefaultValues


class PositionRenewProcessor extends Processor with Logging {

  @Inject
  private var positionDao: PositionDao = null

  @Inject
  private var positionInitDao: PositionInitDao = null

  @Inject
  private var positionHistDao: PositionHistDao = null

  @Inject
  private var carryingValueHistDao: CarryingValueHistDao = null
  
  @Inject
  private var securityTransactionDao: SecurityTransactionDao = null
  
  @Inject
  private var repoTransactionLogic: RepoTransactionLogic = null

  def process(account: Account, asOfDate: LocalDate): Unit = {
    logger.info("Start renewing positions")
    
//    deletePositionHists(account, asOfDate)  Temporary comment out
    carryingValueHistDao.deleteByAccountIdAsOfDate(account.getId, asOfDate)

    if (account.getOpenDate == null) {
      throw new RuntimeException("Data error. Account OpenDate cannot be null on Account #" +
        account.getId)
    }
    val openDate = account.getOpenDate.toLocalDate()

    if (openDate.compareTo(asOfDate) > 0) {
      logger.warn("Business logic error. Cannot renew account #" + account.getId +
        " because renewal asOfDate (" + asOfDate + ") is earlier than account's open date (" +
        openDate + ")")
    } else if (openDate.compareTo(asOfDate) < 0) {
      refreshAccount(account, asOfDate)
    } else {
      refreshAccountFromInit(account, openDate)
    }

    logger.debug("Positions on account #{} renew successfully", account.getId)
  }

  /* 
   * Temporarily comment out delete position hist
   * 
   * private def deletePositionHists(account: Account, asOfDate: LocalDate): Unit = {
    val positions = positionDao.findByAccountId(account.getId)
    for (p <- positions) {
      val ph = positionHistDao.findByPositionIdAsOfDate(p.getId, asOfDate)
      if (ph != null) {
        positionHistDao.delete(ph)
      }
    }
  }*/

  private def refreshAccountFromInit(account: Account, openDate: LocalDate): Unit = {
    val accountId = account.getId
    val positionList = positionDao.findByAccountId(accountId)

    for (i <- 0 to positionList.size - 1) {
      val position = positionList.get(i)
      refreshPositionFromInit(position, openDate)
    }
  }

  private def refreshPositionFromInit(position: Position, accountOpenDate: LocalDate): Unit = {
    val positionInit = positionInitDao.findById(position.getId)
    val positionOpenDate = if (position.getOpenDate != null) {
      position.getOpenDate
    } else {
      null
    }

    // Check if the position exists in position init data
    if (positionOpenDate == null) {
      throw new RuntimeException("Data error. Position #" + position.getId + " open date is null")
    }

    if (positionOpenDate.toLocalDate.compareTo(accountOpenDate) < 0) {
      throw new RuntimeException("Data error. Position #" + position.getId + " OpenDate " +
        positionOpenDate + " is earlier than Account #" + position.getAccountId + " OpenDate " +
        accountOpenDate)
    }

    if (positionInit == null || positionOpenDate.toLocalDate.compareTo(accountOpenDate) > 0) {
      logger.debug("No position init data found for position #{}. Skip refresh position #{} on {}",
        position.getId, position.getId, accountOpenDate)
      return
    }

    // create position hist
    val quantity = if (positionInit.getQuantity == null) BigDecimal(0.0) else positionInit.getQuantity
    val positionHistPK = new PositionHist.PK(position.getId, accountOpenDate)
    createOrUpdatePositionHist(positionHistPK, quantity, null)
//    val positionHist = new PositionHist(
//      new PositionHist.PK(position.getId, accountOpenDate),
//      quantity,
//      null)
//    positionHistDao.save(positionHist)

    // create position's carrying value hist
    val carryingValue = if (positionInit.getCarryingValue == null) BigDecimal(0.0) else positionInit.getCarryingValue
    val currencyCode = position match {
      case s: SecurityPosition => s.getCurrencyCode
      case c: CashPosition => c.getCurrencyCode
      case x => throw new RuntimeException("Unable to handle position " + x + " where positionId = " + position.getId)
    }
    val carryingValueHist = new CarryingValueHist(
      new CarryingValueHist.PK(position.getId, DefaultValues.CARRYING_VALUE_TYPE, accountOpenDate),
      position.getAccountId,
      carryingValue,
      currencyCode,
      positionOpenDate)
    carryingValueHistDao.save(carryingValueHist)
  }

  private def refreshAccount(account: Account, asOfDate: LocalDate): Unit = {
    val accountId = account.getId
    val positions = positionDao.findByAccountId(accountId)
    val previousDay = asOfDate.minusDays(1)

    val prevPositionHists = loadPositionHists(positions, previousDay)
    val prevCarryingValueHists = loadCarryingValueHists(positions, previousDay)

    for (p <- positions) {
      val positionId = p.getId.toLong

      prevPositionHists.get(positionId) match {
        case Some(hist) =>
          refreshPositionHist(hist, asOfDate)
        case None =>
          logger.error("Failed to find position hist for position #{} on {}", positionId, previousDay)
      }

      prevCarryingValueHists.get(positionId) match {
        case Some(hist) =>
          refreshCarryingValueHist(hist, asOfDate)
        case None =>
          if (!p.isInstanceOf[CashPosition]) {
            logger.error("Failed to find carrying value hist for position #{} on {}", positionId, previousDay)
          }
      }
    }
    
    processRepoPositions(accountId, asOfDate)
  }
  
  private def processRepoPositions(accountId: Long, asOfDate: LocalDate) = {
    var transactionsWithInterests = securityTransactionDao.findRepoTransactionWithInterests(accountId, asOfDate)
    repoTransactionLogic.createCashInterestPositions(accountId, transactionsWithInterests, asOfDate)
    
    var expiredTransactions = securityTransactionDao.findExpiredRepoTransaction(accountId, asOfDate)    
   	repoTransactionLogic.removeSecurityPositions(expiredTransactions, asOfDate)
  }

  private def loadCarryingValueHists(positions: Seq[Position], asOfDate: LocalDate): Map[Long, CarryingValueHist] = {
    val ids = positions.map(_.getId)
    val hists = carryingValueHistDao.findByPositionIdListTypeIdAsOfDate(ids, DefaultValues.CARRYING_VALUE_TYPE, asOfDate)
    val map = hists.map { h => (h.getPK.getPositionId.toLong -> h) }.toMap
    map
  }

  private def loadPositionHists(positions: Seq[Position], asOfDate: LocalDate): Map[Long, PositionHist] = {
    val ids = positions.map(_.getId)
    val hists = positionHistDao.findByPositionIdListAsOfDate(ids, asOfDate)
    val map = hists.map { h => (h.getPK.getPositionId.toLong -> h) }.toMap
    map
  }

  private def refreshPositionHist(previousPositionHist: PositionHist, asOfDate: LocalDate): Unit = {
    val newPK = new PositionHist.PK(previousPositionHist.getPK.getPositionId, asOfDate)
    val quantity = previousPositionHist.getQuantity()
    val settleQty = previousPositionHist.getSettleQty()
    createOrUpdatePositionHist(newPK, quantity, settleQty)
  }
  
  private def createOrUpdatePositionHist(pk: PositionHist.PK, quantity: BigDecimal, settleQty: BigDecimal): Unit = {
    val existingHist = positionHistDao.findById(pk)
    if (existingHist == null) {
      val newPositionHist = new PositionHist(pk, quantity, settleQty)
      positionHistDao.save(newPositionHist)
    } else {
      existingHist.setQuantity(quantity)
      existingHist.setSettleQty(settleQty)
      positionHistDao.update(existingHist)
    }
  }

  private def refreshCarryingValueHist(previousCarryingValueHist: CarryingValueHist, asOfDate: LocalDate): Unit = {
    val newPK = new CarryingValueHist.PK(previousCarryingValueHist.getPK.getPositionId,
      previousCarryingValueHist.getPK.getTypeId, asOfDate)
    val todaysCarryingValueHist = new CarryingValueHist(newPK, previousCarryingValueHist)
    carryingValueHistDao.save(todaysCarryingValueHist)
  }
}
