package org.egov.pqm.web.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SortBy {

	id,
	wfStatus,
	testId,
	plantCode,
	processCode,
	stageCode,
	materialCode,
	deviceCode,
    createdTime,
    scheduledDate,
    plantUserUuid
}
