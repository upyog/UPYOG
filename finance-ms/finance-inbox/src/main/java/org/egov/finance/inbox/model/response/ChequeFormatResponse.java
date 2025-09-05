package org.egov.finance.inbox.model.response;

import java.util.List;

import org.egov.finance.inbox.entity.ChequeFormat;
import org.egov.finance.inbox.model.ResponseInfo;

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
