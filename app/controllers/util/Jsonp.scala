package controllers.util

import play.api.libs.json.JsValue
import play.api.mvc.Results.Ok
import play.api.mvc.{Result, Request, AnyContent}

/**
 * This trait should be used in controllers for responding either json or jsonp
 * response. It responds jsonp if the "callback" parameter exist, otherwise it
 * respnds json.
 */
trait Jsonp {

  protected def respondJsonOrJsonp(json: JsValue)(implicit req: Request[AnyContent]): Result = {
    req.queryString.get("callback") flatMap (_.headOption) match {
      case Some(callback) =>
        Ok(play.api.libs.Jsonp(callback, json))
      case None =>
        Ok(json)
    }
  }

  protected def respondJsonOrJsonp(json: String)(implicit req: Request[AnyContent]): Result = {
    req.queryString.get("callback") flatMap (_.headOption) match {
      case Some(callback) =>
        Ok(callback + "(" + json + ")").as("application/javascript; charset=utf-8")
      case None =>
        Ok(json).as("application/json; charset=utf-8")
    }
  }
}
