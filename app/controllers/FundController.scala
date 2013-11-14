package controllers

import play.api.mvc.Controller
import controllers.util.Jsonp
import com.datayes.invest.pms.logging.Logging
import javax.inject.Inject
import com.datayes.invest.pms.web.sso.AuthAction
import com.datayes.invest.pms.web.service.FundService
import com.datayes.invest.pms.web.model.models.ModelWrites._
import play.api.libs.json.Json



class FundController extends Controller with AsOfDateSupport with Jsonp with Logging {

}
