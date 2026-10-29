package org.egov.ndc.calculator.web.models.ndc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.egov.common.contract.request.RequestInfo;


import java.util.ArrayList;
import java.util.List;

@Data
public class NdcApplicationRequest {

    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @JsonProperty("Applications")
    private List<Application> applications;

    public NdcApplicationRequest addApplicationItem(Application applicationItem) {
        if (this.applications == null) {
            this.applications = new ArrayList<>();
        }
        this.applications.add(applicationItem);
        return this;
    }
}