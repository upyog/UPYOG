package org.egov.tl.web.models.contract;

import java.util.List;

import org.egov.tl.web.models.niuadata.DataItem;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UmeedDashboardResponse {

	@JsonProperty("Data")
	private List<DataItem> data;

}
