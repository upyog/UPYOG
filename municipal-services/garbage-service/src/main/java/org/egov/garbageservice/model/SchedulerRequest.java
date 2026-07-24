package org.egov.garbageservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.egov.common.contract.request.RequestInfo;

import java.time.LocalDate;

@Data
public class SchedulerRequest {
    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate billingDate;
}
