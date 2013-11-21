package test.gsonscala

import scala.collection.mutable.{ Buffer, ListBuffer }
import com.datayes.invest.pms.util.gson.PmsGsonBuilder

object TestPmsGson {

  def main(args: Array[String]): Unit = {
//    val buffer = ListBuffer.empty[Int]
//    buffer.append(1)
//    buffer.append(2)
//    val buffer: Buffer[Int] = ListBuffer(1, 2, 3)
    val buffer = Seq(1,2,3)
    val gson = new PmsGsonBuilder().create()
    val json = gson.toJson(buffer)
    println(json)
  }
}