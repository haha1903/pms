package com.datayes.invest.pms.persist

import scala.collection.JavaConversions._
import com.datayes.invest.pms.logging.Logging
import javax.persistence.EntityTransaction


package object dsl extends Logging {
  
  def transaction[R](op: => R): R = transaction(PersistUnit.ACCOUNT_MASTER)(op)

  def transaction[R](pu: PersistUnit)(op: => R): R = {
    val tx: Transaction = Persist.beginTransaction(pu)
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