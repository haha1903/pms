package com.datayes.invest.pms.web.service

import com.datayes.invest.pms.dao.account.AccountDao
import javax.inject.Inject
import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.web.model.models._
import org.joda.time.LocalDate
import scala.collection.JavaConversions._
import com.datayes.invest.pms.entity.account.Account
import com.datayes.invest.pms.web.model.models.AssetTree
import com.datayes.invest.pms.service.industry.IndustryService


class PortfolioService extends Logging {

  @Inject
  private var accountDao: AccountDao = null

  @Inject
  private var assetsLoader: AssetsLoader = null
  
  @Inject
  private var industryService: IndustryService = null

  // TODO Temporary variables for testing
  private var _totalNumOfPositions = 0

  def getAssetTree(asOfDate: LocalDate, groupings: List[AssetNodeType.Type], filterParam: FilterParam): AssetTree = {
    val accounts = accountDao.findEffectiveAccounts(asOfDate)
    val allValidAssets = accounts.flatMap(a => assetsLoader.loadAssets(a.getId, asOfDate)).filter { asset =>
      asset.assetClass != AssetClassType.future && asset.assetClass != AssetClassType.cash &&
      asset.assetClass != AssetClassType.none
    }
    val filteredAssets = FilterHelper.filterAssets(allValidAssets, filterParam)
    val accountIdNameMap = createAccountIdNameMap(accounts.toList)
    val treeMaker = new AssetTreeMaker(groupings, accountIdNameMap)
    treeMaker.create(filteredAssets)
  }

  def getAvailableIndustry(): List[String] = {
    industryService.getAvailableIndustries().toList
  }

  private def createAccountIdNameMap(accounts: List[Account]): Map[Long, String] =
    (for (a <- accounts) yield (a.getId.toLong -> a.getAccountName)).toMap
}