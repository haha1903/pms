package com.datayes.invest.platform.logging

trait Logging {
  
  protected val logger = new Logger(this.getClass)
}
