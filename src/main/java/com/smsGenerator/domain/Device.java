package com.smsGenerator.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private Integer numberPort;
    private Integer numberSim;
    private String status;
    private String type;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Device() {
    }

    public Device(Integer numberPort, Integer numberSim, String status, String type) {
        this.numberPort = numberPort;
        this.numberSim = numberSim;
        this.status = status;
        this.type = type;
    }
}
