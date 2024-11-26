package org.egov.asset.web.models;

import javax.persistence.Embedded;
import javax.persistence.Id;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representation of a address. Indiavidual APIs may choose to extend from this using allOf if more details needed to be added in their case. 
 */
@ApiModel(description = "Representation of a address. Indiavidual APIs may choose to extend from this using allOf if more details needed to be added in their case. ")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-12T12:56:34.514+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Address   {
        @JsonProperty("tenantId")
        private String tenantId = null;

        @JsonProperty("doorNo")
        private String doorNo = null;

        @JsonProperty("latitude")
        private Double latitude = null;

        @JsonProperty("longitude")
        private Double longitude = null;
        
        @Id
        @JsonProperty("addressId")
        private String addressId = null;

        @JsonProperty("addressNumber")
        private String addressNumber = null;

        @JsonProperty("type")
        private String type = null;

        @JsonProperty("addressLine1")
        private String addressLine1 = null;

        @JsonProperty("addressLine2")
        private String addressLine2 = null;

        @JsonProperty("landmark")
        private String landmark = null;

        @JsonProperty("city")
        private String city = null;

        @JsonProperty("pincode")
        private String pincode = null;

        @JsonProperty("detail")
        private String detail = null;

        @JsonProperty("buildingName")
        private String buildingName = null;

        @JsonProperty("street")
        private String street = null;

        @Embedded
        @JsonProperty("locality")
        private Boundary locality = null;


}

