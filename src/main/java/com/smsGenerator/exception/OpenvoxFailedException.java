package com.smsGenerator.exception;

public class OpenvoxFailedException extends Exception {

    public OpenvoxFailedException(String message) {
        super(message);
    }

    public OpenvoxFailedException() {
        super();
    }
}