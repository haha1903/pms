package com.datayes.invest.pms.util.gson;


import com.datayes.invest.pms.util.gson.adapter.SeqTypeAdapterFactory;
import com.datayes.invest.pms.util.gson.adapter.LocalDateTimeTypeAdapterFactory;
import com.datayes.invest.pms.util.gson.adapter.LocalDateTypeAdapterFactory;
import com.datayes.invest.pms.util.gson.adapter.ScalaBigDecimalTypeAdapterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class PmsGsonBuilder {
    
    private GsonBuilder builder = new GsonBuilder();

    public PmsGsonBuilder() {
        // Exclude the fields that have GsonExclude annotation
        builder.setExclusionStrategies(new GsonExcludeStrategy());
        
        // Serialize nulls
        builder.serializeNulls();
        
        // Register type adapter factories
        builder.registerTypeAdapterFactory(new LocalDateTimeTypeAdapterFactory());
        builder.registerTypeAdapterFactory(new LocalDateTypeAdapterFactory());
        builder.registerTypeAdapterFactory(new ScalaBigDecimalTypeAdapterFactory());
        builder.registerTypeAdapterFactory(new SeqTypeAdapterFactory());
    }
    
    public Gson create() {
        return builder.create();
    }
    
    public PmsGsonBuilder setPrettyPrinting() {
        builder.setPrettyPrinting();
        return this;
    }
}
