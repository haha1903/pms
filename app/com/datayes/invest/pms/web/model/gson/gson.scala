package com.datayes.invest.pms.web.model.gson

import com.google.gson._
import java.lang.reflect.Type
import org.joda.time.LocalDateTime
import org.joda.time.LocalDate

class BigDecimalSerializer extends JsonSerializer[BigDecimal] {

  def serialize(src: BigDecimal, typeOfSrc: Type, context: JsonSerializationContext): JsonElement = {
    new JsonPrimitive(src.bigDecimal)
  }
}

class LocalDateSerializer extends JsonSerializer[LocalDate] {

  def serialize(src: LocalDate, typeOfSrc: Type, context: JsonSerializationContext): JsonElement = {
    new JsonPrimitive(src.toString)
  }
}

class LocalDateDeserializer extends JsonDeserializer[LocalDate] {

  def deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LocalDate = {
    LocalDate.parse(json.getAsJsonPrimitive.getAsString)
  }
}

class LocalDateTimeSerializer extends JsonSerializer[LocalDateTime] {

  def serialize(src: LocalDateTime, typeOfSrc: Type, context: JsonSerializationContext): JsonElement = {
    new JsonPrimitive(src.toString)
  }
}

class LocalDateTimeDeserializer extends JsonDeserializer[LocalDateTime] {

  def deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LocalDateTime = {
    new LocalDateTime(json.getAsJsonPrimitive.getAsString)
  }
}

class BigDecimalDeserializer extends JsonDeserializer[BigDecimal] {

  def deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): BigDecimal = {
    BigDecimal(json.getAsJsonPrimitive.getAsBigDecimal)
  }
}
