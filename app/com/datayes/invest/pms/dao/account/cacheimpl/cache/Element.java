package com.datayes.invest.pms.dao.account.cacheimpl.cache;


public class Element {

    private Object value;
    
    private State state;
    
    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public static enum State {
        LOADED, CREATED, UPDATED, DELETED
    }
    
    @Override
    public String toString() {
        return "Element [" + value + ", " + state + "]";
    }
}
