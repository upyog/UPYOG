package org.upyog.web.models;

import lombok.*;
import org.springframework.validation.annotation.Validated;

@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-12-07T15:40:06.365+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModuleSearchCriteria {
    private String tenantId = null;

    private String applicationNumber = null;

    private String moduleName = null;

}
