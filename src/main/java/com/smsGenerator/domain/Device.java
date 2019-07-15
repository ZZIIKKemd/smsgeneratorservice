package com.smsGenerator.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    private String numberPort;
    private String numberSim;
    private String status;

    public Device() {
    }

    public Device(String numberPort, String numberSim, String status) {
        this.numberPort = numberPort;
        this.numberSim = numberSim;
        this.status = status;
    }
}
