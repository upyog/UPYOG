package org.egov.advertisementcanopy.util;

import lombok.Data;

@Data
public class SiteConstants {
	public static final String WORKFLOW_ACTION_FORWARD_TO_APPROVER  = "FORWARD_TO_APPROVER";
	public static final String WORKFLOW_ACTION_PENDING_FOR_MODIFICATION = "PENDING_FOR_MODIFICATION";
	public static final String WORKFLOW_ACTION_INITIATE= "INITIATED";
	public static final String WORKFLOW_ACTION_APPROVE ="APPROVE";
	public static final String WORKFLOW_ACTION_REJECT = "REJECT";
	public static final String WORKFLOW_ACTION_RETURN_TO_INITIATOR = "RETURN_TO_INITIATOR"; 
    public static final String USER_ROLE_SITE_REVIEWER = "SITE_CREATOR";
	public static final String USER_ROLE_SITE_APPROVER = "SITE_APPROVER";
	public static final String APPLICATION_STATUS_INITIATED = "INITIATED";
	public static final String APPLICATION_STATUS_PENDINGFORMODIFICATION = "PENDINGFORMODIFICATION";
	public static final String APPLICATION_STATUS_PENDINGFORPAYMENT = "PENDINGFORPAYMENT";
	public static final String APPLICATION_STATUS_PENDINGFORVERIFICATION = "PENDINGFORVERIFICATION";
	public static final String APPLICATION_STATUS_PENDINGFORAPPROVAL = "PENDINGFORAPPROVAL";
	public static final String APPLICATION_STATUS_APPROVED = "APPROVED";
	public static final String APPLICATION_STATUS_REJECTED = "REJECTED";
}
	
