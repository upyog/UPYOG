/**
 * Created on Jun 9, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.report.model.response;

import java.util.List;

import org.egov.finance.report.entity.Function;
import org.egov.finance.report.model.FunctionModel;
import org.egov.finance.report.model.ResponseInfo;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FunctionResponse {

	ResponseInfo responseInfo;
	List<FunctionModel>function;
}

