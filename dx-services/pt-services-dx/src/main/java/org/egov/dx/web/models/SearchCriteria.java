package org.egov.dx.web.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

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

}
