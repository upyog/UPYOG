package org.upyog.cdwm.web.models.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.egov.common.contract.request.RequestInfo;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Builder
public class CreateUserRequestV2 {
    private RequestInfo requestInfo;

    @NotNull
    @Valid
    private User user;

}


