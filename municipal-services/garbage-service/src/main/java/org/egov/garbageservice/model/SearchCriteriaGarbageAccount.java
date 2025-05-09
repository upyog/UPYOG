package org.egov.garbageservice.model;

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
    
    private List<String> uuid;

    private List<String> type;

    private List<String> name;

    private List<String> mobileNumber;

    private List<String> createdBy;

    private List<String> applicationNumber;

    private String tenantId;

    private List<String> status;
    
    private List<String> statusList;
    
    private List<String> channels;
    
    private List<String> wardNames;
    
    private List<String> oldGarbageIds;
    
    private List<String> unitTypes;
    
    private List<String> unitCategories;
    
    private Boolean isOwner;
    
    private String parentAccount;

    private String orderBy = "DESC";

    private Long startId;

    private Long endId;
    
    private Boolean isActiveSubAccount;
    
    private Boolean isActiveAccount;
    
    @Builder.Default
	private Boolean isSchedulerCall = false;
    
    private Long offset;

	private Long limit;
	
	public SearchCriteriaGarbageAccount copy() {
		return SearchCriteriaGarbageAccount.builder().id(copyList(this.id)).garbageId(copyList(this.garbageId))
				.propertyId(copyList(this.propertyId)).uuid(copyList(this.uuid)).type(copyList(this.type))
				.name(copyList(this.name)).mobileNumber(copyList(this.mobileNumber)).createdBy(copyList(this.createdBy))
				.applicationNumber(copyList(this.applicationNumber)).tenantId(this.tenantId)
				.status(copyList(this.status)).statusList(copyList(this.statusList)).isOwner(this.isOwner)
				.parentAccount(this.parentAccount).orderBy(this.orderBy).startId(this.startId).endId(this.endId)
				.isActiveSubAccount(this.isActiveSubAccount).isActiveAccount(this.isActiveAccount)
				.isSchedulerCall(this.isSchedulerCall).offset(this.offset).limit(this.limit)
				.channels(copyList(this.channels)).wardNames(copyList(this.wardNames))
				.oldGarbageIds(copyList(this.oldGarbageIds)).unitTypes(copyList(this.unitTypes))
				.unitCategories(copyList(this.unitCategories)).build();
	}

	private <T> List<T> copyList(List<T> originalList) {
		return originalList != null ? new java.util.ArrayList<>(originalList) : null;
	}
    
}
