package org.upyog.pgrai.web.models.Idgen;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * Represents a request for generating a unique ID in the system.
 * This class encapsulates the details required for ID generation, including:
 * - The name of the ID to be generated.
 * - The tenant ID associated with the request.
 * - An optional format for the ID.
 *
 * This class is part of the ID generation module in the PGR system.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IdRequest {

	@JsonProperty("idName")
	@NotNull
	private String idName;

	@NotNull
	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("format")
	private String format;

}
