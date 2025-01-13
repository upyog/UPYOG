package org.egov.pg.web.models;

import org.egov.pg.models.enums.Category;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class SMSRequest {
    private String mobileNumber;
    private String message;
    private Category category;
    private Long expiryTime;

}