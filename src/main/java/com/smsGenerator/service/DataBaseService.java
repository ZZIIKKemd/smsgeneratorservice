package com.smsGenerator.service;

import com.smsGenerator.domain.SMSQueue;

import java.util.List;

public interface DataBaseService {
    boolean saveNewSms(List<SMSQueue> newSms);

    List<SMSQueue> getAllQueueSms(Integer maxCountSimCard);

    List<SMSQueue> getAllQueueSms();

    void deleteSms(SMSQueue smsRequest);
}
