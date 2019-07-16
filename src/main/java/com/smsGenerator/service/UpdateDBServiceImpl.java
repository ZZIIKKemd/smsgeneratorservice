package com.smsGenerator.service;

import com.smsGenerator.domain.Device;
import com.smsGenerator.domain.ManualDevice;
import com.smsGenerator.repos.DeviceRepos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class UpdateDBServiceImpl implements  UpdateDBService{

    @Autowired
    private DeviceRepos deviceRepos;

    private static final String ACTION_ADD = "add";
    private static final String ACTION_DELETE = "delete";
    private static final String ACTION_MODIFY = "modify";
    private static final String ACTION_READ = "read";

    public String updateDB(ManualDevice manualDevice) {
        Device device = new Device("12310", "1421", "3423");
        deviceRepos.save(device);
        return "ghfgh";
    };
}
