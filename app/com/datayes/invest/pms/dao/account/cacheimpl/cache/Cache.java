package com.datayes.invest.pms.dao.account.cacheimpl.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Cache<T> {

    private ConcurrentMap<Key, Element> map = new ConcurrentHashMap<>();
    
    public T get(Key key) {
        Element e = map.get(key);
        if (e == null) {
            return null;
        }
        return (T) e.getValue();
    }
    
    public void put(Key key, Object value) {
        Element e = map.get(key);
        if (e == null) {
            e = new Element();
            e.setState(Element.State.CREATED);
            e.setValue(value);
        } else if (e.getState() == Element.State.LOADED){
            e.setState(Element.State.UPDATED);
        }
        map.put(key, e);
    }
    
    public void preload(Key key, Object value) {
        Element e = new Element();
        e.setState(Element.State.LOADED);
        e.setValue(value);
        map.put(key, e);
    }
    
    public Collection<T> getAll() {
        Collection<T> values = new ArrayList<>();
        Collection coll = map.values();
        for (Object o : coll) {
            Element e = (Element) o;
            Object v = e.getValue();
            if (v != null) {
                values.add((T) v);
            }
        }
        return values;
    }
    
    public Element getElement(Key key) {
        return map.get(key);
    }
    
    public Iterator<Key> keysIterator() {
        return map.keySet().iterator();
    }
}