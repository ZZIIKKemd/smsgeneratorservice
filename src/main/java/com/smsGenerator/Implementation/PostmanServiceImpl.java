package com.smsGenerator.Implementation;

import com.smsGenerator.service.PostmanService;
import com.smsGenerator.service.SmsGatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class PostmanServiceImpl implements PostmanService {
    @Autowired
    private SmsGatewayService smsGatewayService;

    private volatile Boolean go = true;

    @Override
    public void start() {
        go =true;
    }

    @Override
    public void stop() {
        go = false;
    }

    @Override
    public Boolean status() {
        return go;
    }

    @PostConstruct
    public void initPostman() {
        Runnable run = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(180000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (go) {
                        smsGatewayService.sendSms();
                    }
                }
            }
        };
        Thread t = new Thread(run);
        t.start();
    }
}
