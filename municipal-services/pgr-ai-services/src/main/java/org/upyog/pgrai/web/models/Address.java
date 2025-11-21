package org.upyog.pgrai.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.upyog.pgrai.validation.SanitizeHtml;
import org.springframework.validation.annotation.Validated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

import jakarta.validation.Valid;

/**
 * Representation of a address. Indiavidual APIs may choose to extend from this using allOf if more details needed to be added in their case. 
 */
@Schema(description = "Representation of a address. Indiavidual APIs may choose to extend from this using allOf if more details needed to be added in their case. ")
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2020-07-15T11:35:33.568+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Address   {
        @SanitizeHtml
        @JsonProperty("tenantId")
        private String tenantId = null;

        @SanitizeHtml
        @JsonProperty("doorNo")
        private String doorNo = null;

        @SanitizeHtml
        @JsonProperty("plotNo")
        private String plotNo = null;

        @SanitizeHtml
        @JsonProperty("id")
        private String id = null;

        @SanitizeHtml
        @JsonProperty("landmark")
        private String landmark = null;

        @SanitizeHtml
        @JsonProperty("city")
        private String city = null;

        @SanitizeHtml
        @JsonProperty("district")
        private String district = null;

        @SanitizeHtml
        @JsonProperty("region")
        private String region = null;

        @SanitizeHtml
        @JsonProperty("state")
        private String state = null;

        @SanitizeHtml
        @JsonProperty("country")
        private String country = null;

        @SanitizeHtml
        @JsonProperty("pincode")
        private String pincode = null;

        @JsonProperty("additionDetails")
        private Object additionDetails = null;

        @SanitizeHtml
        @JsonProperty("buildingName")
        private String buildingName = null;

        @SanitizeHtml
        @JsonProperty("street")
        private String street = null;

        @Valid
        @JsonProperty("locality")
        private Boundary locality = null;

        @JsonProperty("geoLocation")
        private GeoLocation geoLocation = null;


}

