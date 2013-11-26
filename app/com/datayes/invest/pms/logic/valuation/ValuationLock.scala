package com.datayes.invest.pms.logic.valuation

import java.util.concurrent.locks.ReentrantLock
import com.datayes.invest.pms.logging.Logging

object ValuationLock extends Logging {
  
  private val lock = new ReentrantLock()

  def withLocking[R](op: => R): R = {
    try {
      logger.debug("Waiting for valuation lock")
      lock.lock()
      val r = op
      r
    } finally {
      lock.unlock()
    }
  }
}