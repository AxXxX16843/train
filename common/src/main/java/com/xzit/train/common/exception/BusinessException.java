package com.xzit.train.common.exception;

public class BusinessException extends RuntimeException {
    private BusinessExpectionEnum e;

    public BusinessExpectionEnum getE() {
        return e;
    }

    public void setE(BusinessExpectionEnum e) {
        this.e = e;
    }
    public BusinessException(BusinessExpectionEnum e) {
        this.e = e;
    }
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
