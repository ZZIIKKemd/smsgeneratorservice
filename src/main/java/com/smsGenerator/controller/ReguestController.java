package com.smsGenerator.controller;


import com.smsGenerator.domain.ManualDevice;
import com.smsGenerator.repos.DeviceRepos;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("Generate requests")
@RequestMapping("/generate")
@NoArgsConstructor
public class ReguestController {

    @Autowired
    private UpdateDBService updateDBService;

    @GetMapping("/setting-device")
    public String settinDevice(
            @RequestBody ManualDevice data) {
        return updateDBService.updateDB(data);
    }
}
