package org.egov.ptr.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PTRConstants {

	private PTRConstants() {
	}

	public static final String MDMS_PT_OWNERTYPE = "OwnerType";

	public static final String MDMS_PT_EGF_MASTER = "egf-master";

	public static final String MDMS_PT_FINANCIALYEAR = "FinancialYear";

	public static final String JSONPATH_FINANCIALYEAR = "$.MdmsRes.egf-master";

	public static final String BOUNDARY_HEIRARCHY_CODE = "REVENUE";

	public static final String NOTIFICATION_LOCALE = "en_IN";

//    
	public static final String ACTION_PAY = "PAY";

	public static final String ACTION_PAID = "PAID";

	// Variable names for diff

	public static final String VARIABLE_ACTION = "action";

	public static final String VARIABLE_WFDOCUMENTS = "wfDocuments";

	public static final String VARIABLE_ACTIVE = "active";

	public static final String VARIABLE_USERACTIVE = "status";

	public static final String VARIABLE_CREATEDBY = "createdBy";

	public static final String VARIABLE_LASTMODIFIEDBY = "lastModifiedBy";

	public static final String VARIABLE_CREATEDTIME = "createdTime";

	public static final String VARIABLE_LASTMODIFIEDTIME = "lastModifiedTime";

	public static final String VARIABLE_OWNER = "ownerInfo";

	public static final String CITIZEN_SENDBACK_ACTION = "SENDBACKTOCITIZEN";

	public static final String WORKFLOW_START_ACTION = "INITIATE";

	public static final String ASMT_WORKFLOW_CODE = "ASMT";

	public static final String ASMT_MODULENAME = "PT";

	public static final String CREATE_PROCESS_CONSTANT = "CREATE";

	public static final String UPDATE_PROCESS_CONSTANT = "UPDATE";

	public static final String MUTATION_PROCESS_CONSTANT = "MUTATION";

	public static final String ALTERNATE_PROCESS_CONSTANT = "ALTERNATE";

	/* notification constants */

	public static final String WF_STATUS_PAID = "PAID";

	public static final String WF_STATUS_PAYMENT_PENDING = "PAYMENT_PENDING";

	public static final String WF_STATUS_REJECTED = "REJECTED";

	public static final String WF_STATUS_FIELDVERIFIED = "FIELDVERIFIED";

	public static final String WF_STATUS_DOCVERIFIED = "DOCVERIFIED";

	public static final String WF_STATUS_CANCELLED = "CANCELLED";

	public static final String WF_STATUS_APPROVED = "APPROVED";

	public static final String WF_STATUS_OPEN = "OPEN";

	public static final String WF_NO_WORKFLOW = "NO_WORKFLOW";

	public static final String ACTION_UPDATE_MOBILE = "UPDATE_MOBILE";

	public static final String ACTION_ALTERNATE_MOBILE = "ALTERNATE_MOBILE";

	public static final String ACTION_FOR_DUES = "DUE";

	public static final String ACTION_FOR_PAYMENT_FAILURE = "FAILURE";

	public static final String WF_STATUS_OPEN_LOCALE = "PT_NOTIF_WF_STATE_LOCALE_OPEN";

	public static final String WF_STATUS_DOCVERIFIED_LOCALE = "PT_NOTIF_WF_STATE_LOCALE_DOCVERIFIED";

	public static final String WF_STATUS_FIELDVERIFIED_LOCALE = "PT_NOTIF_WF_STATE_LOCALE_FIELDVERIFIED";

	public static final String WF_STATUS_APPROVED_LOCALE = "PT_NOTIF_WF_STATE_LOCALE_APPROVED";

	public static final String WF_STATUS_REJECTED_LOCALE = "PT_NOTIF_WF_STATE_LOCALE_REJECTED";

	public static final String WF_STATUS_PAID_LOCALE = "PT_NOTIF_WF_STATE_LOCALE_PAID";

	public static final String NOTIFICATION_MODULENAME = "rainmaker-ptr";

	public static final String WORKFLOW_SENDBACK_CITIZEN = "SENDBACKTOCITIZEN";

	public static final String BILL_AMOUNT_PATH = "/Bill/0/totalAmount";

	public static final String BILL_NO_DEMAND_ERROR_CODE = "EG_BS_BILL_NO_DEMANDS_FOUND";

	public static final String BILL_NO_PAYABLE_DEMAND_ERROR_CODE = "EG_BS_BILL_ZERO_TAX";

	// NOTIFICATION PLACEHOLDER

	public static final String NOTIFICATION_OWNERNAME = "{OWNER_NAME}";

	public static final String NOTIFICATION_EMAIL = "{EMAIL_ID}";

	public static final String NOTIFICATION_STATUS = "{STATUS}";

	public static final String NOTIFICATION_UPDATED_CREATED_REPLACE = "{updated/created}";

	public static final String CREATE_STRING = "Create";

	public static final String UPDATE_STRING = "Update";

	public static final String CREATED_STRING = "Created";

	public static final String UPDATED_STRING = "Updated";

	public static final String NOTIFICATION_APPID = "{APPID}";

	public static final String NOTIFICATION_CONSUMERCODE = "{CONSUMERCODE}";

	public static final String NOTIFICATION_TENANTID = "{TENANTID}";

	public static final String NOTIFICATION_BUSINESSSERVICE = "{BUSINESSSERVICE}";

	public static final String NOTIFICATION_PAY_LINK = "{PAYLINK}";

	public static final String NOTIFICATION_MUTATION_LINK = "{MTURL}";

	public static final String NOTIFICATION_AMOUNT = "{AMOUNT}";

	public static final String ONLINE_PAYMENT_MODE = "ONLINE";

	public static final String URL_PARAMS_SEPARATER = "?";

	public static final String TENANT_ID_FIELD_FOR_SEARCH_URL = "tenantId=";

	public static final String LIMIT_FIELD_FOR_SEARCH_URL = "limit=";

	public static final String OFFSET_FIELD_FOR_SEARCH_URL = "offset=";

	public static final String SEPARATER = "&";

	// PDF CONSUMER
	public static final String KEY_ID = "id";

	public static final String KEY_FILESTOREID = "filestoreid";

	public static final String KEY_PDF_JOBS = "jobs";

	public static final String KEY_PDF_ENTITY_ID = "entityid";

	public static final String KEY_PDF_TENANT_ID = "tenantId";

	public static final String KEY_PDF_MODULE_NAME = "moduleName";

	public static final String KEY_PDF_FILESTOREID = "filestoreids";

	public static final String KEY_PDF_DOCUMENTTYPE = "documentType";

	public static final String ASMT_USER_EVENT_PAY = "pay";

	public static final String VIEW_APPLICATION_CODE = "View Application";

	public static final String TRACK_APPLICATION_CODE = "TRACK APPLICATION";

	public static final String DOWNLOAD_CERTIFICATE_CODE = "DOWNLOAD CERTIFICATE";

	public static final String DOWNLOAD_RECEIPT_CODE = "DOWNLOAD RECEIPT";

	// Notification Enhancement
	public static final String CHANNEL_NAME_SMS = "SMS";

	public static final String CHANNEL_NAME_EVENT = "EVENT";

	public static final String CHANNEL_NAME_EMAIL = "EMAIL";

	public static final String MODULE = "module";

	public static final String ACTION = "action";

	public static final String CHANNEL_LIST = "channelList";

	public static final String CHANNEL = "Channel";

	public static final String LOCALIZATION_CODES_JSONPATH = "$.messages.*.code";

	public static final String LOCALIZATION_MSGS_JSONPATH = "$.messages.*.message";

	// EVENT PAY
	public static final String EVENT_PAY_TENANTID = "$tenantId";

	public static final String EVENT_PAY_BUSINESSSERVICE = "$businessService";

	public static final String TRACK_APPLICATION_STRING = "You can track your application on the link given below - {PTURL} Thank you";

	public static final String PAY_ONLINE_STRING = "Click on the URL to view the details and pay online {PAYMENT_LINK}";

	public static final String PAYLINK_STRING = "You can pay your application fee on the below link - {PAYLINK} or visit your ULB to pay your dues. Thank you";

	public static final String CERTIFICATE_STRING = "You can download your certificate on the below link - {PTRURL} Thank you";

	public static final String RECEIPT_STRING = "You can download your receipt on the below link - {PTRURL} Thank you";

	public static final String NOTIFICATION_VERIFY = "ptr.en.counter.verify";

	public static final String NOTIFICATION_APPLY = "ptr.en.counter.submit";

	public static final String NOTIFICATION_APPROVE = "ptr.en.counter.approve";

	public static final String NOTIFICATION_REJECT = "ptr.en.counter.reject";

	public static final String USREVENTS_EVENT_TYPE = "SYSTEMGENERATED";

	public static final String USREVENTS_EVENT_NAME = "PTR";

	public static final String USREVENTS_EVENT_POSTEDBY = "SYSTEM-PTR";

	// APPLICATION STATUS
	public static final String APPLICATION_STATUS_INITIATED = "INITIATED";
	public static final String APPLICATION_STATUS_PENDINGFORMODIFICATION = "PENDINGFORMODIFICATION";
	public static final String APPLICATION_STATUS_PENDINGFORPAYMENT = "PENDINGFORPAYMENT";
	public static final String APPLICATION_STATUS_PENDINGFORVERIFICATION = "PENDINGFORVERIFICATION";
	public static final String APPLICATION_STATUS_PENDINGFORAPPROVAL = "PENDINGFORAPPROVAL";
	public static final String APPLICATION_STATUS_APPROVED = "APPROVED";
	public static final String APPLICATION_STATUS_REJECTED = "REJECTED";

	
	public static final String USER_TYPE_CITIZEN = "CITIZEN";
	
	public static final String USER_TYPE_EMPLOYEE = "EMPLOYEE";
	
	public static final String USER_ROLE_PTR_VERIFIER = "PTR_VERIFIER";
	
	public static final String USER_ROLE_PTR_APPROVER = "PTR_APPROVER";
}
