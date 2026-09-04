package org.upyog.dashboard.model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ErrorLogDTO {

    private String id;
    private String tenantId;
    private String moduleName;
    private String errorDate;
    private String issueDescription;
    private Long createdTime;
    private String createdBy;
}
