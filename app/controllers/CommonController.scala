package controllers

import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.persist.dsl.transaction
import com.datayes.invest.pms.web.model.writes.IndexWrites
import com.datayes.invest.pms.web.service.CommonService

import javax.inject.Inject
import play.api.libs.json.Json
import play.pms.PmsAction
import play.pms.PmsController
import play.pms.PmsResult

class CommonController extends PmsController with Logging {
  
  @Inject
  private var commonService: CommonService = null

  def industries = PmsAction { implicit req =>
    val inds = transaction {
      commonService.getIndustries()
    }
    val arr = Json.toJson(inds)
    PmsResult(arr)
  }

  def marketIndexes = PmsAction { implicit req =>
    val indexes = transaction {
      commonService.getMarketIndexes()
    }
    val arr = Json.toJson(indexes)
    PmsResult(arr)
  }
}