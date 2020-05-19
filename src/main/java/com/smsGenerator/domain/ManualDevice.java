package com.smsGenerator.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ManualDevice {
    String action;
    Integer numberPort;
    Integer numberSIM;
    String type;
}