package org.egov.pt.web.contracts;

import org.egov.common.contract.request.RequestInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendPropertyNoticeRequest {

    private String tenantId;

    private String billId;

    private String demandId;

    private String propertyId;

    private String email;

    private RequestInfo requestInfo;
}
