package com.smsGenerator.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smsGenerator.domain.Device;
import com.smsGenerator.domain.ManualDevice;
import com.smsGenerator.repos.DeviceRepos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.smsGenerator.utils.Constants.STATUS_OK;

@Service
public class UpdateDBServiceImpl implements  UpdateDBService{

    @Autowired
    private DeviceRepos deviceRepos;
    @Autowired
    private ObjectMapper afterBurnerMapper;

    private static final String ACTION_ADD = "add";
    private static final String ACTION_DELETE = "delete";
    private static final String ACTION_MODIFY = "modify";
    private static final String ACTION_READ = "read";



    public String addPort(ManualDevice manualDevice) {
        switch (manualDevice.getAction()) {
            case ACTION_ADD: {
                if(deviceRepos.findByNumberPort(manualDevice.getNumberPort()) != null) {
                    return "Port with this number already exists!";
                }
                Integer numberPhone = manualDevice.getNumberSIM();
                Map<Integer, String> status =  new HashMap<>();
                IntStream.range(0, numberPhone).forEach(i -> status.put(i, STATUS_OK));
                try {
                    final String jsonString = afterBurnerMapper.writeValueAsString(status);
                    Device device = new Device(manualDevice.getNumberPort(), manualDevice.getNumberSIM(), jsonString);
                    deviceRepos.save(device);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                return "Successfully!";
            }
            default: {
                return "Undefy action!!!";
            }
        }
    }

    @Override
    public String getPortStatus(Integer numberPort) {
        Device device = deviceRepos.findByNumberPort(numberPort);
        return device.getStatus();
    }

    @Override
    public String deleteDevice(Integer numberPort) {
        Device device = deviceRepos.findByNumberPort(numberPort);
        deviceRepos.delete(device);
        return "Successfully!";
    }

    @Override
    public String getAllDeviceStatus(){
        List<Device> devices = deviceRepos.findAll().stream()
                .peek(device -> device.setStatus(null))
                .collect(Collectors.toList());
        try {
            return afterBurnerMapper.writeValueAsString(devices);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "Faild!";
    }
}
