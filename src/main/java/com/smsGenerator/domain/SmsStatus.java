package com.smsGenerator.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String phone;
    private String message;
    private String result;
    private String description;
    private Integer numberPort;
    private Integer numberSim;
    private Timestamp timestamp_send;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public void setStatusSending(String result) {
        this.result = result;
    }

    public Integer getNumberPort() {
        return numberPort;
    }

    public void setNumberPort(Integer numberPort) {
        this.numberPort = numberPort;
    }

    public Integer getNumberSim() {
        return numberSim;
    }

    public void setNumberSim(Integer numberSim) {
        this.numberSim = numberSim;
    }

    public Timestamp getTimestamp_send() {
        return timestamp_send;
    }

    public void setTimestamp_send(Timestamp timestamp_send) {
        this.timestamp_send = timestamp_send;
    }
}
