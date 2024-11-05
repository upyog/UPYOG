package org.upyog.adv.web.models.idgen;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.response.ResponseInfo;

import java.util.List;

/**
 * <h1>IdGenerationResponse</h1>
 *
 * @author VISHAL_GENIUS
 *
 */
 @AllArgsConstructor
@NoArgsConstructor
@Builder
public class IdGenerationResponse {

    private ResponseInfo responseInfo;

    private List<IdResponse> idResponses;

	public List<digit.models.coremodels.IdResponse> getIdResponses() {
		// TODO Auto-generated method stub
		return null;
	}

}
