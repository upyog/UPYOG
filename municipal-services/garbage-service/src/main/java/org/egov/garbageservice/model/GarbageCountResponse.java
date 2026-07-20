package org.egov.garbageservice.model;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
/**
 * Response for aggregate garbage application counts exposed via common/count APIs.
 * Returns tabular countsData and total application count for dashboards.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GarbageCountResponse {
    private List<Map<String, Object>> countsData;
	private long  applicationTotalCount;
}
