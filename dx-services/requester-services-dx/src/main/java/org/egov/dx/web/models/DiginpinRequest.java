package org.egov.dx.web.models;

import javax.validation.constraints.NotNull;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request payload model for generating a DIGIPIN code.
 * Contains the geographical coordinates (latitude and longitude) and standard request metadata.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiginpinRequest {

    /**
     * Standard  request info containing metadata like API ID, user info, msgId, etc.
     */
    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    /**
     * The latitude of the location.
     * Must be provided and should ideally fall within the supported bounding box (2.5 to 38.5).
     */
    @NotNull
    @JsonProperty("latitude")
    private Double latitude;

    /**
     * The longitude of the location.
     * Must be provided and should ideally fall within the supported bounding box (63.5 to 99.5).
     */
    @NotNull
    @JsonProperty("longitude")
    private Double longitude;
}
