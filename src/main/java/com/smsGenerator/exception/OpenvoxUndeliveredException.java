package com.smsGenerator.exception;

public class OpenvoxUndeliveredException extends Exception {

    public OpenvoxUndeliveredException(String message) {
        super(message);
    }

    public OpenvoxUndeliveredException() {
        super();
    }
}