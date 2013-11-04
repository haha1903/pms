package com.datayes.invest.pms.dao.account.cacheimpl.cache;

import java.util.Arrays;

public class Key {

    private final Object[] comps;
    
    public Key(Object... comps) {
        this.comps = comps;
    }
    
    public Object[] getComponents() {
        return comps;
    }
    
    @Override
    public String toString() {
        return "Key" + Arrays.toString(comps);
    }
    
    @Override
    public boolean equals(Object o) {
        if (! (o instanceof Key)) {
            return false;
        }
        Key other = (Key) o;
        return Arrays.equals(comps, other.comps);
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(comps);
    }
}
