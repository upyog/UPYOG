package org.egov.finance.report.model.response;

import java.util.List;

import org.egov.finance.report.model.BankModel;
import org.egov.finance.report.model.ResponseInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankResponse {
	private ResponseInfo responseInfo;
	private List<BankModel> banks;

}
