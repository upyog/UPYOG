/**
 * 
 */
package org.egov.finance.inbox.model.response;

import java.util.List;

import org.egov.finance.inbox.model.ResponseInfo;
import org.egov.finance.inbox.model.SubSchemeModel;

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
