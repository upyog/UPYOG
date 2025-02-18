package org.upyog.cdwm.web.models;

import lombok.*;
import org.egov.common.contract.request.RequestInfo;

import javax.validation.Valid;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CNDApplicationRequest {
    private RequestInfo requestInfo;

    @Valid
    private CNDApplicationDetail cndApplication;
}
