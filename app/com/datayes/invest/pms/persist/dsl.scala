package com.datayes.invest.platform.persist

import scala.collection.JavaConversions._
import com.datayes.invest.platform.logging.Logging
import javax.persistence.EntityTransaction
import com.datayes.invest.pms.persist.Transaction
import com.datayes.invest.pms.persist.Persist
import com.datayes.invest.pms.persist.PersistUnit


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