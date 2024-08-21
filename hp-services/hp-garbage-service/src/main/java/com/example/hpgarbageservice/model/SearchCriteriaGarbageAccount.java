package com.example.hpgarbageservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
public class SearchCriteriaGarbageAccount {
	
    private List<Long> id;

    private List<Long> garbageId;

    private List<String> propertyId;

    private List<String> type;

    private List<String> name;

    private List<String> mobileNumber;

    private List<String> createdBy;

    private List<String> applicationNumber;

    private String tenantId;

    private List<String> status;
    
    private Boolean isOwner;
}
