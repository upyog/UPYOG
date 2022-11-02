package org.egov.filemgmnt.web.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
public class ApplicantPersonalSearchCriteria {

	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("ids")
	private List<String> ids;

	@JsonProperty("fileCodes")
	private List<String> fileCodes;

	@JsonProperty("fromDate")
	private Long fromDate = null;

	@JsonProperty("toDate")
	private Long toDate = null;

	@JsonProperty("offset")
	private Integer offset;

	@JsonProperty("limit")
	private Integer limit;

	public boolean isEmpty() {
		log.info("this.tenantId " + this.tenantId);
		return (this.tenantId == null && this.fileCodes == null);
	}

	public boolean tenantIdOnly() {
		return (this.tenantId != null);
	}
}
