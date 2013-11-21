package test.assetsloader

import javax.inject.Inject
import com.datayes.invest.pms.dao.account.AccountDao
import com.datayes.invest.pms.persist.dsl.transaction
import org.joda.time.LocalDate
import com.datayes.invest.pms.util.gson.PmsGsonBuilder
import com.datayes.invest.pms.web.assets.PortfolioLoader
import com.datayes.invest.pms.web.assets.TreeMaker
import com.datayes.invest.pms.web.assets.enums.AssetNodeType

import scala.collection.JavaConversions._

class TestWorker {

  @Inject
  private var accountDao: AccountDao = null
  
  @Inject
  private var portfolioLoader: PortfolioLoader = null
  
  private val gson = new PmsGsonBuilder().setPrettyPrinting().create()
  
  def run(): Unit = transaction {
    val asOfDate = LocalDate.now
    val accounts = accountDao.findEffectiveAccounts(asOfDate)
    val allAssets = portfolioLoader.load(accounts, asOfDate, None)
    
    val groupings = List(AssetNodeType.ACCOUNT, AssetNodeType.ASSET_CLASS, AssetNodeType.INDUSTRY)
    val tree = new TreeMaker(groupings, accounts).create(allAssets)
    val json = gson.toJson(tree)
    println()
    println(json)
  }
}