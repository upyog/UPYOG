/**
 * Created on Jun 9, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.master.model.response;

import java.util.List;

import org.egov.finance.master.entity.Function;
import org.egov.finance.master.model.ResponseInfo;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FunctionResponse {

	ResponseInfo responseInfo;
	List<Function>function;
}

