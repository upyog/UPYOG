package org.upyog.sv.web.models;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RenewalStatus {
    ELIGIBLE_TO_RENEW("EligibleToRenew"),// when a application is eligible for renewal as set in scheduler
    RENEW_IN_PROGRESS("RenewInProgress"),// when first draft call is made or straight to payment is clicked
    RENEW_APPLICATION_CREATED("RenewApplicationCreated"),// when renewed application is created and saved
    RENEWED("Renewed");// when the application is renewed 

    private final String value;

    RenewalStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
