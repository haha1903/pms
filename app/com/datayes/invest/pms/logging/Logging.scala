package com.datayes.invest.pms.logging

trait Logging {
  
  protected val logger = new Logger(this.getClass)
}
