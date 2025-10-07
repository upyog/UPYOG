package org.egov.finance.inbox.model.response;

import java.util.List;

import org.egov.finance.inbox.model.FiscalPeriodModel;
import org.egov.finance.inbox.model.ResponseInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FiscalPeriodResponse {
	private ResponseInfo responseInfo;
	private List<FiscalPeriodModel> fiscalPeriods;

}
