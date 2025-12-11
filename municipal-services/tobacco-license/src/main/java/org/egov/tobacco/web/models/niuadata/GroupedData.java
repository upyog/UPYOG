package org.egov.tobacco.web.models.niuadata;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupedData {
	private String groupBy;
	private List<Bucket> buckets;
}
