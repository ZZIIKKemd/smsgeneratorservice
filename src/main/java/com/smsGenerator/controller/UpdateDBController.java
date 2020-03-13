package com.smsGenerator.controller;


import com.smsGenerator.domain.ManualDevice;
import com.smsGenerator.domain.RequestStatus;
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
    public RequestStatus settinDevice(
            @RequestBody ManualDevice data) {
        return updateDBService.addPort(data);
    }

    @GetMapping("/delete-device")
    public RequestStatus deleteDevice(@RequestParam Integer numberPort) {
        return updateDBService.deleteDevice(numberPort);
    }

    @GetMapping("/get-status-device")
    public String getStatusDevice(@RequestParam Integer numberPort) {
        return updateDBService.getPortStatus(numberPort);
    }

    @GetMapping("/all-device")
    public String getAllDevice() {
        return updateDBService.getAllDeviceStatus();
    }

    @GetMapping("/reset_all-device")
    public RequestStatus resetAllDvice() {
        return updateDBService.resetAllDvice();
    }
}
