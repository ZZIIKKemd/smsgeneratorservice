package com.smsGenerator.domain;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class SMSQueue {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private Integer port;
    private String phone;
    private String message;
    private Boolean updateMessageFlag;

    public SMSQueue(Integer port, String phone, String message, Boolean updateMessageFlag) {
        this.port = port;
        this.phone = phone;
        this.message = message;
        this.updateMessageFlag = updateMessageFlag;
    }

    public Boolean getUpdateMessageFlag() {
        return updateMessageFlag;
    }

    public void setUpdateMessageFlag(Boolean updateMessageFlag) {
        this.updateMessageFlag = updateMessageFlag;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SMSQueue(Integer port, String phone, String message) {
        this.port = port;
        this.phone = phone;
        this.message = message;
    }
}
