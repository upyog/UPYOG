package org.egov.tl.web.models.niuadata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DataItem {
	private String date;
	private String module;
	private String ward;
	private String ulb;
	private String region;
	private String state;
	private Metrics metrics;
}
