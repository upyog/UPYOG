package org.egov.finance.voucher.model;

import java.util.Date;

import lombok.Data;

@Data
public class StateHistoryModel {

	private Long id;
	private Long createdBy;
	private Date createdDate;
	private Long lastModifiedBy;
	private Date lastModifiedDate;
	private Long stateId;
	private String value;
	private Long ownerPosition;
	private Long ownerUser;
	private String senderName;
	private String nextAction;
	private String comments;
	private String natureOfTask;
	private String extraInfo;
	private Date dateInfo;
	private Date extraDateInfo;
	private Long initiatorPosition;

}
