package com.datayes.invest.pms.config;

import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;

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
}
