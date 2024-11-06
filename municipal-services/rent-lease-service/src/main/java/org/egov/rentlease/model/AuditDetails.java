package org.egov.rentlease.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
public class AuditDetails {

	@JsonInclude(JsonInclude.Include.NON_NULL)

	private String createdBy;

	private Long createdDate;

	private String lastModifiedBy;

	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	private Long lastModifiedDate;

}
