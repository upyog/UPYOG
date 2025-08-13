package org.upyog.pgrai.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.validation.annotation.Validated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

/**
 * Represents the geographical location of an entity.
 * This class encapsulates the details of a geographical location, including:
 * - Latitude: The latitude coordinate of the location.
 * - Longitude: The longitude coordinate of the location.
 * - Additional Details: Any additional metadata related to the location.
 *
 * This class is part of the PGR V1 module and is used to manage
 * geo-location-related information within the system.
 */
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2020-07-15T11:35:33.568+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GeoLocation   {
        @JsonProperty("latitude")
        private Double latitude = null;

        @JsonProperty("longitude")
        private Double longitude = null;

        @JsonProperty("additionalDetails")
        private Object additionalDetails = null;


}

