package org.egov.finance.master.model.response;

import java.util.List;

import org.egov.finance.master.entity.ChequeFormat;
import org.egov.finance.master.model.ResponseInfo;

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
