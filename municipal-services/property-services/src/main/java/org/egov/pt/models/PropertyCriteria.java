package org.egov.pt.models;

import java.util.HashSet;
import java.util.Set;

import org.egov.pt.models.enums.Channel;
import org.egov.pt.models.enums.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyCriteria {

	private String tenantId;

	private Set<String> propertyIds;

	private Set<String> tenantIds;

	private Set<String> acknowledgementIds;

	private Set<String> uuids;

	private Set<String> oldpropertyids;

	private Set<Status> status;

	private Set<Channel> channels;

	private Set<Status> statusList;

	private String mobileNumber;

	private String name;

	private Set<String> ownerIds;

	private boolean audit;

	private Long offset;

	private Long limit;

	private Long fromDate;

	private Long toDate;

	private String locality;

	private String doorNo;

	private String oldPropertyId;

	private String propertyType;

	private Set<String> creationReason;

	private Set<String> documentNumbers;

	private Set<String> additionalDetailsPropertyIds;

	@Builder.Default
	private Boolean isSearchInternal = false;

	@Builder.Default
	private Boolean isInboxSearch = false;

	@Builder.Default
	private Boolean isDefaulterNoticeSearch = false;

	@Builder.Default
	private Boolean isRequestForDuplicatePropertyValidation = false;

	private Boolean isCitizen;

	@Builder.Default
	private Boolean isRequestForCount = false;

	@Builder.Default
	private Boolean isRequestForOldDataEncryption = false;

	private Set<String> createdBy;

	@Builder.Default
	private Boolean isSchedulerCall = false;

	private Set<String> addressAdditionalDetailsWardNumbers;

	private Set<String> ownerOldCustomerIds;

	public PropertyCriteria copy() {
		return PropertyCriteria.builder().tenantId(this.tenantId).propertyIds(copySet(this.propertyIds))
				.tenantIds(copySet(this.tenantIds)).acknowledgementIds(copySet(this.acknowledgementIds))
				.uuids(copySet(this.uuids)).oldpropertyids(copySet(this.oldpropertyids)).status(copySet(this.status))
				.channels(copySet(this.channels)).statusList(copySet(this.statusList)).mobileNumber(this.mobileNumber)
				.name(this.name).ownerIds(copySet(this.ownerIds)).audit(this.audit).offset(this.offset)
				.limit(this.limit).fromDate(this.fromDate).toDate(this.toDate).locality(this.locality)
				.doorNo(this.doorNo).oldPropertyId(this.oldPropertyId).propertyType(this.propertyType)
				.creationReason(copySet(this.creationReason)).documentNumbers(copySet(this.documentNumbers))
				.additionalDetailsPropertyIds(copySet(this.additionalDetailsPropertyIds))
				.isSearchInternal(this.isSearchInternal).isInboxSearch(this.isInboxSearch)
				.isDefaulterNoticeSearch(this.isDefaulterNoticeSearch)
				.isRequestForDuplicatePropertyValidation(this.isRequestForDuplicatePropertyValidation)
				.isCitizen(this.isCitizen).isRequestForCount(this.isRequestForCount)
				.isRequestForOldDataEncryption(this.isRequestForOldDataEncryption).createdBy(copySet(this.createdBy))
				.isSchedulerCall(this.isSchedulerCall)
				.addressAdditionalDetailsWardNumbers(copySet(this.addressAdditionalDetailsWardNumbers))
				.ownerOldCustomerIds(copySet(this.ownerOldCustomerIds)).build();
	}

	private <T> Set<T> copySet(Set<T> originalSet) {
		return originalSet != null ? new HashSet<>(originalSet) : null;
	}

}
