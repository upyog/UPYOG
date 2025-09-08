package org.egov.finance.report.model.response;

import java.util.List;

import org.egov.finance.report.model.ResponseInfo;
import org.egov.finance.report.model.VouchermisModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VouchermisResponse {
	private ResponseInfo responseInfo;
    private List<VouchermisModel> vouchermisList;
}
