package org.upyog.cdwm.web.models;

import digit.models.coremodels.AuditDetails;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CNDApplicantDetail {
    private String applicationId;
    private String nameOfApplicant;
    private String mobileNumber;
    private String alternateMobileNumber;
    private String emailId;
    private AuditDetails auditDetails;
}
