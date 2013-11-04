package com.datayes.invest.pms.play

import com.datayes.invest.pms.logging.Logging

import play.api.mvc.{Result, RequestHeader, Filter}

class LoggingFilter extends Filter with Logging {

  override def apply(next: (RequestHeader) => Result)(rh: RequestHeader): Result = {
    logger.debug("{} {} from {}", rh.method, rh.path, rh.remoteAddress)
    next(rh)
  }
}
