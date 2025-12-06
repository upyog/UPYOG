package org.upyog.web.models;

import lombok.*;
import org.springframework.validation.annotation.Validated;
<<<<<<< HEAD
import org.upyog.validation.SanitizeHtml;

@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-12-07T15:40:06.365+05:30")
=======

@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-12-07T15:40:06.365+05:30")
>>>>>>> master-LTS

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModuleSearchCriteria {
    private String tenantId = null;

<<<<<<< HEAD
	@SanitizeHtml(message = "Invalid Application Number")
    private String applicationNumber = null;

    @SanitizeHtml
=======
    private String applicationNumber = null;

>>>>>>> master-LTS
    private String moduleName = null;

}
