package org.egov.waterquality.web.models.collection;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchCriteria {

	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("tenantIds")
	private Set<String> tenantIds;

	@JsonProperty("ids")
	private Set<String> ids;

	@JsonProperty("applicationos")
	private Set<String> applicationNo;
	
	@JsonProperty("type")
	private ApplicationType type;
	
	@JsonProperty("offset")
	private Integer offset;

	@JsonProperty("limit")
	private Integer limit;

	public enum SortOrder {
	    ASC,
	    DESC
	}

}