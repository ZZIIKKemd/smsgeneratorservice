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
public class RequestInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private Integer portNumber;
    private Integer simNumber;

    public RequestInfo(Integer port, Integer sim) {
        this.portNumber = port;
        this.simNumber = sim;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(Integer portNumber) {
        this.portNumber = portNumber;
    }

    public Integer getSimNumber() {
        return simNumber;
    }

    public void setSimNumber(Integer simNumber) {
        this.simNumber = simNumber;
    }
}
