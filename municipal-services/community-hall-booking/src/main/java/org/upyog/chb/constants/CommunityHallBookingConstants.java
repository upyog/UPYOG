package org.upyog.chb.constants;

public class CommunityHallBookingConstants {

	public static final String CHB_MODULE = "ChbService";
	public static final String CHB_BUSINESS_SERVICE = "chb";
	public static final String ASSET_MODULE_CODE = "CHB";

	public static final String COMMUNITY_HALL_BOOKING_CREATED = "Your community hall booking created successfully";

	public static final String COMMUNITY_HALL_BOOKING_LIST = "Your community hall booking list fetched successfully";

	public static final String INVALID_TENANT = "INVALID TENANT";

	public static final String CHB_WORKFLOW_WORKFLOW_ERROR = "CHB_WORKFLOW_WORKFLOW_ERROR";

	// mdms path codes
	public static final String CHB_JSONPATH_CODE = "$.MdmsRes.CHB";
	public static final String COMMON_MASTER_JSONPATH_CODE = "$.MdmsRes.common-masters";

	public static final String COMMON_MASTERS_MODULE = "common-masters";

	public static final String CHB_PURPOSE = "Purpose";
	public static final String CHB_RESIDENT_TYPE = "ResidenType";
	public static final String CHB_SPECIAL_CATEGORY = "SpecialCategory";

	public static final String INVALID_TENANT_ID_MDMS_KEY = "INVALID TENANTID";
	public static final String INVALID_TENANT_ID_MDMS_MSG = "No data found for this tenant";

	public static final String EMPLOYEE = "EMPLOYEE";
	public static final String CITIZEN = "CITIZEN";
	public static final String INVALID_SEARCH = "INVALID SEARCH";

	public static final String MOBILE_NUMBER_PARAM = "mobileNumber";
	public static final String BUSINESS_SERVICE_PARAM = "businessservice";
	public static final String TENANT_ID_PARAM = "tenantId";
	public static final String LOCALITY_PARAM = "locality";
	public static final String PTR_APPLICATION_NUMBER_PARAM = "applicationNumber";
	public static final String ASSIGNEE_PARAM = "assignee";
	public static final String STATUS_PARAM = "status";
	public static final String OFFSET_PARAM = "offset";
	public static final String NO_OF_RECORDS_PARAM = "noOfRecords";
	public static final String LIMIT_PARAM = "limit";
	public static final String ACKNOWLEDGEMENT_IDS_PARAM = "acknowledgementIds";
	public static final String REQUESTINFO_PARAM = "RequestInfo";
	public static final String SEARCH_CRITERIA_PARAM = "searchCriteria";
	public static final String USERID_PARAM = "userid";
	public static final String SORT_ORDER_PARAM = "sortOrder";
	public static final String DESC_PARAM = "DESC";
	
	
	// NOTIFICATION PLACEHOLDER
	
	public static final String NOTIFICATION_LOCALE = "en_IN";
	
	public static final String NOTIFICATION_MODULENAME = "rainmaker-chb";

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
	
	
	public static final String NOTIFICATION_VERIFY = "chb.en.counter.verify";

	public static final String NOTIFICATION_APPLY = "chb.en.counter.submit";

	public static final String NOTIFICATION_APPROVE = "chb.en.counter.approve";

	public static final String NOTIFICATION_REJECT = "chb.en.counter.reject";

	public static final String USREVENTS_EVENT_TYPE = "SYSTEMGENERATED";
	
	public static final String USREVENTS_EVENT_NAME = "CHB";

	public static final String USREVENTS_EVENT_POSTEDBY = "SYSTEM-CHB";
	

}
