package org.egov.inbox.web.model.ndc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NdcApplicationResponse_bkp {
    @JsonProperty("id")
    private String id;

    @JsonProperty("fname")
    private String firstName;

    @JsonProperty("lname")
    private String lastName;

    @JsonProperty("tenantid")
    private String tenantId;

    @JsonProperty("mobile")
    private String mobile;

    @JsonProperty("email")
    private String email;

    @JsonProperty("address")
    private String address;

    @JsonProperty("applicationstatus")
    private String applicationStatus;

    @JsonProperty("active")
    private String active;

    @JsonProperty("a_createdby")
    private String createdBy;

    @JsonProperty("a_lastmodifiedby")
    private String lastModifiedBy;

    @JsonProperty("a_createdtime")
    private Double createdTime;

    @JsonProperty("a_lastmodifiedtime")
    private Double lastModifiedTime;

    @JsonProperty("wf_businessservice")
    private String businessService;

    @JsonProperty("wf_action")
    private String action;

    @JsonProperty("wf_assigner")
    private String assigner;

    @JsonProperty("wf_status")
    private String status;

    @JsonProperty("wf_createdby")
    private String workflowCreatedBy;

    @JsonProperty("wf_lastmodifiedby")
    private String workflowLastModifiedBy;

    @JsonProperty("wf_assignee")
    private String assignee;

}