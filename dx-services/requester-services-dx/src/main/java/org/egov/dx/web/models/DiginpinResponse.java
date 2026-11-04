package org.egov.dx.web.models;

import org.egov.common.contract.response.ResponseInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response payload model for the DIGIPIN generation API.
 * Contains the generated DIGIPIN string and standard response metadata.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiginpinResponse {

    /**
     * Standard response info containing metadata like API status, correlation ID, etc.
     */
    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;

    /**
     * The generated DIGIPIN string corresponding to the requested coordinates.
     */
    @JsonProperty("digipin")
    private String digipin;
}
