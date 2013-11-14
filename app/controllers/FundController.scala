package controllers


import com.datayes.invest.pms.logging.Logging
import javax.inject.Inject
import com.datayes.invest.pms.web.service.FundService
import com.datayes.invest.pms.web.model.models.ModelWrites._
import play.api.libs.json.Json
import play.pms.{PmsAction, PmsController}
import org.joda.time.LocalDate
import com.datayes.invest.pms.userpref.UserPref


class FundController extends PmsController with Logging {
  
  @Inject
  private var fundService: FundService = null
  
  @Inject
  private var userPref: UserPref = null

  def getSummary() = PmsAction { implicit req =>
    val accountId: Long = param("accountId")
    val asOfDate: LocalDate = paramAsOfDateOrToday
    val summary = fundService.getSummary(accountId, asOfDate)
    Json.toJson(summary)
  }
  
  def getDashboardLayout = PmsAction { implicit req =>
    val config = userPref.getDashboardLayoutConfig()
    val json = Json.obj("config" -> config)
    json
  }
  
  def setDashboardLayout = PmsAction { implicit req =>
    val config: String = param("config")
    
    val (result, message) = try {
      userPref.setDashboardLayoutConfig(config)
      (true, "layout config has bean saved")
    } catch {
      case e: Throwable => (false, e.getMessage)
    }
    val json = Json.obj("success" -> result, "message" -> message)
    json
  }
}
