package com.smsGenerator.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class DeviceWrapper {
    Device device;
    Map<Integer, String> status =  new HashMap<>();
}
