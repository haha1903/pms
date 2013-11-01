package com.datayes.invest.pms.persist;

public class PersistException extends RuntimeException {

    public PersistException() {
    }
    
    public PersistException(String message) {
        super(message);
    }
    
    public PersistException(Throwable th) {
        super(th);
    }
    
    public PersistException(String message, Throwable th) {
        super(message, th);
    }
}
