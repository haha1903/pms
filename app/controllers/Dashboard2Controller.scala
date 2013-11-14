package controllers

import play.pms.PmsController
import com.datayes.invest.pms.logging.Logging
import play.pms.PmsAction
import javax.inject.Inject
import com.datayes.invest.pms.userpref.UserPref
import play.api.libs.json.Json

class Dashboard2Controller extends PmsController with Logging {
  
  @Inject
  private var userPref: UserPref = null

  def getLayout = PmsAction { implicit req =>
    val config = userPref.getDashboardLayoutConfig()
    val json = Json.obj("config" -> config)
    json
  }
  
  def setLayout = PmsAction { implicit req =>
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
