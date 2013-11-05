package com.datayes.invest.pms.dao.account.cacheimpl.cache;

import java.util.HashMap;
import java.util.Map;


public class CacheWorkspace {
    
    public static ThreadLocal<CacheWorkspace> threadLocal = new ThreadLocal<>();
    
    private Map<Class<?>, Cache> caches = new HashMap<>();
    
    private CacheWorkspace() {
    }
    
    public static CacheWorkspace init() {
        CacheWorkspace ws = new CacheWorkspace();
        threadLocal.set(ws);
        return ws;
    }
    
    public static CacheWorkspace current() {
        return threadLocal.get();
    }

    public <T> Cache<T> get(Class<T> clazz) {
        Cache<T> cache = (Cache<T>) caches.get(clazz);
        if (cache == null) {
            cache = new Cache<T>();
            caches.put(clazz, cache);
        }
        return cache;
    }
}
