package org.egov.garbageservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.egov.common.contract.request.RequestInfo;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GarbageAccountActionRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo = null;

	private List<String> applicationNumbers;

	private List<String> billStatus;

	private String month;

	private String year;

	private List<String> propertyIds;
	
	private List<String> garbageUuid;

	@Builder.Default
	private Boolean isEmptyBillFilter = false;

}
