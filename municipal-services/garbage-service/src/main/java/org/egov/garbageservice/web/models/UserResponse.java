package org.egov.garbageservice.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.request.User;
import org.egov.common.contract.response.ResponseInfo;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
/**
 * Wrapper for user service responses listing common.contract User entries.
 * Carries ResponseInfo and user list after search or create calls from UserService.
 */
@Builder
public class UserResponse {

    @JsonProperty("responseInfo")
    ResponseInfo responseInfo;

    @JsonProperty("user")
    List<User> user;

}
