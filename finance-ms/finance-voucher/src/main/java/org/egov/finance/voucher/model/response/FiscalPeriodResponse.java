package org.egov.finance.voucher.model.response;

import java.util.List;

import org.egov.finance.voucher.model.FiscalPeriodModel;
import org.egov.finance.voucher.model.ResponseInfo;

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
