package org.egov.finance.master.model;

import org.egov.finance.master.customannotation.SafeHtml;

import lombok.Data;

@Data
public class EgwStatusModel {

	private Long id;

	@SafeHtml
	private String moduleType;

	@SafeHtml
	private String code;

	@SafeHtml
	private String description;

	private Long createdBy;
	private java.util.Date createdDate;
	private Long lastModifiedBy;
	private java.util.Date lastModifiedDate;

}
