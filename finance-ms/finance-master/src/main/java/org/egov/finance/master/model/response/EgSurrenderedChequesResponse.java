package org.egov.finance.master.model.response;

import java.util.List;

import org.egov.finance.master.model.EgSurrenderedChequesModel;
import org.egov.finance.master.model.ResponseInfo;

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
