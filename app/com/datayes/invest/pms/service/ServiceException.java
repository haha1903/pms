package com.datayes.invest.pms.service;

public class ServiceException extends RuntimeException {

    public ServiceException() {
    }

    public ServiceException(String msg) {
        super(msg);
    }

    public ServiceException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
