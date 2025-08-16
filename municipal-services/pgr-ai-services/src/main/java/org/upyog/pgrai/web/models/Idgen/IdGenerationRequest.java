package org.upyog.pgrai.web.models.Idgen;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.request.RequestInfo;

import java.util.List;

/**
 * Represents a request for generating IDs in the system.
 * This class contains the request information and a list of ID generation requests.
 *
 * It is used to encapsulate the data required for ID generation, including:
 * - {@link RequestInfo} for metadata about the request.
 * - A list of {@link IdRequest} objects specifying the details of the IDs to be generated.
 *
 * This class is part of the ID generation module in the PGR system.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IdGenerationRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	private List<IdRequest> idRequests;

}
