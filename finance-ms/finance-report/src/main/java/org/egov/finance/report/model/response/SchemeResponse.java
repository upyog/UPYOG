package org.egov.finance.report.model.response;
/**
 * SchemeResponse.java
 * 
 * @author mmavuluri
 * @date 9 Jun 2025
 * @version 1.0
 */
import java.util.List;

import org.egov.finance.report.model.ResponseInfo;
import org.egov.finance.report.model.SchemeModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchemeResponse {

	private ResponseInfo responseInfo;
	private List<SchemeModel> schemes;

}
