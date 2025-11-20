package org.upyog.pgrai.web.models.Idgen;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.response.ResponseInfo;

import java.util.List;

/**
 * Represents the response for an ID generation request in the system.
 * This class contains metadata about the response and a list of generated IDs.
 *
 * It is used to encapsulate the data returned after processing an ID generation request, including:
 * - {@link ResponseInfo} for metadata about the response.
 * - A list of {@link IdResponse} objects containing the generated IDs.
 *
 * This class is part of the ID generation module in the PGR system.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IdGenerationResponse {

    private ResponseInfo responseInfo;

    private List<IdResponse> idResponses;

}
