package org.upyog.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "module")
public class ModuleConfig {

    private Map<String, String> endpoints;
    private Map<String, String> uniqueIdParams; 

    public Map<String, String> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(Map<String, String> endpoints) {
        this.endpoints = endpoints;
    }

    public Map<String, String> getUniqueIdParams() {
        return uniqueIdParams;
    }

    public void setUniqueIdParams(Map<String, String> uniqueIdParams) {
        this.uniqueIdParams = uniqueIdParams;
    }
}


