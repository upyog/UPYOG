package org.egov.finance.voucher.model.response;

import java.util.List;

import org.egov.finance.voucher.model.ResponseInfo;
import org.egov.finance.voucher.model.VouchermisModel;

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
