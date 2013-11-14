package com.datayes.invest.pms.web.service

import com.datayes.invest.pms.logging.Logging
import javax.inject.Inject
import com.datayes.invest.pms.dao.account.{AccountDao, AccountValuationHistDao}
import org.joda.time.LocalDate
import com.datayes.invest.pms.web.model.models.{AccountsSummaryItem, AccountsSummary}
import com.datayes.invest.pms.dbtype.{AccountValuationType, AccountTypeType}
import com.datayes.invest.pms.entity.account.{Account, AccountValuationHist}
import com.datayes.invest.pms.persist.dsl._
import scala.collection.JavaConversions._

class FundService extends Logging {

}
