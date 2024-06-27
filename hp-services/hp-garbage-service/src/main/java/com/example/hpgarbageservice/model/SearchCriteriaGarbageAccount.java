package com.example.hpgarbageservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.example.hpgarbageservice.model.contract.RequestInfo;

@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
public class SearchCriteriaGarbageAccount {
	
    private List<Long> id;

    private List<Long> garbageId;

    private List<Long> propertyId;

    private List<String> type;

    private List<String> name;

    private List<String> mobileNumber;

    private List<Long> parentId;
}
