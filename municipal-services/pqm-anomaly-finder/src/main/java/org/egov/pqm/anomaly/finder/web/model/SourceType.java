package org.egov.pqm.anomaly.finder.web.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SourceType {
	LAB_SCHEDULED,
	  IOT_SCHEDULED,
	  LAB_ADHOC,
	  TEST_RESULT_NOT_SUBMITTED
}