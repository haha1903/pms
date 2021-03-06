package com.datayes.invest.pms.config;

import org.joda.time.LocalTime;

import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;

import java.util.List;

public class Config {
    
    public static final Config INSTANCE = new Config("application.pms");

    private final com.typesafe.config.Config underlying;

    private Config(String resourceName) {
        System.out.println("Loading config file: " + resourceName + ".conf");
        this.underlying = ConfigFactory.load(resourceName);
    }
    
    public boolean getBoolean(String path) {
        return underlying.getBoolean(path);
    }
    
    public Boolean getBoolean(String path, boolean defaultValue) {
        try {
            return getBoolean(path);
        } catch (ConfigException.Missing e) {
            return defaultValue;
        }
    }
    
    public int getInt(String path) {
        return underlying.getInt(path);
    }
    
    public int getInt(String path, int defaultValue) {
        try {
            return getInt(path);
        } catch (ConfigException.Missing e) {
            return defaultValue;
        }
    }
    
    public long getLong(String path) {
        return underlying.getLong(path);
    }
    
    public long getLong(String path, long defaultValue) {
        try {
            return getLong(path);
        } catch (ConfigException.Missing e) {
            return defaultValue;
        }
    }
    
    public String getString(String path) {
        return underlying.getString(path);
    }
    
    public String getString(String path, String defaultValue) {
        try {
            return getString(path);
        } catch (ConfigException.Missing e) {
            return defaultValue;
        }
    }
    
    public LocalTime getLocalTime(String path) {
        String s = underlying.getString(path);
        LocalTime lt = LocalTime.parse(s);
        return lt;
    }
    
    public LocalTime getLocalTime(String path, LocalTime defaultValue) {
        try {
            return getLocalTime(path);
        } catch (ConfigException.Missing e) {
            return defaultValue;
        }
    }

    public List<String> getStringList(String path) {
        return underlying.getStringList(path);
    }

    public List<String> getStringList(String path, List<String> defaultList) {
        try {
            return getStringList(path);
        } catch (ConfigException.Missing e) {
            return defaultList;
        }
    }
}
