package org.upyog.sv.constants;

public class StreetVendingConstants {

	public static final String EMPLOYEE = "EMPLOYEE";
	public static final String CITIZEN = "CITIZEN";
	public static final String SYSTEM= "SYSTEM";
	public static final String APPLICATION_CREATED = "Application Created";
	public static final String INVALID_TENANT = "INVALID TENANT";
	public static final String INVALID_SEARCH = "INVALID SEARCH";
	public static final String APPLICATIONS_FOUND = "Applications Found";
	public static final String APPLICATION_UPDATED = "Application Updated";
	public static final String REGISTRATION_COMPLETED = "REGISTRATIONCOMPLETED";
	public static final String INSPECTION_PENDING_STATUS = "INPECTIONPENDING";
	public static final String DUPLICATE_DOCUMENT_UPLOADED = "DUPLICATE_DOCUMENT_UPLOADED";
	public static final String INVALID_APPLICATION = "INVALID APPLICATION";
	public static final String DRAFT_DISCARDED = "Draft discarded";
	public static final String RENEWAL_DEMAND_CREATED = "Renewal Demand created successfully";

	// mdms path codes
	public static final String SV_MASTER_MODULE_NAME = "StreetVending";
	public static final String SV_JSONPATH_CODE = "$.MdmsRes.StreetVending";
	public static final String COMMON_MASTER_JSONPATH_CODE = "$.MdmsRes.common-masters";
	public static final String COMMON_MASTERS_MODULE = "common-masters";

	// Street vending master data
	public static final String VENDING_ZONES = "VendingZones";
	public static final String VENDING_ACTIVITY_TYPE = "VendingActivityType";
	public static final String DOCUMENTS = "Documents";
	public static final String VENDOR_LOCALITY = "VendorLocality";
	

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

	public static final String USREVENTS_EVENT_NAME = "Street-Vending";

	public static final String USREVENTS_EVENT_POSTEDBY = "SYSTEM-SV";

	public static final String NOTIFICATION_LOCALE = "en_IN";

	public static final String NOTIFICATION_MODULE_NAME = "rainmaker-sv";

	public static final String NOTIFICATION_OWNERNAME = "{OWNER_NAME}";

	public static final String NOTIFICATION_EMAIL = "{EMAIL_ID}";

	public static final String NOTIFICATION_STATUS = "{STATUS}";

	public static final String NOTIFICATION_INSPECTION = "sv.en.counter.inspection";

	public static final String NOTIFICATION_INSPECTION_COMPLETE = "sv.en.counter.inspection.complete";

	public static final String NOTIFICATION_SUBMIT = "sv.en.counter.submit";

	public static final String NOTIFICATION_SENTBACK = "sv.en.counter.sentback";

	public static final String NOTIFICATION_REJECT = "sv.en.counter.reject";

	public static final String NOTIFICATION_APPROVED = "sv.en.counter.approved";

	public static final String NOTIFICATION_REGISTRATIONCOMPLETED = "sv.en.counter.registrationcompleted";

	public static final String NOTIFICATION_ELIGIBLETORENEW = "sv.en.counter.eligibletorenew";

	public static final String SV_APPLICANT_DETAIL_ENCRYPTION_KEY = "SVApplicantDetail";

	public static final String SV_APPLICANT_DETAIL_PLAIN_DECRYPTION_KEY = "SVApplicantDetailDecrypDisabled";

	public static final String SV_APPLICANT_DETAIL_PLAIN_DECRYPTION_PURPOSE = "SVApplicantDetailDecryptionDisabled";

	public static final String NOTIFICATION_APPLICATIONEXPIRED = "sv.en.counter.applicationexpired";
	
	public static final String NOTIFICATION_SCHEDULEPAYMENT = "sv.en.schedule.payment";

	public static final String ACTION_STATUS_APPLY = "APPLY";

	public static final String ACTION_STATUS_FORWARD = "FORWARD";
	
	public static final String ACTION_STATUS_APPROVE = "APPROVE";

	public static final String ACTION_STATUS_SENDBACKTOCITIZEN = "SENDBACKTOCITIZEN";

	public static final String ACTION_STATUS_REJECT = "REJECT";
	
	public static final String MESSAGE_TEXT = "MESSAGE_TEXT";

	public static final String ACTION_STATUS_PAY = "PAY";

	public static final String ACTION_STATUS_ELIGIBLE_TO_RENEW = "ELIGIBLETORENEW";

	public static final String ACTION_STATUS_APPLICATION_EXPIRED = "APPLICATIONEXPIRED";

	public static final String SYSTEM_CITIZEN_TENANTID = "pg";

	public static final String STATUS_EXPIRED = "EXPIRED";

    public static final String APPLICATION_STATUS_RENEWED = "RENEWED";
    
    public static final String ACTION_LINK = "ACTION_LINK";
    
    public static final String NOTIFICATION_PAY_NOW = "PAY NOW";
	
	public static final String NOTIFICATION_DOWNLOAD_RECEIPT = "Download Receipt";
	
	public static final String NOTIFICATION_ACTION = "{Action Button}";
	
	public static final String NOTIFICATION_ACTION_BUTTON = "{/Action Button}";
	
	public static final String MONTHLY = "MONTHLY";
	
	public static final String SVMONTHLYFEE = "SV.MONTHLY_FEE";
	
	public static final String SVQUATERLYYFEE = "SV.QUATERLY_FEE";
	
	public static final String SVONETIMEFEE = "SV.ONE_TIME_FEE";
	
	public static final String QUATERLY = "QUATERLY";
	
	public static final String HALFYEARLY = "HALFYEARLY";
	
	public static final String YEARLY = "YEARLY";
	
	public static final String TAXHEADMONTHLY = "SV_STREET_VENDING_MONTHLY_FEE";
	
	public static final String TAXHEADQUATERLY = "SV_STREET_VENDING_QUATERLY_FEE";
	
	public static final String CODE = "code";
	
	public static final String NAME = "name";
	
	public static final String TENANTBOUNDARY = "TenantBoundary";
	
	public static final String BOUNDARY = "boundary";
	
	public static final String ACTION_STATUS_SCHEDULE_PAYMENT = "SCHEDULE_PAYMENT_PENDING";
	
	public static final String DATEFORMAT = "dd-MM-yyyy";
	
    
}
