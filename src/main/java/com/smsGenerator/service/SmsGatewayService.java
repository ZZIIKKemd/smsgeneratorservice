package com.smsGenerator.service;

import com.smsGenerator.domain.SMSQueue;
import com.smsGenerator.domain.SmsStatus;

import java.util.List;

public interface SmsGatewayService {

    List<SmsStatus> sendNewSms(List<String> phone, String message, boolean updateMessageFlag);

    List<SmsStatus> sendOldSms();

    List<SMSQueue> getSmsQueue();
}
