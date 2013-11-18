package com.datayes.invest.pms.web.sso

import com.datayes.invest.pms.logging.Logging
import play.api.mvc._
import com.datayes.invest.pms.config.Config

class AuthAction[A](bodyParser: BodyParser[A])(block: (Request[A]) => Result) extends Action[A] with SamlActionTrait[A] with Logging {
  
  val parser = bodyParser

  val userContextHandler = UserContextHandler.SessionHandler

  def apply(request: Request[A]): Result = if (AuthAction.ssoEnabled) {
    doSaml(parser)(request)(block)
  } else {
    block(request)
  }
}

object AuthAction {
  
  private lazy val ssoEnabled = Config.INSTANCE.getBoolean("paas.sso.enabled", false)
  
  def apply[A](bodyParser: BodyParser[A])(block: Request[A] => Result) = new AuthAction[A](bodyParser)(block)
  
  def apply(block: Request[AnyContent] => Result): Action[AnyContent] = apply(BodyParsers.parse.anyContent)(block)
}