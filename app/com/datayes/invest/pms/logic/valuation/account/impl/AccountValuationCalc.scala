package com.datayes.invest.pms.logic.valuation.account.impl


object AccountValuationCalc {

  def calculatePosition(values: List[BigDecimal]): BigDecimal = {

    values.foldLeft(BigDecimal(0))(_ + _)
  }

  def calculateFuture(longFuture: BigDecimal, shortFuture: BigDecimal): BigDecimal = {

    longFuture - shortFuture
  }
  
  def calculateMargin(balance: BigDecimal, futurePNL: BigDecimal): BigDecimal = {
    balance + futurePNL
  }

  def calculateAsset(security: BigDecimal,
                    cash: BigDecimal,
                    margin: BigDecimal,
                    receivableSettlement: BigDecimal,
                    repoReceivable: BigDecimal): BigDecimal = {
    security + cash + margin + receivableSettlement + repoReceivable
  }

  def calculateLiability(payableSettlement: BigDecimal,
    commission: BigDecimal, repoPayable: BigDecimal): BigDecimal = {

    payableSettlement + commission + repoPayable
  }

  def calculateNetWorth(asset: BigDecimal,
                        liabilityAmount: BigDecimal): BigDecimal = {

    //基金总资产 - 基金总负债
    asset - liabilityAmount
  }

  def calculateUnitNet(netWorth: BigDecimal,
                     share: BigDecimal): BigDecimal = {

    netWorth / share
  }

  def calculateDailyReturn(closingNet: BigDecimal,
                         outflow: BigDecimal,
                         previousNet: BigDecimal,
                         inflow: BigDecimal): BigDecimal = {

    //每日回报率 = （期末净值 + 当日资金流出 (赎回或者分红）） / （前一日净值+当日资金流入（申购、追加资金等）) - 1
    (closingNet + outflow) / (previousNet+ inflow) - BigDecimal(1)
  }

  def calculateProfitAndLoss(closingNet: BigDecimal,
                           sold: BigDecimal,
                           dividend: BigDecimal,
                           outflow: BigDecimal,
                           previousNet: BigDecimal,
                           buy: BigDecimal,
                           inflow: BigDecimal): BigDecimal = {


    //期末市值 + 卖出金额 + 分红金额 + 特殊业务流出 - （期初市值 + 买入金额 + 特殊业务流入）
    closingNet + sold + dividend + outflow - ( previousNet + buy + inflow )
  }
}
