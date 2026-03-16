package org.egov.finance.inbox.model;

import java.util.Date;

import lombok.Data;

@Data
public class AccountDetailKeyModel {

	private Long id;

	private Long accountDetailTypeId;

	private Long key;

	private Long createdBy;
	private Date createdDate;
	private Long lastModifiedBy;
	private Date lastModifiedDate;

}
