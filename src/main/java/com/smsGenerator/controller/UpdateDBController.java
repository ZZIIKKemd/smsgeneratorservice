package com.smsGenerator.controller;


import com.smsGenerator.domain.ManualDevice;
import com.smsGenerator.repos.DeviceRepos;
import com.smsGenerator.service.UpdateDBService;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController("Generate DB")
@RequestMapping("/setting")
public class UpdateDBController {

    @Autowired
    private UpdateDBService updateDBService;

    @GetMapping("/add-device")
    public String settinDevice(
            @RequestBody ManualDevice data) {
        return updateDBService.addPort(data);
    }

    @GetMapping("/get-status-device")
    public String getStatusDevice(@RequestParam Integer numberPort) {
        return updateDBService.getPortStatus(numberPort);
    }


    @GetMapping("/delete-device")
    public String deleteDevice(@RequestParam Integer numberPort) {
        return updateDBService.deleteDevice(numberPort);
    }


    @GetMapping("/all-device")
    public String getAllDevice() {
        return updateDBService.getAllDeviceStatus();
    }

    @GetMapping("/reset_all-device")
    public String resetAllDvice() {
        return updateDBService.resetAllDvice();
    }
}
