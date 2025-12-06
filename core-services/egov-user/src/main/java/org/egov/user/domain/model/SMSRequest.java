package org.egov.user.domain.model;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SMSRequest {

    private String mobileNumber;

    private String message;

}