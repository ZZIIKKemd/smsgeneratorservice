package com.smsGenerator.service;

import com.smsGenerator.domain.SMSQueue;
import com.smsGenerator.domain.SmsStatus;

import java.util.List;

public interface SmsGatewayService {
    boolean addNewSms(Integer port, List<String> phone, String message, boolean updateMessageFlag);

    void sendSms();

    List<SMSQueue> getSmsQueue();
}
