package org.egov.user.web.contract;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.egov.common.contract.request.RequestInfo;
import org.egov.user.domain.model.User;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class CreateUserRequestV2 {
    private RequestInfo requestInfo;

    @NotNull
    @Valid
    private UserRequestV2 user;

    public User toDomain(boolean isCreate) {
        return user.toDomain(loggedInUserId(),loggedInUserUuid(), isCreate);
    }

    // TODO Update libraries to have uuid in request info
    private Long loggedInUserId() {
        return requestInfo.getUserInfo() == null ? null : requestInfo.getUserInfo().getId();
    }
    private String loggedInUserUuid() {
        return requestInfo.getUserInfo() == null ? null : requestInfo.getUserInfo().getUuid();
    }

}


