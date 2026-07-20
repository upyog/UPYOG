package org.egov.garbageservice.model;

import java.util.List;

import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
/**
 * Batch request to the eGov encryption service containing one or more EncReqObject items.
 * Used when sensitive fields must be encrypted or decrypted before persistence or display.
 */
@Builder
public class EncryptionRequest {

	@NotNull
	@JsonProperty("encryptionRequests")
	private List<EncReqObject> encryptionRequests;

}
