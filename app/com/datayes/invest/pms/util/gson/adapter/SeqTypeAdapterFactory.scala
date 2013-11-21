package com.datayes.invest.pms.util.gson.adapter

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
//import scala.collection.mutable.ListBuffer
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.google.gson.TypeAdapterFactory


class SeqTypeAdapterFactory extends TypeAdapterFactory {

  override def create[T](gson: Gson, typeToken: TypeToken[T]): TypeAdapter[T] = {
    val theType = typeToken.getType()
    val rawType = typeToken.getRawType()

    if (! classOf[Seq[_]].isAssignableFrom(typeToken.getRawType())) {
      return null;
    }
    
    val elementType: Type = if (theType.isInstanceOf[ParameterizedType]) {
      theType.asInstanceOf[ParameterizedType].getActualTypeArguments()(0)
    } else {
      null
    }
    val elementTypeAdapter = if (elementType != null) {
      gson.getAdapter(TypeToken.get(elementType))
    } else {
      null
    }
    
    val adapter: TypeAdapter[_] = new Adapter[T](gson, elementType, elementTypeAdapter.asInstanceOf[TypeAdapter[T]])
    return adapter.asInstanceOf[TypeAdapter[T]]
  }
  
  // TODO write this to a more generic form
  private class Adapter[A](context: Gson, elementType: Type, elementTypeAdapter: TypeAdapter[A]) extends TypeAdapter[Seq[A]] {
  
    val elementTypeAdapterWrapper = new TypeAdapterRuntimeTypeWrapper[A](context, elementTypeAdapter, elementType)
  
    override def write(out: JsonWriter, list: Seq[A]): Unit = {
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
  
    override def read(in: JsonReader): Seq[A] = {
      // TODO implement this
      throw new UnsupportedOperationException()
    }
  }
}