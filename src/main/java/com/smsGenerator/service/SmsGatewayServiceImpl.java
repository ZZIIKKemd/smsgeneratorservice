package com.smsGenerator.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smsGenerator.domain.*;
import com.smsGenerator.repos.DeviceRepos;
import com.smsGenerator.repos.RequestInfoRepos;
import com.smsGenerator.repos.SMSQueueRepos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.smsGenerator.utils.Constants.STATUS_FAILED;
import static com.smsGenerator.utils.Constants.STATUS_OK;

@Service
public class SmsGatewayServiceImpl implements SmsGatewayService {

    @Autowired
    private ObjectMapper afterBurnerMapper;
    @Autowired
    private DeviceRepos deviceRepos;
    @Autowired
    private RequestInfoRepos requestInfoRepos;
    @Autowired
    private SMSQueueRepos smsQueueRepos;

    @Override
    public String getMessege(List<String> phones, String message) {
        List<SMSQueue> smsQueue = phones.stream()
                .map(phone -> new SMSQueue(phone, message))
                .peek(sms -> smsQueueRepos.save(sms))
                .collect(Collectors.toList());

        for (SMSQueue smsRequest : smsQueue) {
            SmsStatus smsStatus = generateRequest(smsRequest.getPhone(), smsRequest.getMessage());
            if (smsStatus != null) {
                smsQueueRepos.delete(smsRequest);
            } else {
                return "Failed!";
            }
        }
        return "Completed!";
    }

    private SmsStatus generateRequest(String phone, String message) {
        List<Device> devices = deviceRepos.findAll();
        Map<Integer, DeviceWrapper> devicesMap = devices.stream().collect(Collectors.toMap((entry) -> entry.getNumberPort(), (entry) -> generateDeviceWrapper(entry)));

        RequestInfo oldRequestInfo = getOldRequestInfo(devicesMap);


        RequestInfo newRequestInfo = getNewRequestInfo(oldRequestInfo, devicesMap);
        if (newRequestInfo != null) {
            StringBuilder requestAddress = generateStringRequest(newRequestInfo.getPort(), newRequestInfo.getSim(), phone, message);

            RestTemplate restTemplate = new RestTemplate();
            setTimeout(restTemplate, 1000);
            System.out.println(requestAddress);
            requestInfoRepos.save(newRequestInfo);
            try {
                String statusRequest = restTemplate.getForObject(requestAddress.toString(), String.class);
            } catch (Exception e) {
                devicesMap.get(newRequestInfo.getPort()).getStatus().put(newRequestInfo.getSim(), STATUS_FAILED);
                e.printStackTrace();
                saveDeviceStatus(devicesMap, newRequestInfo);
                return generateRequest(phone, message);
            }
            return new SmsStatus();
        } else {
            return null;
        }
    }

    private void saveDeviceStatus(Map<Integer, DeviceWrapper> devicesMap, RequestInfo newRequestInfo) {
        Object objectTosave = devicesMap.get(newRequestInfo.getPort()).getStatus();
        try {
            String jsonStatus = afterBurnerMapper.writeValueAsString(objectTosave);
            devicesMap.get(newRequestInfo.getPort()).getDevice().setStatus(jsonStatus);
            Device device = devicesMap.get(newRequestInfo.getPort()).getDevice();
            device.setStatus(jsonStatus);
            deviceRepos.save(device);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private DeviceWrapper generateDeviceWrapper(Device entry) {
        try {
            Map<Integer, String> statusMap = new ObjectMapper().readValue(entry.getStatus(), new TypeReference<Map<Integer, String>>() {
            });
            return new DeviceWrapper(entry, statusMap);
        } catch (IOException e) {
            e.printStackTrace();
            return new DeviceWrapper(entry, Collections.emptyMap());
        }
    }

    private StringBuilder generateStringRequest(Integer numberPort, Integer sim, String phone, String message) {
        StringBuilder requestAddress = new StringBuilder("http://87.244.1.90:/default/en_US/send.html?u=admin&p=sms_93_ZAK_322ZAK933&l=&n=&m=");
        requestAddress.insert(83, message);
        requestAddress.insert(80, phone);
        requestAddress.insert(77, sim);
        requestAddress.insert(19, numberPort);
        return requestAddress;
    }

    private RequestInfo getOldRequestInfo(Map<Integer, DeviceWrapper> devicesMap) {
        List<RequestInfo> oldRequestInfos = requestInfoRepos.findAll();
        RequestInfo oldRequestInfo;
        if (oldRequestInfos.isEmpty()) {
            oldRequestInfo = new RequestInfo(devicesMap.values().stream().findFirst().orElse(null).getDevice().getNumberPort(), 0);
        } else {
            oldRequestInfo = oldRequestInfos.stream().findFirst().orElse(null);
        }
        return oldRequestInfo;
    }

    private RequestInfo getNewRequestInfo(RequestInfo oldRequestInfo, Map<Integer, DeviceWrapper> devicesMap) {
        Integer indexPort = null;
        Integer indexSim = oldRequestInfo.getSim();

        List<DeviceWrapper> listWrapper = new ArrayList<DeviceWrapper>(devicesMap.values());
        List<Integer> keys = new ArrayList<Integer>(devicesMap.keySet());
        for (int i = 0; i < keys.size(); i++) {
            if (keys.get(i) == oldRequestInfo.getPort()) {
                indexPort = i;
                break;
            }
        }
        if (indexPort == null) {
            indexPort = 0;
        }
        final int sizePortMap = devicesMap.size();
        for (int i = 0; i < sizePortMap; i++) {
            for (int j = 0; j < devicesMap.get(oldRequestInfo.getPort()).getDevice().getNumberSim(); j++) {
                if (indexSim == (devicesMap.get(oldRequestInfo.getPort()).getDevice().getNumberSim() - 1)) {
                    if (indexPort == (sizePortMap - 1)) {
                        indexPort = 0;
                    } else {
                        indexPort++;
                    }
                    indexSim = 0;
                } else {
                    indexSim++;
                }
                if (devicesMap.get(listWrapper.get(indexPort).getDevice().getNumberPort()).getStatus().get(indexSim).equals(STATUS_OK)) {
                    return new RequestInfo(oldRequestInfo.getId(), listWrapper.get(indexPort).getDevice().getNumberPort(), indexSim);
                }
            }
        }
        return null;
    }

    private void setTimeout(RestTemplate restTemplate, int timeout) {
        restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory());
        SimpleClientHttpRequestFactory rf = (SimpleClientHttpRequestFactory) restTemplate
                .getRequestFactory();
        rf.setReadTimeout(timeout);
        rf.setConnectTimeout(timeout);
    }
}
