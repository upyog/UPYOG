/**
 * 
 */
package org.egov.finance.report.model.response;

import java.util.List;

import org.egov.finance.report.model.ResponseInfo;
import org.egov.finance.report.model.SubSchemeModel;

import lombok.Builder;
import lombok.Getter;

/**
 * SubSchemeResponse.java
 * 
 * @author bpattanayak
 * @date 11 Jun 2025
 * @version 1.0
 */

@Getter
@Builder
public class SubSchemeResponse {
	
	ResponseInfo responseInfo;
	List<SubSchemeModel> subSchemeResponse;

}
