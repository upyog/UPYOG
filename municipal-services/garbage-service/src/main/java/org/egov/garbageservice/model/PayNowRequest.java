package org.egov.garbageservice.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PayNowRequest {

	private String userUuid;

	private List<String> garbageApplicationNumbers;

	private List<String> garbageUuid;

	private List<String> billStatus;

	private String month;

	private String year;

	private List<String> propertyIds;
	
	private List<Long> garbageIds;


	@Builder.Default
	private Boolean isEmptyBillFilter = false;

}
