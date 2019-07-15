package com.smsGenerator.service;

import com.smsGenerator.domain.ManualDevice;
import org.springframework.beans.factory.annotation.Autowired;
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
        return "ghfgh";
    };
}
