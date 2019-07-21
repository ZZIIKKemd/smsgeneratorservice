package com.smsGenerator.domain;

import lombok.Data;

import javax.persistence.Entity;

@Data
public class RequestStatus {
    private String message;

    public RequestStatus(String message, String result) {
        this.message = message;
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    private String result;

}
