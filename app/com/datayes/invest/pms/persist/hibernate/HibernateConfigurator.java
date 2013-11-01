package com.datayes.invest.pms.persist.hibernate;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datayes.invest.pms.config.Config;

public class HibernateConfigurator {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateConfigurator.class);
    
    private HibernateConfigurator() {
    }

    static Map<String, String> getProperties(String name) {
        Map<String, String> props = new HashMap<>();
        Config conf = Config.INSTANCE;
        String prefix = getSettingPrefix(name);
        
        // fix and put connection url
        String originUrl = conf.getString(prefix + "db.url");
        String fixedUrl = fixConnectionUrl(originUrl);
        LOGGER.debug("Database connection url for {}: {}", name, fixedUrl);
        props.put("hibernate.connection.url", fixedUrl);
        
        // put other settings
        props.put("hibernate.connection.username", conf.getString(prefix + "db.user"));
        props.put("hibernate.connection.password", conf.getString(prefix + "db.password"));
        props.put("hibernate.show_sql", conf.getBoolean("hibernate.show_sql", false).toString());
        
        return props;
    }
    
    private static String getSettingPrefix(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "";
        }
        name = name.trim();
        if (name.endsWith(".")) {
            return name;
        }
        return name + ".";
    }
    
    private static final String ZERO_DATE_TIME_BEHAVIOR = "zeroDateTimeBehavior";
    private static final String CONVERT_TO_NULL = "convertToNull";
    
    private static String fixConnectionUrl(String origin) {
        if (origin.contains(ZERO_DATE_TIME_BEHAVIOR)) {
            return origin;
        }
        String url = origin;
        if (! url.contains("?")) {
            url += "?";
        } else {
            url += "&";
        }
        url += (ZERO_DATE_TIME_BEHAVIOR + "=" + CONVERT_TO_NULL);
        
        return url;
    }
}
