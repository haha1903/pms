package com.datayes.invest.pms.util

import com.datayes.invest.pms.persist.hibernate.PersistServiceImpl
import com.datayes.invest.pms.persist.Persist

/**
 * Created by changhai on 13-12-12.
 */
trait SpecUtil {
  implicit def reflector(ref: AnyRef) = new {
    def getValue(name: String): Any = {
      val method = ref.getClass.getDeclaredMethods.find(_.getName == name).get
      method.setAccessible(true)
      method.invoke(ref)
    }

    def setValue(name: String, value: Any): Unit = {
      val field = ref.getClass.getDeclaredFields.find(_.getName.endsWith(name)).get
      field.setAccessible(true)
      field.set(ref, value.asInstanceOf[AnyRef])
    }
  }

  def newInstance[A](t: Class[A]): A = {
    val const = t.getDeclaredConstructor()
    const.setAccessible(true)
    const.newInstance().asInstanceOf[A]
  }

  val ps = new PersistServiceImpl
  Persist.setPersistService(ps)
}
