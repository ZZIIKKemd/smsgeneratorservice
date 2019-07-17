package com.smsGenerator.utils;

import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat.PHONE;
import static com.sun.deploy.uitoolkit.ui.UIFactory.UPDATE_MESSAGE;
import static org.hibernate.validator.internal.metadata.core.ConstraintHelper.MESSAGE;

public class Constants {

    public static final String NUMBER_PORT = "numberPort";
    public static final String STATUS_OK = "OK";
    public static final String STATUS_FAILED = "FAILED";
    public static final String PHONE = "phone";
    public static final String MESSAGE = "message";
    public static final String UPDATE_MESSAGE = "updateMessage";

}
