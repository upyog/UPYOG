package org.egov.garbageservice.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BillingUser {

    private String name;
    private String emailId;
    private String mobileNumber;
    private String tenantId;
}