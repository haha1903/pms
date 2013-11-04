package com.datayes.invest.pms.dao.account.cacheimpl.cache;

import java.util.HashMap;
import java.util.Map;


public class CacheWorkspace {
    
    private Map<Class<?>, Cache> caches = new HashMap<>();

    public <T> Cache<T> get(Class<T> clazz) {
        Cache<T> cache = (Cache<T>) caches.get(clazz);
        if (cache == null) {
            cache = new Cache<T>();
            caches.put(clazz, cache);
        }
        return cache;
    }
}
