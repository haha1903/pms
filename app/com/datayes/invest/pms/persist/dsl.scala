package com.datayes.invest.pms.persist

import com.datayes.invest.pms.logging.Logging

package object dsl extends Logging {

  def transaction[R](op: => R): R = {
    val tx: Transaction = Persist.beginTransaction()
    try {
      val r = op
      tx.commit()
      r
    } catch {
      case e: Throwable =>
        tx.rollback()
        throw e
    } finally {
      
    }
  }
}