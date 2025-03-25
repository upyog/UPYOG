package org.upyog.cdwm.web.models;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CNDApplicantDetail {

    private String nameOfApplicant;
    private String mobileNumber;
    private String alternateMobileNumber;
    private String emailId;

}
