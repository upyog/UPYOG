package org.upyog.pgrai.web.models.grievanceClient;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Grievance {

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("serviceCode")
    private String serviceCode;

    @JsonProperty("serviceType")
    private String serviceType;

    @JsonProperty("serviceRequestType")
    private String serviceRequestType;

    @JsonProperty("rating")
    private Integer rating;

    @JsonProperty("applicationStatus")
    private String applicationStatus;

    @JsonProperty("source")
    private String source;

    @JsonProperty("latitude")
    private Double latitude;

    @JsonProperty("longitude")
    private Double longitude;

    @JsonProperty("locality")
    private String locality;

    @JsonProperty("landmark")
    private String landmark;

    @JsonProperty("pincode")
    private String pincode;

    @JsonProperty("city")
    private String city;

    @JsonProperty("state")
    private String state;

    @JsonProperty("priority")
    private String priority;

    @JsonProperty("inputGrievance")
    private String inputGrievance;

    @JsonProperty("workflow")
    private String workflow;

    @JsonProperty("currentState")
    private String currentState;

    @JsonProperty("businessService")
    private String businessService;

    @JsonProperty("action")
    private String action;

    @JsonProperty("assigner")
    private String assigner;

    @JsonProperty("assignee")
    private String assignee;

    @JsonProperty("comments")
    private String comments;

    @JsonProperty("grievanceId")
    private String grievanceId;

}

