package com.smsGenerator.Implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smsGenerator.domain.*;
import com.smsGenerator.repos.DeviceRepos;
import com.smsGenerator.repos.RequestInfoRepos;
import com.smsGenerator.repos.SmsStatusRepos;
import com.smsGenerator.service.*;
import com.smsGenerator.utils.DeviceType;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.util.Pair;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
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
    private DataBaseService dataBaseService;
    @Autowired
    private SmsStatusRepos smsStatusRepos;
    @Autowired
    private MessageUpdateService messageUpdateService;
    @Autowired
    private UpdateDBService updateDBService;

    @Autowired
    private AddressSerice addressSerice;

    @Override
    public boolean addNewSms(Integer port, List<String> phones, String message, boolean updateMessageFlag) {
        return dataBaseService.saveNewSms(phones.stream()
            .map(phone -> new SMSQueue(port, getFormatedPhone(phone), messageUpdateService.generateNewMessage(message, updateMessageFlag), updateMessageFlag))
            .collect(Collectors.toList()));
        
    }

    @Override
    public List<SMSQueue> getSmsQueue() {
        return dataBaseService.getAllQueueSms();
    }

    @Override
    public void sendSms() {
        updateDBService.resetAllDvice();
        List<Device> devices = deviceRepos.findAll();
        Integer maxCountSimCard = devices.stream()
                .mapToInt(Device::getNumberSim)
                .sum();
        
        List<Pair<Device, List<SMSQueue>>> smsQueueByDevice = new ArrayList<Pair<Device, List<SMSQueue>>>();
        for (Device device : devices) {
            SMSQueue specificQueue = new SMSQueue();
            specificQueue.setPort(device.getNumberPort());
            List<SMSQueue> smsQueue = dataBaseService.getAllQueueSms(Example.of(specificQueue));
            Pair<Device, List<SMSQueue>> deviceQueuePair = Pair.of(device, smsQueue);
            smsQueueByDevice.add(deviceQueuePair);
        }

        for (Pair<Device, List<SMSQueue>> deviceQueuePair : smsQueueByDevice) {
            SmsStatus smsStatus;
            Device device = deviceQueuePair.getFirst();
            List<SMSQueue> smsQueue = deviceQueuePair.getSecond();
            for (int simNumber = 1; simNumber <= device.getNumberSim(); simNumber++) {
                if(!CollectionUtils.isEmpty(smsQueue)) {
                    smsStatus = generateRequest(
                        device.getType(),
                        smsQueue.get(0).getPhone(),
                        smsQueue.get(0).getMessage(),
                        smsQueue.get(0).getPort(),
                        simNumber);
                    if (smsStatus != null) {
                        if (STATUS_OK.equals(smsStatus.getResult())) {
                            dataBaseService.deleteSms(smsQueue.get(0));
                            smsQueue.remove(0);
                        }
                        smsStatusRepos.save(smsStatus);
                    }
                } else {
                    break;
                }
            }
        }

    }


    private SmsStatus generateRequest(String deviceType, String phone, String message, Integer numberPotr, Integer simNumber) {
        String requestAddress = generateStringRequest(deviceType, numberPotr, simNumber, phone, message);
        RestTemplate restTemplate = new RestTemplate();
        setTimeout(restTemplate, 30000);
        try {
            String statusRequest = restTemplate.getForObject(requestAddress, String.class);
            DeviceType type = DeviceType.valueOf(deviceType);
            if(type.equals(DeviceType.GOIP) && statusRequest.contains("ERROR")){
                throw new Exception();
            }
            if(type.equals(DeviceType.OPENVOX) && !statusRequest.contains("success")){
                throw new Exception();
            }
        } catch (Exception e) {
            log.info("Port: {}, sim: {} don't work.", numberPotr, simNumber);
            return SmsStatus.builder()
                    .phone(phone)
                    .message(message)
                    .result(STATUS_ERROR)
                    .description(DESCRIPTION_NO_WORKING_SIM)
                    .numberPort(numberPotr)
                    .numberSim(simNumber)
                    .timestamp_send(generetaData())
                    .build();
        }
        return SmsStatus.builder()
                .phone(phone)
                .message(message)
                .result(STATUS_OK)
                .numberPort(numberPotr)
                .numberSim(simNumber)
                .timestamp_send(generetaData())
                .build();
    }

    private String getFormatedPhone(String phone) {
        StringBuilder formstPhone = new StringBuilder(phone.replaceAll("[^0-9]", ""));
        formstPhone.delete(0, formstPhone.length() - 10);
        formstPhone.insert(0,"8");
        return formstPhone.toString();
    }

    private String generateStringRequest(String deviceType, Integer numberPort, Integer sim, String phone, String message) {
        return addressSerice.getAddress(deviceType, message, phone, sim, numberPort);
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
