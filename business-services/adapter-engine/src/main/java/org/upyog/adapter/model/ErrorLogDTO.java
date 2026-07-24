package org.upyog.adapter.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorLogDTO {

    private String id;
    private String tenantId;
    private String moduleName;
    private String errorDate;
    private String issueDescription;
    private Long createdTime;
    private String createdBy;
}
