package org.egov.edcr.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EdcrConfigProperties {

    @Value("${edcr.default.state:pg}")
    private String defaultState;

    @Value("${edcr.default.isStateWise:false}")
    private boolean isStateWise;

    public String getDefaultState() {
        return defaultState;
    }

    public boolean getIsStateWise(){
        return isStateWise;
    }

}