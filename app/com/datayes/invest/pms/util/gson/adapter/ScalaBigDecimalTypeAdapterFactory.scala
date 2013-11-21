package com.datayes.invest.pms.util.gson.adapter

import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonWriter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken

class ScalaBigDecimalTypeAdapterFactory extends TypeAdapterFactory {

  override def create[T](gson: Gson, typeToken: TypeToken[T]): TypeAdapter[T] = {
    val rawType = typeToken.getRawType()
    if (typeToken.getRawType() != classOf[BigDecimal]) {
      return null;
    }
    
    val result: TypeAdapter[BigDecimal] = new Adapter()
    return result.asInstanceOf[TypeAdapter[T]]
  }
  
  private class Adapter extends TypeAdapter[BigDecimal] {
    
    override def write(out: JsonWriter, value: BigDecimal): Unit = {
      if (value == null) {
        out.nullValue()
      } else {
        out.value(value.bigDecimal)
      }
    }
    
    override def read(in: JsonReader): BigDecimal = {
      if (in.peek() == JsonToken.NULL) {
        in.nextNull()
        return null
      }
      val d = in.nextDouble()
      return BigDecimal(d)
    }
  }
}