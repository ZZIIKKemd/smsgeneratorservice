package com.smsGenerator.service;

import java.util.List;

public interface SmsGatewayService {

    String getMessege(List<String> phone, String message);

}
