package org.egov.garbageservice.repository;

import java.math.BigDecimal;

/** Projection interface representing a flattened view of bill and owner details used for SMS notification. */
public interface BillSmsView {

    String getUuid();

    String getGrbgApplicationId();

    String getTenantId();

    String getMonth();

    String getYear();

    String getFromDate();

    String getToDate();

    String getBillId();

    BigDecimal getGrbgBillAmount();

    String getMobileNumber();
    
    String getWard();
    
    String getLastModifiedBy();
    
    String getOwnerName();
}
