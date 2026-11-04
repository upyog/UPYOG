package org.upyog.adv.web.models.idgen;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.response.ResponseInfo;


/**
 * <h1>IdGenerationResponse</h1>
 *
 * @author VISHAL_GENIUS
 *
 */
 @AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class IdGenerationResponse {

    private ResponseInfo responseInfo;

    private List<IdResponse> idResponses;

	
}
