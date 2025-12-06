package org.egov.commons.mdms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MdmsConfiguration {

    @Value("${mdms.host:}")
    private String mdmsHost;

    @Value("${mdms.searchurl:}")
    private String mdmsSearchUrl;
    
    @Value("${mdms.searchurlv2:}")
    private String mdmsSearchUrlv2;

    @Value("${mdms.enable:false}")
    private Boolean mdmsEnabled;

    public String getMdmsHost() {
        return mdmsHost;
    }

    public void setMdmsHost(String mdmsHost) {
        this.mdmsHost = mdmsHost;
    }

    public String getMdmsSearchUrl() {
        return mdmsSearchUrl;
    }
    
    public void setMdmsSearchUrl(String mdmsSearchUrl) {
        this.mdmsSearchUrl = mdmsSearchUrl;
    }

    public void setMdmsSearchUrlv2(String mdmsSearchUrlv2) {
        this.mdmsSearchUrlv2 = mdmsSearchUrlv2;
    }
    
    public String getMdmsSearchUrlv2() {
        return mdmsSearchUrlv2;
    }

    public Boolean getMdmsEnabled() {
        return mdmsEnabled;
    }

    public void setMdmsEnabled(Boolean mdmsEnabled) {
        this.mdmsEnabled = mdmsEnabled;
    }

}
