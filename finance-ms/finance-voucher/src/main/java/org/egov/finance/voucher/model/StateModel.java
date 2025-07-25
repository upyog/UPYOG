package org.egov.finance.voucher.model;

import java.util.Date;

import lombok.Data;

@Data
public class StateModel {

	private Long id;
	private String type;
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
	private StateStatus status;
	private Long initiatorPosition;
	private Long previousOwner;
	private Long previousStateRefId;
	private String deptCode;
	private String deptName;
	private String desgCode;
	private String desgName;

	public enum StateStatus {
		STARTED, INPROGRESS, ENDED
	}

}
