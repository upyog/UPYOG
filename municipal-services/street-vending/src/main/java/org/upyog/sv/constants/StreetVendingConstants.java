package org.upyog.sv.constants;

public class StreetVendingConstants {

	public static final String EMPLOYEE = "EMPLOYEE";
	public static final String CITIZEN = "CITIZEN";
	public static final String APPLICATION_CREATED = "Application Created";
	public static final String INVALID_TENANT = "INVALID TENANT";
	public static final String INVALID_SEARCH = "INVALID SEARCH";
	public static final String APPLICATIONS_FOUND = "Applications Found";
	public static final String APPLICATION_UPDATED = "Application Updated";
	public static final String REGISTRATION_COMPLETED = "Registration Completed";

	public static final String DUPLICATE_DOCUMENT_UPLOADED = "DUPLICATE_DOCUMENT_UPLOADED";

	// mdms path codes
	public static final String SV_MASTER_MODULE_NAME = "StreetVending";
	public static final String SV_JSONPATH_CODE = "$.MdmsRes.StreetVending";
	public static final String COMMON_MASTER_JSONPATH_CODE = "$.MdmsRes.common-masters";
	public static final String COMMON_MASTERS_MODULE = "common-masters";

	// Street vending master data
	public static final String VENDIING_ZONES = "VendingZones";
	public static final String VENDING_ACTIVITY_TYPE = "VendingActivityType";
	public static final String DOCUMENTS = "Documents";

	// For demand generation constants
	public static final String BILLING_SERVICE = "BillingService";
	public static final String CALCULATION_TYPE = "CalculationType";
	public static final String ACTION_PAY = "PAY";
	public static final String ACTION_APPROVE = "APPROVE";
	public static final String VENDOR = "VENDOR";

	public static final String INVALID_TENANT_ID_MDMS_KEY = "INVALID TENANTID";
	public static final String INVALID_TENANT_ID_MDMS_MSG = "No data found for this tenant";

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

	public static final String USREVENTS_EVENT_TYPE = "SYSTEMGENERATED";

	public static final String USREVENTS_EVENT_NAME = "Street Vending";

	public static final String USREVENTS_EVENT_POSTEDBY = "SYSTEM-CHB";
	
	public static final String NOTIFICATION_LOCALE = "en_IN";
	
	public static final String NOTIFICATION_MODULE_NAME = "rainmaker-sv";
	
	public static final String NOTIFICATION_OWNERNAME = "{OWNER_NAME}";
	
	public static final String NOTIFICATION_EMAIL = "{EMAIL_ID}";

	public static final String NOTIFICATION_STATUS = "{STATUS}";
	

	public static final String NOTIFICATION_INSPECTION = "ew.en.counter.inspection";

	public static final String NOTIFICATION_SUBMIT = "ew.en.counter.submit";

	public static final String NOTIFICATION_SENTBACK = "ew.en.counter.sentback";

	public static final String NOTIFICATION_REJECT = "ew.en.counter.reject";
	
	public static final String NOTIFICATION_APPROVED = "ew.en.counter.approved";

	public static final String NOTIFICATION_REGISTRATIONCOMPLETED = "ew.en.counter.registrationcompleted";

}
