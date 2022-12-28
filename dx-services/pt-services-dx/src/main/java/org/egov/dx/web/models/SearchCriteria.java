package org.egov.dx.web.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteria {


    private String propertyId;

    private String city;

    private String txn;

    private String origin;

    private String URI;
    
    private String receiptNumber;
    
    private String docType;
    
    private String payerName;
    
    private String mobile;

}
