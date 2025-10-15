package org.egov.ndc.calculator.web.models.ndc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.egov.ndc.calculator.web.models.workflow.Workflow;

import java.math.BigDecimal;

@Data
@Builder
public class ApplicantRequest {
    @JsonProperty("uuid")
    private String uuid;

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("firstname")
    private String firstname;

    @JsonProperty("lastname")
    private String lastname;

    @JsonProperty("mobile")
    private String mobile;

    @JsonProperty("email")
    private String email;

    @JsonProperty("address")
    private String address;

    @JsonProperty("applicationStatus")
    private String applicationStatus;

    @JsonProperty("active")
    private Boolean active;

    @JsonProperty("action")
    private String action;

    @JsonProperty("fee")
    private BigDecimal fee;

    @JsonProperty("reason")
    private String reason;

    @JsonProperty("createdby")
    private String createdby;

    @JsonProperty("lastmodifiedby")
    private String lastmodifiedby;

    @JsonProperty("createdtime")
    private Long createdtime;

    @JsonProperty("lastmodifiedtime")
    private Long lastmodifiedtime;

    @JsonProperty("workflow")
    private Workflow workflow = null;
}