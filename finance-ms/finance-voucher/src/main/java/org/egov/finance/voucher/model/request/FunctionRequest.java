/**
 * Created on Jun 9, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.voucher.model.request;

import org.egov.finance.voucher.entity.Function;
import org.egov.finance.voucher.model.FunctionModel;
import org.egov.finance.voucher.model.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import lombok.Data;
@Data
public class FunctionRequest {


	@JsonProperty("RequestInfo")
	RequestInfo requestInfo;
	@Valid
	@JsonProperty("Function")
	FunctionModel function;
}

