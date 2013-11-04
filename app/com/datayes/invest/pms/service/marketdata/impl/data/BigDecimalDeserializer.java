package com.datayes.invest.pms.service.marketdata.impl.data;


import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import scala.math.BigDecimal;

import java.lang.reflect.Type;

public class BigDecimalDeserializer implements JsonDeserializer<BigDecimal> {
    @Override
    public BigDecimal deserialize(JsonElement jsonElement,
                                  Type type,
                                  JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {
        return new BigDecimal(jsonElement.getAsJsonPrimitive().getAsBigDecimal());
    }
}
