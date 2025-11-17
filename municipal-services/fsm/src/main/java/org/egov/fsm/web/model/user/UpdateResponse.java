package org.egov.fsm.web.model.user;

import java.util.List;

import org.egov.common.contract.response.ResponseInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response object for user update operations from egov-user service
 * Maps to the UpdateResponse returned by /users/_updatenovalidate endpoint
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateResponse {

	@JsonProperty("responseInfo")
    ResponseInfo responseInfo;

    @JsonProperty("user")
    List<UpdateRequest> user;
}
