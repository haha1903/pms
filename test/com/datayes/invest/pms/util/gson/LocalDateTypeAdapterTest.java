package com.datayes.invest.pms.util.gson;

import junit.framework.Assert;

import org.joda.time.LocalDate;
import org.junit.Test;

import com.google.gson.Gson;

public class LocalDateTypeAdapterTest {
    
    private Gson gson = new PmsGsonBuilder().create();

    @Test
    public void testWrite() {
        LocalDate date = LocalDate.parse("2013-11-20");
        String json = gson.toJson(date);
        Assert.assertEquals("\"2013-11-20\"", json);
    }
    
    @Test
    public void testRead() {
        String json = "\"2013-11-20\"";
        LocalDate date = gson.fromJson(json, LocalDate.class);
        Assert.assertEquals(LocalDate.parse("2013-11-20"), date);
    }
}
