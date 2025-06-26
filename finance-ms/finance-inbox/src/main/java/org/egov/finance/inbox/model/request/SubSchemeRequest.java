/**
 * 
 */
package org.egov.finance.inbox.model.request;

import org.egov.finance.inbox.model.RequestInfo;
import org.egov.finance.inbox.model.SubSchemeModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import lombok.Data;

/**
 * SubSchemeRequest.java
 * 
 * @author bpattanayak
 * @date 11 Jun 2025
 * @version 1.0
 */

@Data
public class SubSchemeRequest {
	
	@JsonProperty("RequestInfo")
	RequestInfo requestInfo;
	@Valid
	@JsonProperty("SubScheme")
	SubSchemeModel subSchemeRequest;

}
