package com.smsGenerator.service;

import com.smsGenerator.domain.ManualDevice;
import com.smsGenerator.domain.RequestStatus;

public interface UpdateDBService {
    RequestStatus addPort(ManualDevice manualDevice);

    String getPortStatus(Integer numberPort);

    RequestStatus deleteDevice(Integer numberPort);

    String getAllDeviceStatus();

    RequestStatus resetAllDvice();
}
