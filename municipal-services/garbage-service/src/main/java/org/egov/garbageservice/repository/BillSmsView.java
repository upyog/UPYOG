package org.egov.garbageservice.repository;

import java.math.BigDecimal;

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
