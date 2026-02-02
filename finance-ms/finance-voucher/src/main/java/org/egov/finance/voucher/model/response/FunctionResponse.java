/**
 * Created on Jun 9, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.voucher.model.response;

import java.util.List;

import org.egov.finance.voucher.entity.Function;
import org.egov.finance.voucher.model.FunctionModel;
import org.egov.finance.voucher.model.ResponseInfo;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FunctionResponse {

	ResponseInfo responseInfo;
	List<FunctionModel>function;
}

