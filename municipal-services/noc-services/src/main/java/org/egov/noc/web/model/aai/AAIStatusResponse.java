package org.egov.noc.web.model.aai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AAIStatusResponse {
    private List<AAIApplicationStatus> applicationStatuses;
    private Boolean success;
    private String errorMessage;
    private String errorCode;
}
