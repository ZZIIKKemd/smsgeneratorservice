package com.smsGenerator.Implementation;

import com.smsGenerator.domain.SMSQueue;
import com.smsGenerator.repos.SMSQueueRepos;
import com.smsGenerator.service.DataBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataBaseServiceImpl implements DataBaseService {
    @Autowired
    private SMSQueueRepos smsQueueRepos;

    private final Object obj = new Object();

    @Override
    public boolean saveNewSms(List<SMSQueue> newSms) {
        synchronized(obj) {
            newSms.forEach(sms -> smsQueueRepos.save(sms));
        }
        return true;
    }

    @Override
    public List<SMSQueue> getAllQueueSms(Integer maxCountSimCard) {
        List<SMSQueue> smsQueue;
        synchronized(obj) {
            smsQueue = smsQueueRepos.findAll();
        }
        return smsQueue;
    }

    @Override
    public List<SMSQueue> getAllQueueSms(Example<SMSQueue> exampleQueue) {
        List<SMSQueue> smsQueue;
        synchronized(obj) {
            smsQueue = smsQueueRepos.findAll(exampleQueue);
        }
        return smsQueue;
    }

    @Override
    public List<SMSQueue> getAllQueueSms() {
        List<SMSQueue> smsQueue;
        synchronized(obj) {
            //todo
            smsQueue = smsQueueRepos.findAll();
        }
        return smsQueue;
    }

    @Override
    public void deleteSms(SMSQueue smsRequest) {
        synchronized(obj) {
            smsQueueRepos.delete(smsRequest);
        }
    }
}
