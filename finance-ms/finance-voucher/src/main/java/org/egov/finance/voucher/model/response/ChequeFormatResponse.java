package org.egov.finance.voucher.model.response;

import java.util.List;

import org.egov.finance.voucher.entity.ChequeFormat;
import org.egov.finance.voucher.model.ResponseInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChequeFormatResponse {
	private ResponseInfo responseInfo;
    private List<ChequeFormat> chequeFormats;
}
