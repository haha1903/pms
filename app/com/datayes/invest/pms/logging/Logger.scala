package com.datayes.invest.platform.logging

import org.slf4j.LoggerFactory


class Logger(classType: Class[_]) {

  private val logger = LoggerFactory.getLogger(classType)

  def trace(format: String, args: Any*){
    if (logger.isTraceEnabled()) {
      val array = args.map(_.asInstanceOf[AnyRef]).toArray
      logger.trace(format, array:_*)
    }
  }

  def info(format: String, args: Any*){
    if(logger.isInfoEnabled) {
      val array = args.map(_.asInstanceOf[AnyRef]).toArray
      logger.info(format, array:_*)
    }
  }

  def debug(format: String, args: Any*){
    if(logger.isDebugEnabled) {
      val array = args.map(_.asInstanceOf[AnyRef]).toArray
      logger.debug(format, array:_*)
    }
  }

  def warn(format: String, args: Any*){
    if(logger.isWarnEnabled) {
      val array = args.map(_.asInstanceOf[AnyRef]).toArray
      logger.warn(format, array:_*)
    }
  }

  def error(format: String, args: Any*){
    if(logger.isErrorEnabled) {
      val array = args.map(_.asInstanceOf[AnyRef]).toArray
      logger.error(format, array:_*)
    }
  }
}
