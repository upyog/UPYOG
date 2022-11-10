package org.egov.filemgmnt.web.models;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

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
	private Long fromDate;

	@JsonProperty("toDate")
	private Long toDate;

	@JsonProperty("offset")
	private Integer offset;

	@JsonProperty("limit")
	private Integer limit;

	@JsonProperty("aadhaarno")
	private String aadhaarno;

	public boolean isEmpty() {
		log.info("this.tenantId " + this.tenantId);
		// return (tenantId == null && this.fileCodes == null);
		return (StringUtils.isBlank(tenantId) && CollectionUtils.isEmpty(fileCodes));
	}

	public boolean tenantIdOnly() {
		// return (tenantId != null);
		return StringUtils.isNotBlank(tenantId);
	}
}
