package test.gsonscala

import scala.collection.mutable.ListBuffer
import com.google.gson.GsonBuilder
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonArray
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonWriter
import com.google.gson.Gson
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import java.lang.reflect.ParameterizedType
import test.gson.GsonExclude
import test.gson.GsonExcludeStrategy
import javax.transaction.NotSupportedException
import javax.persistence.Transient
import com.datayes.invest.pms.util.gson.PmsGsonBuilder

abstract class Node {
  
  def id: Int
  def name: String
  
  @GsonExclude
  val price: Double = 0.0
}

trait Filterable {
  def industry: String
}

class Tree(@GsonExclude val id: Int, val name: String) extends Node {
  var children: ListBuffer[Node] = ListBuffer.empty[Node]
}

class Security(@GsonExclude val id: Int, val name: String) extends Node with Filterable {
  
  @GsonExclude
  var marketValue: Long = 0L
  
  var industry: String = null
}

object TestGson {
  def main(args: Array[String]): Unit = {
    
    val root = new Tree(1, "root")
    val secondLevel = new Tree(2, "2nd level")
    val leaf = new Security(3, "MS")
    leaf.industry = "Finance"
    leaf.marketValue = 200L
    
    root.children.append(secondLevel)
    secondLevel.children.append(leaf)
    
    for (i <- 0 until 10) {
      val lf = new Security(i, "MS-" + i)
      secondLevel.children.append(lf)
    }
    
//    val builder = new GsonBuilder().serializeNulls().setExclusionStrategies(new GsonExcludeStrategy)
////    builder.registerTypeAdapter(classOf[ListBuffer[Node]], new ListBufferTypeAdapter())
////    builder.registerTypeAdapter(classOf[ListBuffer[Node]], new ListBufferSerializer[Node])
//    builder.registerTypeAdapterFactory(new ListBufferTypeAdapterFactory())
    val builder = new PmsGsonBuilder()
    val gson = builder.create()
    
    val start = System.currentTimeMillis()
    val buffer = ListBuffer(1, 2, 3)
//    val json = gson.toJson(buffer)
    val json = gson.toJson(root)
    val duration = System.currentTimeMillis() - start
    
    println(json)
    println("duration: " + duration)
  }
}
/*
//    if (! theType.isInstanceOf[ParameterizedType]) {
//      return null;
//    }
//    val elementType: Type = $Gson$Types.getCollectionElementType(`type`, rawType);
    
    val elementType: Type = if (theType.isInstanceOf[ParameterizedType]) {
      theType.asInstanceOf[ParameterizedType].getActualTypeArguments()(0)
    } else {
      null
    }
    
    val elementTypeAdapter = if (elementType == null) null else gson.getAdapter(TypeToken.get(elementType))
    val result: TypeAdapter[_] = new ListBufferTypeAdapter[T](gson, elementType, elementTypeAdapter.asInstanceOf[TypeAdapter[T]])
    return result.asInstanceOf[TypeAdapter[T]]
  }
}

class ListBufferTypeAdapter[A](context: Gson, elementType: Type, elementTypeAdapter: TypeAdapter[A]) extends TypeAdapter[ListBuffer[A]] {
  
  val elementTypeAdapterWrapper = new TypeAdapterRuntimeTypeWrapper[A](context, elementTypeAdapter, elementType)
  
  override def write(out: JsonWriter, list: ListBuffer[A]): Unit = {
    val adapter = elementTypeAdapterWrapper
    if (list == null) {
      out.nullValue()
      return
    }
    
    out.beginArray()
    for (e <- list) {
      adapter.write(out, e)
    }
    out.endArray()
  }
  
  override def read(in: JsonReader): ListBuffer[A] = {
    throw new UnsupportedOperationException()
  }
}

class ListBufferSerializer[A <: Any] extends JsonSerializer[ListBuffer[A]] {

  def serialize(src: ListBuffer[A], typeOfSrc: Type, context: JsonSerializationContext): JsonElement = {
    val arr = new JsonArray
    for (e <- src) {
      val json = context.serialize(e)
      arr.add(json)
    }
    arr
  }
}*/