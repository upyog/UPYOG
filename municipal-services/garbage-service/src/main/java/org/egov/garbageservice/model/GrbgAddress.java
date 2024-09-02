package org.egov.garbageservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GrbgAddress {

    private String uuid;
    private Long garbageId;
    private String addressType;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String pincode;
    private Boolean isActive;
    private String zone;
    private String ulbName;
    private String ulbType;
    private String wardName;

    private JsonNode additionalDetail = null;
}
