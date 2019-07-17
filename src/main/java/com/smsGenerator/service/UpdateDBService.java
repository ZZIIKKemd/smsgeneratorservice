package com.smsGenerator.service;

import com.smsGenerator.domain.ManualDevice;

public interface UpdateDBService {
    String addPort(ManualDevice manualDevice);

    String getPortStatus(Integer numberPort);

    String deleteDevice(Integer numberPort);

    String getAllDeviceStatus();
}
