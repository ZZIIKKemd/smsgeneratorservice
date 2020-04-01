package com.smsGenerator.Implementation;

import com.smsGenerator.service.AddressSerice;
import org.springframework.stereotype.Service;

@Service
public class AddressServiceImpl implements AddressSerice {
//    private final String address = System.getProperty("some.address");
    private final String address = "5.164.29.31";

    @Override
    public String getAddress(String message, String phone, int simNumber, Integer numberPort) {
    return new StringBuilder("http://")
            .append(address)
            .append(":")
            .append(numberPort)
            .append("/default/en_US/send.html?u=admin&p=sms_93_ZAK_322ZAK933&l=")
            .append(simNumber)
            .append("&n=")
            .append(phone)
            .append("&m=")
            .append(message).toString();
    }
}
