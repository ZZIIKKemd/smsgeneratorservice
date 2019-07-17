package com.smsGenerator.controller;

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
@RequestMapping("/generate-sms")
public class SMSGeneratorController {
    @Autowired
    private SmsGatewayService smsGatewayService;

    @GetMapping()
    public String generateRequest(@RequestParam(name = PHONE, required = false) List<String> numbers,
                                  @RequestParam(name = MESSAGE, required = false, defaultValue = "") String message,
                                  @RequestParam(value = UPDATE_MESSAGE, required = false) boolean updateMessage) {
        String quote = smsGatewayService.getMessege(numbers, message);
        return quote;
    }

}
