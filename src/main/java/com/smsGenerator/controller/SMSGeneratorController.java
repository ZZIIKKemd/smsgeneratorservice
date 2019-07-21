package com.smsGenerator.controller;

import com.smsGenerator.domain.SMSQueue;
import com.smsGenerator.domain.SmsStatus;
import com.smsGenerator.service.SmsGatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.smsGenerator.utils.Constants.MESSAGE;
import static com.smsGenerator.utils.Constants.PHONE;
import static com.smsGenerator.utils.Constants.UPDATE_MESSAGE;


@RestController("Generate requests")
@RequestMapping()
public class SMSGeneratorController {
    @Autowired
    private SmsGatewayService smsGatewayService;

    @GetMapping("/generate-sms")
    public List<SmsStatus> generateRequest(@RequestParam(name = PHONE, required = false) List<String> numbers,
                                  @RequestParam(name = MESSAGE, required = false, defaultValue = "") String message,
                                  @RequestParam(value = UPDATE_MESSAGE, required = false) boolean updateMessage) {
        List<SmsStatus> statuses = smsGatewayService.sendNewSms(numbers, message, updateMessage);
        return statuses;
    }

    @GetMapping("/send-old-sms")
    public List<SmsStatus> sendOldSMS() {
        List<SmsStatus> quote = smsGatewayService.sendOldSms();
        return quote;
    }

    @GetMapping("/get-sms-queue")
    public List<SMSQueue> getSmsQueue() {
        List<SMSQueue> smsQueue = smsGatewayService.getSmsQueue();
        return smsQueue;
    }
}
