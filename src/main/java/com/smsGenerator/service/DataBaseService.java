package com.smsGenerator.service;

import com.smsGenerator.domain.SMSQueue;
import org.springframework.data.domain.Example;

import java.util.List;

public interface DataBaseService {
    boolean saveNewSms(List<SMSQueue> newSms);

    List<SMSQueue> getAllQueueSms(Integer maxCountSimCard);

    public List<SMSQueue> getAllQueueSms(Example<SMSQueue> exampleQueue);

    List<SMSQueue> getAllQueueSms();

    void deleteSms(SMSQueue smsRequest);
}
