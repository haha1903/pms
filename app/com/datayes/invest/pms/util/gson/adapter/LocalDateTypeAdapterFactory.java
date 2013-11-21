package com.datayes.invest.pms.util.gson.adapter;

import java.io.IOException;

import org.joda.time.LocalDate;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class LocalDateTypeAdapterFactory implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class<T> rawType = (Class<T>) type.getRawType();
        if (! LocalDate.class.equals(rawType)) {
            return null;
        }
        return (TypeAdapter<T>) new Adapter();
    }
    
    private static class Adapter extends TypeAdapter<LocalDate> {

        @Override
        public void write(JsonWriter out, LocalDate value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                String s = value.toString();
                out.value(s);
            }
        }

        @Override
        public LocalDate read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            String s = in.nextString();
            LocalDate date = LocalDate.parse(s);
            return date;
        }
        
    }

}
