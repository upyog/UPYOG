/**
 * Created on Jun 17, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.inbox.model.request;

import org.egov.finance.inbox.model.GenericModel;
import org.egov.finance.inbox.model.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import lombok.Data;

@Data
public class VoucherPrintRequest {
	private RequestInfo requestInfo;
	@JsonProperty("voucher")
    private GenericModel voucher;
}

