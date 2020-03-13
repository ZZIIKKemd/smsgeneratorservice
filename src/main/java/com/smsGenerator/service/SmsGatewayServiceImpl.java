package com.smsGenerator.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smsGenerator.domain.*;
import com.smsGenerator.repos.DeviceRepos;
import com.smsGenerator.repos.RequestInfoRepos;
import com.smsGenerator.repos.SMSQueueRepos;
import com.smsGenerator.repos.SmsStatusRepos;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.text.ParseException;

import java.io.IOException;
import java.util.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.smsGenerator.utils.Constants.*;

@Slf4j
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
    @Autowired
    private SmsStatusRepos smsStatusRepos;
    @Autowired
    private MessageUpdateService messageUpdateService;
    @Autowired
    private UpdateDBService updateDBService;

    @Override
    public List<SmsStatus> sendNewSms(List<String> phones, String message, boolean updateMessageFlag) {
        //todo delete after adding auto sent
        updateDBService.resetAllDvice();

        List<SMSQueue> smsQueue = phones.stream()
                .map(phone -> new SMSQueue(getFormatedPhone(phone), messageUpdateService.generateNewMessage(message, updateMessageFlag), updateMessageFlag))
                .peek(sms -> smsQueueRepos.save(sms))
                .collect(Collectors.toList());

        List<SmsStatus> smsStatuses = generateAndSendSms(smsQueue);

        if (smsStatuses.size() != 0
                && smsStatuses.stream().allMatch(smsStatus -> STATUS_OK.equals(smsStatus.getResult()))) {
            sendOldSms();
        }
        return smsStatuses;
    }

    private List<SmsStatus> generateAndSendSms(List<SMSQueue> smsQueue) {

        List<SmsStatus> SmsStatuses = new ArrayList<>();
        for (SMSQueue smsRequest : smsQueue) {
            SmsStatus smsStatus = generateRequest(smsRequest.getPhone(), smsRequest.getMessage());
            SmsStatuses.add(smsStatus);
            if (STATUS_OK.equals(smsStatus.getResult())) {
                smsQueueRepos.delete(smsRequest);
            }
        }
        smsStatusRepos.saveAll(SmsStatuses);
        return SmsStatuses;
    }

    @Override
    public List<SmsStatus> sendOldSms() {
        updateDBService.resetAllDvice();
        List<SMSQueue> smsQueue =smsQueueRepos.findAll();
        return generateAndSendSms(smsQueue);
    }

    @Override
    public List<SMSQueue> getSmsQueue() {
        return smsQueueRepos.findAll();
    }

    private SmsStatus generateRequest(String phone, String message) {

        List<Device> devices = deviceRepos.findAll();
        Map<Integer, DeviceWrapper> mapDeviceWrapperByPort = devices.stream()
                .collect(Collectors.toMap(Device::getNumberPort, this::generateDeviceWrapper));
        RequestInfo oldRequestInfo = getOldRequestInfo(mapDeviceWrapperByPort);
        RequestInfo newRequestInfo = getNewRequestInfo(oldRequestInfo, mapDeviceWrapperByPort);

        if (newRequestInfo != null) {
            StringBuilder requestAddress = generateStringRequest(newRequestInfo.getPortNumber(), newRequestInfo.getSimNumber(), phone, message);

            RestTemplate restTemplate = new RestTemplate();
            setTimeout(restTemplate, 1000);
            requestInfoRepos.save(newRequestInfo);
            try {
                String statusRequest = restTemplate.getForObject(requestAddress.toString(), String.class);
                if(statusRequest.contains("ERROR")){
                    throw new Exception();
                }
            } catch (Exception e) {
                log.info("Port: {}, sim: {} don't work.", newRequestInfo.getPortNumber(), newRequestInfo.getSimNumber());
                mapDeviceWrapperByPort.get(newRequestInfo.getPortNumber()).getStatus().put(newRequestInfo.getSimNumber(), STATUS_FAILED);
                saveDeviceStatus(mapDeviceWrapperByPort, newRequestInfo);
                return generateRequest(phone, message);
            }
            return SmsStatus.builder()
                    .phone(phone)
                    .message(message)
                    .result(STATUS_OK)
                    .numberPort(newRequestInfo.getPortNumber())
                    .numberSim(newRequestInfo.getSimNumber())
                    .timestamp_send(generetaData())
                    .build();
        } else {
            return SmsStatus.builder()
                    .phone(phone)
                    .message(message)
                    .result(STATUS_ERROR)
                    .description(DESCRIPTION_NO_WORKING_SIM)
                    .timestamp_send(generetaData())
                    .build();
        }
    }

    private String getFormatedPhone(String phone) {
        StringBuilder formstPhone = new StringBuilder(phone.replaceAll("[^0-9]", ""));
        formstPhone.delete(0, formstPhone.length() - 10);
        formstPhone.insert(0,"8");
        return formstPhone.toString();
    }

    private void saveDeviceStatus(Map<Integer, DeviceWrapper> mapDeviceWrapperByPort, RequestInfo newRequestInfo) {
        Object objectToSave = mapDeviceWrapperByPort.get(newRequestInfo.getPortNumber()).getStatus();
        try {
            String jsonStatus = afterBurnerMapper.writeValueAsString(objectToSave);
            mapDeviceWrapperByPort.get(newRequestInfo.getPortNumber()).getDevice().setStatus(jsonStatus);
            Device device = mapDeviceWrapperByPort.get(newRequestInfo.getPortNumber()).getDevice();
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
        requestAddress.insert(77, sim+1);
        requestAddress.insert(19, numberPort);
        return requestAddress;
    }

    private RequestInfo getOldRequestInfo(Map<Integer, DeviceWrapper> mapDeviceWrapperByPort) {
        List<RequestInfo> oldRequestInfos = requestInfoRepos.findAll();
        RequestInfo oldRequestInfo;
        if (oldRequestInfos.isEmpty()) {
            DeviceWrapper deviceWrapper = mapDeviceWrapperByPort.values().stream().findFirst().orElse(null);
            if(deviceWrapper == null) {
                throw new IllegalArgumentException("Не определены шлюзы! Добавте рабочие шлюзы.");
            }
            oldRequestInfo = new RequestInfo(deviceWrapper.getDevice().getNumberPort(), 0);
        } else {
            oldRequestInfo = oldRequestInfos.get(0);
        }
        return oldRequestInfo;
    }

    private RequestInfo getNewRequestInfo(RequestInfo oldRequestInfo, Map<Integer, DeviceWrapper> mapDeviceWrapperByPort) {

//        DeviceWrapper deviceWrapper = mapDeviceWrapperByPort.get(oldRequestInfo.getPortNumber());
//        if (deviceWrapper.getDevice().getNumberSim() == (oldRequestInfo.getSimNumber() + 1) {
//        }
        Integer indexPort = null;
        Integer indexSim = oldRequestInfo.getSimNumber();

        List<DeviceWrapper> listWrapper = new ArrayList(mapDeviceWrapperByPort.values());
        List<Integer> keys = new ArrayList(mapDeviceWrapperByPort.keySet());
        for (int i = 0; i < keys.size(); i++) {
            if (keys.get(i) == oldRequestInfo.getPortNumber()) {
                indexPort = i;
                break;
            }
        }
        if (indexPort == null) {
            indexPort = 0;
        }
        final int sizePortMap = mapDeviceWrapperByPort.size();
        for (int i = 0; i < sizePortMap; i++) {
            for (int j = 0; j < listWrapper.get(indexPort).getDevice().getNumberSim(); j++) {
                if (indexSim == (listWrapper.get(indexPort).getDevice().getNumberSim() - 1)) {
                    if (indexPort == (sizePortMap - 1)) {
                        indexPort = 0;
                    } else {
                        indexPort++;
                    }
                    indexSim = 0;
                } else {
                    indexSim++;
                }
                if (mapDeviceWrapperByPort.get(listWrapper.get(indexPort).getDevice().getNumberPort()).getStatus().get(indexSim).equals(STATUS_OK)) {
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

    private Timestamp generetaData() {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date date;
        try {
            date = formatter.parse(formatter.format(new Date()));
        } catch (ParseException e) {
            return null;
        }
        return new Timestamp(date.getTime());
    }
}
