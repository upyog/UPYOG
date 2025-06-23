package org.egov.finance.inbox.model.response;

import java.util.List;

import org.egov.finance.inbox.model.ResponseInfo;
import org.egov.finance.inbox.model.VouchermisModel;

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
