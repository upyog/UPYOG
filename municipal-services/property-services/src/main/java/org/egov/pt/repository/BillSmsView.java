package org.egov.pt.repository;

import java.math.BigDecimal;

public interface BillSmsView {

    String getUuid();

    String getGrbgApplicationId();

    String getTenantId();

    String getMonth();

    String getYear();

    String getFromDate();

    String getToDate();
    
    String getFinancialYear();

    String getBillId();

    BigDecimal getGrbgBillAmount();

    String getMobileNumber();
    
    String getContactNumber(); 
    
    String getWard();
    
    String getLastModifiedBy();
    
    String getOwnerName();
    
    
}
