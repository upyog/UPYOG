package org.egov.garbageservice.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Single encryption or decryption operation for a tenant and named value map.
 * Submitted inside EncryptionRequest to the platform crypto/egov-enc-service API.
 */
@Schema(description = "Encryption / Decryption Request Meta-data and Values")
@Validated
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EncReqObject {

    @NotNull
    @JsonProperty("tenantId")
    private String tenantId = null;

    @NotNull
    @JsonProperty("type")
    private String type = null;

    @NotNull
    @JsonProperty("value")
    @Valid
    private Object value = null;

}