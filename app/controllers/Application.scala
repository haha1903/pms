package controllers

import play.api.mvc._
import com.datayes.invest.pms.config.Config
import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.web.sso.AuthAction


class Application extends Controller with Logging {

  private val f2eHost = Config.INSTANCE.getString("pms.f2e.host", "f2e.datayes.com")

  private val pmsUrl = Config.INSTANCE.getString("pms.url", "localhost:9000")

  def index = AuthAction { implicit req =>
    val username = "" //if (user == null) "" else user.getName()
    Ok(views.html.index(pmsUrl, f2eHost, "10415", "通联数据", username))
  }

  def tools = AuthAction { implicit request =>
    Ok(views.html.tools.main(request.host))
  }

  def saml(path: String) = AuthAction { implicit request =>
    BadRequest    // Fake response, all logic should be handled by AuthAction
  }
}