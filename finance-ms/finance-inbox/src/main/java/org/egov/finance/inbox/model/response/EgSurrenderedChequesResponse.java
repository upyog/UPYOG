package org.egov.finance.inbox.model.response;

import java.util.List;

import org.egov.finance.inbox.model.EgSurrenderedChequesModel;
import org.egov.finance.inbox.model.ResponseInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EgSurrenderedChequesResponse {
	private ResponseInfo responseInfo;

	private List<EgSurrenderedChequesModel> egSurrenderedCheques;

}
