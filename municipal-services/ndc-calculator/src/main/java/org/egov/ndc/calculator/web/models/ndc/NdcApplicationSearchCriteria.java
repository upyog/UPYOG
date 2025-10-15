package org.egov.ndc.calculator.web.models.ndc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NdcApplicationSearchCriteria {
    private String uuid;
    private String tenantId;
    private String status;
    private String mobileNumber;
    private String name;
    private Boolean active;

}