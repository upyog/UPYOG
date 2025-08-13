package org.egov.vendor.web.models;

import lombok.Data;

@Data
public class SearchCriteria {
    private String  vendorAdditionalDetailsId;
    private String tenantId;
    private String vendorId;
    private String category;
    private String status;
    private String vendorGroup;
    private String vendorType;
    private String serviceType;
    private String registrationNo;
}