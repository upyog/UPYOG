package org.egov.dx.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.egov.common.contract.request.RequestInfo;
import java.util.List;

@AllArgsConstructor
@Getter
public class UserRequest {
    @JsonProperty("requestInfo")
    RequestInfo requestInfo;

    @JsonProperty("user")
    User user;

	public void setUser(User user) {
		// TODO Auto-generated method stub
		
	}
}
