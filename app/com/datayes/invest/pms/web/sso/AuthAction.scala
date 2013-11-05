package com.datayes.invest.pms.web.sso

import com.datayes.invest.pms.logging.Logging
import play.api.mvc._
import com.datayes.invest.pms.config.Config

object AuthAction extends SamlActionTrait with Logging {

  val userContextHandler = UserContextHandler.CookieHandler

  private lazy val ssoEnabled = Config.INSTANCE.getBoolean("paas.sso.enabled", false)

  def apply(func: => Result): Action[AnyContent] = apply(_ => func)

  def apply(block: Request[AnyContent] => Result): Action[AnyContent] = if (ssoEnabled) {
    doSaml(block)
  } else {
    Action(block)
  }

  def apply[A](bodyParser: BodyParser[A])(block: (Request[A]) => Result): Action[A] = if (ssoEnabled) {
    doSaml(bodyParser)(block)
  } else {
    Action(bodyParser)(block)
  }
}
