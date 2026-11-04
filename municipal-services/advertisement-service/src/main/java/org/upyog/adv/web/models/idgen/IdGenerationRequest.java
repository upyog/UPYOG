package org.upyog.adv.web.models.idgen;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.request.RequestInfo;


/**
 * <h1>IdGenerationRequest</h1>
 * 
 * @author VISHAL_GENIUS
 *
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
