package com.mseva.gisintegration.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MdmsConfig {

    @Value("${mdms.host}")
    private String host;

    @Value("${mdms.endpoint}")
    private String endpoint;

    public String getUrl() {
        return host + endpoint;
    }
}