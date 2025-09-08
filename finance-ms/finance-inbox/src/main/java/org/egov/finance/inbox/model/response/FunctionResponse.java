/**
 * Created on Jun 9, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.inbox.model.response;

import java.util.List;

import org.egov.finance.inbox.entity.Function;
import org.egov.finance.inbox.model.FunctionModel;
import org.egov.finance.inbox.model.ResponseInfo;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FunctionResponse {

	ResponseInfo responseInfo;
	List<FunctionModel>function;
}

