package org.egov.garbageservice.model;

import org.egov.tracer.annotations.CustomSafeHtml;

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
/**
 * Structured address on a garbage application (locality, ward, pincode, geo fields).
 * Distinct from generic Address model used in user-service integration.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GrbgAddress {

    @CustomSafeHtml
    private String uuid;
    private Long garbageId;
    @CustomSafeHtml
    private String addressType;
    @CustomSafeHtml
    private String address1;
    @CustomSafeHtml
    private String address2;
    @CustomSafeHtml
    private String city;
    @CustomSafeHtml
    private String state;
    @CustomSafeHtml
    private String pincode;
    private Boolean isActive;
    @CustomSafeHtml
    private String zone;
    @CustomSafeHtml
    private String ulbName;
    @CustomSafeHtml
    private String ulbType;
    @CustomSafeHtml
    private String wardName;

    private JsonNode additionalDetail = null;
}
