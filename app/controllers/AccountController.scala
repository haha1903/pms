package controllers

import org.joda.time.LocalDate

import com.datayes.invest.pms.logging.Logging
import com.datayes.invest.pms.persist.dsl.transaction
import com.datayes.invest.pms.web.model.writes.AccountWrites
import com.datayes.invest.pms.web.service.AccountService

import javax.inject.Inject
import play.api.libs.json.Json
import play.pms.PmsAction
import play.pms.PmsController

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
  
}