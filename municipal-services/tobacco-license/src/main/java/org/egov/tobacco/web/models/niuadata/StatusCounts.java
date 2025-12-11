package org.egov.tobacco.web.models.niuadata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatusCounts {
	private String status;
	private long createdTodayCount;
	private long modifiedTodayCount;

}
