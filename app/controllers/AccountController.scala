package controllers

import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.persist.dsl.transaction
import play.pms.PmsController
import play.pms.PmsAction
import com.datayes.invest.pms.web.service.DashboardService
import javax.inject.Inject
import com.datayes.invest.pms.web.service.AccountService
import org.joda.time.LocalDate
import play.api.libs.json.Writes
import com.datayes.invest.pms.entity.account.Account
import play.api.libs.json._
import com.datayes.invest.pms.web.model.writes.LocalDateTimeWrites

class AccountController extends PmsController with Logging {
  
  @Inject
  private var accountService: AccountService = null

  def list = PmsAction { implicit req =>
    val asOfDate: LocalDate = paramAsOfDateOrToday()
    
    val accounts = transaction {
      accountService.getAccountList(asOfDate)
    }
    val json = Json.toJson(accounts)
    json
  }
  
  implicit object AccountWrites extends Writes[Account] {
    
    def writes(o: Account) = Json.obj(
      "id" -> o.getId().toLong,
      "name" -> o.getAccountName(),
      "accountNo" -> o.getAccountNo(),
      "countryCode" -> o.getCountryCode(),
      "currencyCode" -> o.getCurrencyCode(),
      "classCode" -> o.getAccountClass(),
      "openDate" -> o.getOpenDate()
    )
  }
}