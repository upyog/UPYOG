package org.egov.ewst.util;

public class EwasteConstants {

	private EwasteConstants() {
	}

    public static final String MDMS_EW_MOD_NAME = "Ewaste";
    
	public static final String MDMS_EW_PRODUCTNAME = "ProductName";

	public static final String NOTIFICATION_LOCALE = "en_IN";

	// Variable names for diff

	public static final String VARIABLE_ACTION = "action";

	public static final String VARIABLE_WFDOCUMENTS = "wfDocuments";

	public static final String VARIABLE_ACTIVE = "active";

	public static final String VARIABLE_USERACTIVE = "status";

	public static final String VARIABLE_CREATEDBY = "createdBy";

	public static final String VARIABLE_LASTMODIFIEDBY = "lastModifiedBy";

	public static final String VARIABLE_CREATEDTIME = "createdTime";

	public static final String VARIABLE_LASTMODIFIEDTIME = "lastModifiedTime";

	/* notification constants */

	public static final String WF_STATUS_PAID = "PAID";

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

	public static final String NOTIFICATION_MODULENAME = "rainmaker-ew";

	public static final String WORKFLOW_SENDBACK_CITIZEN = "SENDBACKTOCITIZEN";

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

	public static final String URL_PARAMS_SEPARATER = "?";

	public static final String TENANT_ID_FIELD_FOR_SEARCH_URL = "tenantId=";

	public static final String LIMIT_FIELD_FOR_SEARCH_URL = "limit=";

	public static final String OFFSET_FIELD_FOR_SEARCH_URL = "offset=";

	public static final String SEPARATER = "&";

	// PDF CONSUMER
	public static final String KEY_ID = "id";

	public static final String KEY_FILESTOREID = "filestoreid";

	public static final String KEY_PDF_TENANT_ID = "tenantId";

	public static final String KEY_PDF_MODULE_NAME = "moduleName";

	public static final String KEY_PDF_FILESTOREID = "filestoreids";

	public static final String KEY_PDF_DOCUMENTTYPE = "documentType";

	public static final String VIEW_APPLICATION_CODE = "View Application";

	public static final String TRACK_APPLICATION_CODE = "TRACK APPLICATION";

	public static final String DOWNLOAD_CERTIFICATE_CODE = "DOWNLOAD CERTIFICATE";

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

	public static final String NOTIFICATION_VERIFY = "ew.en.counter.verify";

	public static final String NOTIFICATION_SUBMIT = "ew.en.counter.submit";

	public static final String NOTIFICATION_PICKUPALERT = "ew.en.counter.sendpickupalert";

	public static final String NOTIFICATION_REJECT = "ew.en.counter.reject";

	public static final String NOTIFICATION_COMPLETE_REQUEST = "ew.en.counter.completerequest";

	public static final String USREVENTS_EVENT_TYPE = "SYSTEMGENERATED";

	public static final String USREVENTS_EVENT_NAME = "EWASTE";

	public static final String USREVENTS_EVENT_POSTEDBY = "SYSTEM-EW";

	public static final String JSONPATH_CODES = "$.MdmsRes.Ewaste";

}
