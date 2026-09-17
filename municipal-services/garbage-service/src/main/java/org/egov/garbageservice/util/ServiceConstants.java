package org.egov.garbageservice.util;

import org.springframework.stereotype.Component;

/**
 * Constants class defining service-level identifiers and tax-head category keys.
 */
@Component
public class ServiceConstants {
    public static final String GRBG_TAX_HEAD_CODE = "GC_RENTAL_FEE";
    public static final String GRBG_PENALTY_FEE = "GC_PENALTY_FEE";
    public static final String GRBG_REBATE_FEE = "GC_REBATE_FEE";
    public static final String PAYMENT_TYPE_FULL = "FULL";
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_SYSTEM = "system";
    public static final String STATUS_APPROVED = "APPROVED";
    public static final String STATUS_PENDING_FOR_PAYMENT = "PENDING_FOR_PAYMENT";
    public static final String STATUS_PAID = "PAID";
}