package com.datayes.invest.pms.web.sso

import play.api.mvc._

// TODO replace with real AuthAction
object AuthAction {
  
  def apply(func: => Result): Action[AnyContent] = apply(_ => func)

  def apply(block: Request[AnyContent] => Result): Action[AnyContent] = Action(block)

  def apply[A](bodyParser: BodyParser[A])(block: (Request[A]) => Result): Action[A] = Action(bodyParser)(block)
}
