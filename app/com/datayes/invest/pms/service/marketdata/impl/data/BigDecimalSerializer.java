package com.datayes.invest.pms.service.marketdata.impl.data;


import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import scala.math.BigDecimal;

import java.lang.reflect.Type;

public class BigDecimalSerializer implements JsonSerializer<BigDecimal> {
    @Override
    public JsonElement serialize(BigDecimal bigDecimal,
                                 Type type,
                                 JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(bigDecimal.bigDecimal());
    }
}
