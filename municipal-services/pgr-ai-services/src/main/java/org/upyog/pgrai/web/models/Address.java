package org.upyog.pgrai.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
<<<<<<< HEAD
import io.swagger.v3.oas.annotations.media.Schema;
import org.upyog.pgrai.validation.SanitizeHtml;
=======
import io.swagger.annotations.ApiModel;
import org.hibernate.validator.constraints.SafeHtml;
>>>>>>> master-LTS
import org.springframework.validation.annotation.Validated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

<<<<<<< HEAD
import jakarta.validation.Valid;
=======
import javax.validation.Valid;
>>>>>>> master-LTS

/**
 * Representation of a address. Indiavidual APIs may choose to extend from this using allOf if more details needed to be added in their case. 
 */
<<<<<<< HEAD
@Schema(description = "Representation of a address. Indiavidual APIs may choose to extend from this using allOf if more details needed to be added in their case. ")
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2020-07-15T11:35:33.568+05:30")
=======
@ApiModel(description = "Representation of a address. Indiavidual APIs may choose to extend from this using allOf if more details needed to be added in their case. ")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2020-07-15T11:35:33.568+05:30")
>>>>>>> master-LTS

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Address   {
<<<<<<< HEAD
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
=======
        @SafeHtml
        @JsonProperty("tenantId")
        private String tenantId = null;

        @SafeHtml
        @JsonProperty("doorNo")
        private String doorNo = null;

        @SafeHtml
        @JsonProperty("plotNo")
        private String plotNo = null;

        @SafeHtml
        @JsonProperty("id")
        private String id = null;

        @SafeHtml
        @JsonProperty("landmark")
        private String landmark = null;

        @SafeHtml
        @JsonProperty("city")
        private String city = null;

        @SafeHtml
        @JsonProperty("district")
        private String district = null;

        @SafeHtml
        @JsonProperty("region")
        private String region = null;

        @SafeHtml
        @JsonProperty("state")
        private String state = null;

        @SafeHtml
        @JsonProperty("country")
        private String country = null;

        @SafeHtml
>>>>>>> master-LTS
        @JsonProperty("pincode")
        private String pincode = null;

        @JsonProperty("additionDetails")
        private Object additionDetails = null;

<<<<<<< HEAD
        @SanitizeHtml
        @JsonProperty("buildingName")
        private String buildingName = null;

        @SanitizeHtml
=======
        @SafeHtml
        @JsonProperty("buildingName")
        private String buildingName = null;

        @SafeHtml
>>>>>>> master-LTS
        @JsonProperty("street")
        private String street = null;

        @Valid
        @JsonProperty("locality")
        private Boundary locality = null;

        @JsonProperty("geoLocation")
        private GeoLocation geoLocation = null;


}

