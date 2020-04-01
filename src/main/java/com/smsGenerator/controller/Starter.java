package com.smsGenerator.controller;

import com.smsGenerator.service.PostmanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("Starter")
@RequestMapping("/postman")
public class Starter {
    @Autowired
    private PostmanService postmanService;

    @GetMapping("/start")
    public void startPostman() {
        postmanService.start();
    }

    @GetMapping("/stop")
    public void stopPostman() {
        postmanService.stop();
    }

    @GetMapping("/status")
    public Boolean statusPostman() {
        return postmanService.status();
    }
}
