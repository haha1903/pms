package com.datayes.invest.pms.util.gson;

import junit.framework.Assert;

import org.joda.time.LocalDateTime;
import org.junit.Test;

import com.google.gson.Gson;

public class LocalDateTimeTypeAdapterTest {
    
    private Gson gson = new PmsGsonBuilder().create();

    @Test
    public void testWrite() {
        LocalDateTime date = LocalDateTime.parse("2013-11-20T17:53:06.111");
        String json = gson.toJson(date);
        Assert.assertEquals("\"2013-11-20T17:53:06.111\"", json);
    }
    
    @Test
    public void testRead() {
        String json = "\"2013-11-20T17:53:06.111\"";
        LocalDateTime date = gson.fromJson(json, LocalDateTime.class);
        Assert.assertEquals(LocalDateTime.parse("2013-11-20T17:53:06.111"), date);
    }
}