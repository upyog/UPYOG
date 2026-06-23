package org.egov.rl.calculator.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RLConstants {

    private RLConstants() {}
    
    public static final String TIME_ZONE = "Asia/Kolkata";

    public static final String NEW_RL_APPLICATION = "NEW";
    
    public static final String RL_WORKFLOW_NAME = "RENT_N_LEASE_NEW";
    
    public static final String RL_WORKFLOW_NAME_LEGACY = "RENT_AND_LEASE_LG";

	public static final String RENEWAL_RL_APPLICATION = "RENEWAL";
	
    public static final String APPROVED_RL_APPLICATION = "APPROVE";

	public static final String RL_ALLOTMENT_FEE = "RL_ALLOTMENT_FEE";
    public static final String ROUND_OFF_TAX_HEAD_CODE = "RL_FEE_ROUND_OFF";

	public static final String RL_MASTER_MODULE_NAME = "rentAndLease";

	public static final String RL_SERVICE_NAME = "rl-services";

	public static final String SECURITY_DEPOSIT_FEE_RL_APPLICATION = "RL_SECURITY_DEPOSIT_FEE";

	public static final String RENT_LEASE_FEE_RL_APPLICATION = "RENT_LEASE_FEE";

	public static final String COWCESS_FEE_RL_APPLICATION = "RL_COWCESS_FEE";

	public static final String PENALTY_FEE_RL_APPLICATION = "RL_PENALTY_FEE";

    public static final String RL_DAILYINTEREST = "RL_DAILYINTEREST";
	
	public static final String SGST_FEE_RL_APPLICATION = "RL_SGST_FEE";

	public static final String ROUND_OFF_RL_APPLICATION = "RL_FEE_ROUND_OFF";

    public static final String RL_ARREAR_FEE = "RL_ARREAR_FEE";
    public static final String APPLICATION_TYPE_LEGACY = "Legacy";
    
    // Legacy arrear keys for additionalDetails
    public static final String LEGACY_ARREAR_KEY = "arrear";
    public static final String LEGACY_ARREAR_START_DATE_KEY = "arrearStartDate";
    public static final String LEGACY_LAST_BILLING_PERIOD_KEY = "lastBillingPeriod";

	public static final String CGST_FEE_RL_APPLICATION = "RL_CGST_FEE";
	
	public static final String RL_MONTHLY_CYCLE = "MONTHLY";
	public static final String RL_QUATERLY_CYCLE = "QUATERLY";
	public static final String RL_BIAANNUALY_CYCLE = "BIANNUAL";
	public static final String RL_YEARLY_CYCLE = "ANNUAL";
	
	
    public static final String TAX_PERIOD_MASTER = "TaxPeriod";
    public static final String BILLING_SERVICE_MASTER = "BillingService";
    public static final String DEMAND_CANCELLED_STATUS = "CANCELLED";

    public static final String NOTIFICATION_VERIFY = "rl.en.counter.verify";

	public static final String NOTIFICATION_APPLY = "rl.en.counter.submit";

	public static final String NOTIFICATION_APPROVE = "rl.en.counter.approve";

	public static final String NOTIFICATION_REJECT = "rl.en.counter.reject";

	
    public static final String MDMS_RL_MOD_NAME = "rentAndLease";

    public static final String PT_TYPE_VACANT = "VACANT";
    
    public static final String PT_TYPE_SHAREDPROPERTY = "SHAREDPROPERTY";
    
    public static final String PT_TYPE_BUILTUP = "BUILTUP";
    
    public static final String JSONPATH_CODES = "$.MdmsRes.PropertyTax";

    public static final String MDMS_PT_MOD_NAME = "PropertyTax";

    public static final String MDMS_PT_PROPERTYTYPE = "PropertyType";
    
    public static final String MDMS_PT_MUTATIONREASON = "MutationReason";
    
    public static final String MDMS_PT_USAGECATEGORY = "UsageCategory";

    public static final String MDMS_PT_PROPERTYSUBTYPE = "PropertySubType";

    public static final String MDMS_PT_OCCUPANCYTYPE = "OccupancyType";

    public static final String MDMS_PT_CONSTRUCTIONTYPE = "ConstructionType";

    public static final String MDMS_PT_CONSTRUCTIONSUBTYPE = "ConstructionSubType";

    public static final String MDMS_PT_OWNERSHIPCATEGORY = "OwnerShipCategory";

    public static final String MDMS_PT_SUBOWNERSHIP = "SubOwnerShipCategory";

    public static final String MDMS_PT_USAGEMAJOR = "UsageCategoryMajor";

    public static final String MDMS_PT_USAGEMINOR = "UsageCategoryMinor";

    public static final String MDMS_PT_USAGEDETAIL = "UsageCategoryDetail";

    public static final String MDMS_PT_USAGESUBMINOR = "UsageCategorySubMinor";

    public static final String MDMS_PT_OWNERTYPE = "OwnerType";

    public static final String MDMS_PT_EGF_MASTER = "egf-master";

    public static final String MDMS_PT_FINANCIALYEAR = "FinancialYear";

    public static final String JSONPATH_FINANCIALYEAR = "$.MdmsRes.egf-master";

    public static final String BOUNDARY_HEIRARCHY_CODE = "REVENUE";

    public static final String NOTIFICATION_LOCALE = "en_IN";

    public static final String NOTIFICATION_CREATE_CODE = "pt.property.en.create";

    public static final String NOTIFICATION_UPDATE_CODE = "pt.property.en.update";

    public static final String NOTIFICATION_EMPLOYEE_UPDATE_CODE = "pt.property.en.update.employee";

    public static final String NOTIFICATION_PAYMENT_ONLINE = "PT_NOTIFICATION_PAYMENT_ONLINE";

    public static final String NOTIFICATION_PAYMENT_OFFLINE = "PT_NOTIFICATION_PAYMENT_OFFLINE";

    public static final String NOTIFICATION_PAYMENT_FAIL = "PT_NOTIFICATION_PAYMENT_FAIL";

    public static final String NOTIFICATION_PAYMENT_PARTIAL_OFFLINE = "PT_NOTIFICATION_PAYMENT_PARTIAL_OFFLINE";

    public static final String NOTIFICATION_PAYMENT_PARTIAL_ONLINE = "PT_NOTIFICATION_PAYMENT_PARTIAL_ONLINE";
    
    public static final String DUES_NOTIFICATION = "DUES_NOTIFICATION";

    public static final String NOTIFICATION_OLDPROPERTYID_ABSENT = "pt.oldpropertyid.absent";

    public static final List<String> ASSESSSMENT_NOTIFICATION_CODES = Collections.unmodifiableList(Arrays.asList(NOTIFICATION_PAYMENT_ONLINE,
            NOTIFICATION_PAYMENT_OFFLINE, NOTIFICATION_PAYMENT_FAIL,NOTIFICATION_PAYMENT_PARTIAL_OFFLINE,
            NOTIFICATION_PAYMENT_PARTIAL_ONLINE,NOTIFICATION_OLDPROPERTYID_ABSENT));
    
    public static final String ACTION_PAY = "PAY";

    public static final String ACTION_PAID = "PAID";

    public static final String  USREVENTS_EVENT_TYPE = "SYSTEMGENERATED";
	public static final String  USREVENTS_EVENT_NAME = "Property Tax";
	public static final String  USREVENTS_EVENT_POSTEDBY = "SYSTEM-PT";



	// Variable names for diff
	public static final String MDMS_WC_ROLE_MODLENAME = "common-masters";
	
	public static final String MDMS_WC_ROLE_MASTERNAME = "thirdparty";
	public static final String MODULE_NAME = "pb";
	public static final String MDMS_RESPONSE_KEY = "MdmsRes";
	public static final String CATEGORY_KEY = "category";
	public static final String ROLE_CODE_KEY = "rolecode";

    public static final String VARIABLE_ACTION = "action";

    public static final String VARIABLE_WFDOCUMENTS = "wfDocuments";

    public static final String VARIABLE_ACTIVE = "active";

    public static final String VARIABLE_USERACTIVE = "status";

    public static final String VARIABLE_CREATEDBY = "createdBy";

    public static final String VARIABLE_LASTMODIFIEDBY = "lastModifiedBy";

    public static final String VARIABLE_CREATEDTIME = "createdTime";

    public static final String VARIABLE_LASTMODIFIEDTIME = "lastModifiedTime";

    public static final String VARIABLE_OWNER = "ownerInfo";


    public static final List<String> FIELDS_TO_IGNORE = Collections.unmodifiableList(Arrays.asList(VARIABLE_ACTION,VARIABLE_WFDOCUMENTS,
            VARIABLE_CREATEDBY,VARIABLE_LASTMODIFIEDBY,VARIABLE_CREATEDTIME,VARIABLE_LASTMODIFIEDTIME));

    public static final List<String> FIELDS_FOR_OWNER_MUTATION = Collections.unmodifiableList(Arrays.asList("name","gender","fatherOrHusbandName"));

    public static final List<String> FIELDS_FOR_PROPERTY_MUTATION = Collections.unmodifiableList(Arrays.asList("propertyType","usageCategory","ownershipCategory","noOfFloors","landArea"));

    public static final String CITIZEN_SENDBACK_ACTION = "SENDBACKTOCITIZEN";
    
    public static final String WORKFLOW_START_ACTION = "INITIATE";

    public static final String ASMT_WORKFLOW_CODE = "ASMT";

    public static final String ASMT_MODULENAME = "PT";

	public static final String CREATE_PROCESS_CONSTANT = "CREATE";
	
	public static final String UPDATE_PROCESS_CONSTANT = "UPDATE";
	
	public static final String MUTATION_PROCESS_CONSTANT = "MUTATION";
	
	public static final String ALTERNATE_PROCESS_CONSTANT = "ALTERNATE";
	
	public static final String PREVIOUS_PROPERTY_PREVIOUD_UUID = "previousPropertyUuid";
	
	
	/* notification constants */
	
	public static final String WF_STATUS_PAID = "PAID";
	
	public static final String WF_STATUS_PAYMENT_PENDING = "PAYMENT_PENDING";
	
	public static final String WF_STATUS_REJECTED = "REJECTED";
	
	public static final String WF_STATUS_FIELDVERIFIED = "FIELDVERIFIED";
	
	public static final String WF_STATUS_DOCVERIFIED = "DOCVERIFIED";
	
	public static final String WF_STATUS_CANCELLED = "CANCELLED";
	
	public static final String WF_STATUS_APPROVED = "APPROVE";
	
	public static final String WF_STATUS_OPEN = "OPEN";
	
	public static final String WF_NO_WORKFLOW = "NO_WORKFLOW";

    public static final String ACTION_UPDATE_MOBILE = "UPDATE_MOBILE";

    public static final String ACTION_ALTERNATE_MOBILE = "ALTERNATE_MOBILE";

    public static final String ACTION_FOR_DUES = "DUE";

    public static final String ACTION_FOR_ASSESSMENT =  "ASSESS";

    public static final String ACTION_FOR_PAYMENT_FAILURE = "FAILURE";

    public static final String WF_STATUS_OPEN_LOCALE = "PT_NOTIF_WF_STATE_LOCALE_OPEN";
	
    public static final String WF_STATUS_DOCVERIFIED_LOCALE = "PT_NOTIF_WF_STATE_LOCALE_DOCVERIFIED";
    
    public static final String WF_STATUS_FIELDVERIFIED_LOCALE = "PT_NOTIF_WF_STATE_LOCALE_FIELDVERIFIED";
    
    public static final String WF_STATUS_APPROVED_LOCALE = "PT_NOTIF_WF_STATE_LOCALE_APPROVED";
    
    public static final String WF_STATUS_REJECTED_LOCALE = "PT_NOTIF_WF_STATE_LOCALE_REJECTED";
    
    public static final String WF_STATUS_PAID_LOCALE = "PT_NOTIF_WF_STATE_LOCALE_PAID";

	public static final String NOTIFICATION_MODULENAME = "rl-services";
	
	/* PT notif loc codes */
	
	public static final String WF_MT_STATUS_OPEN_CODE = "PT_NOTIF_WF_MT_OPEN";
	
	public static final String WF_MT_STATUS_CHANGE_CODE =  "PT_NOTIF_WF_MT_STATE_CHANGE";
	
	public static final String WF_MT_STATUS_PAYMENT_PENDING_CODE = "PT_NOTIF_WF_MT_PAYMENT_PENDING";
	
	public static final String WF_MT_STATUS_PAID_CODE =   "PT_NOTIF_WF_MT_PAID";
	
	public static final String WF_MT_STATUS_APPROVED_CODE =  "PT_NOTIF_WF_MT_APPROVED";
	
	public static final String MT_NO_WORKFLOW = "PT_NOTIF_WF_MT_NONE";
	
	public static final String PT_UPDATE_OWNER_NUMBER = "PT_UPDATE_OWNER_NUMBER";
	
	public static final String PT_UPDATE_ALTERNATE_NUMBER = "PT_UPDATE_ALTERNATE_NUMBER";
	
	/* update */
	public static final String WF_UPDATE_STATUS_OPEN_CODE  = "PT_NOTIF_WF_OPEN";
	
	public static final String WF_UPDATE_STATUS_CHANGE_CODE = "PT_NOTIF_WF_STATUS_CHANGE";
	
	public static final String WF_UPDATE_STATUS_APPROVED_CODE =   "PT_NOTIF_WF_APPROVED";
	
	public static final String UPDATE_NO_WORKFLOW = "PT_NOTIF_WF_UPDATE_NONE";
	
	public static final String CREATE_NOTIF_CODE = "PT_NOTIF_CREATE";
	
	
	/* ASSESSMENT CONSTANTS */
	
    public static final String WORKFLOW_SENDBACK_CITIZEN = "SENDBACKTOCITIZEN";

    public static final String ASSESSMENT_BUSINESSSERVICE = "ASMT";

	public static final String BILL_AMOUNT_PATH = "/Bill/0/totalAmount";

	public static final String BILL_NO_DEMAND_ERROR_CODE = "EG_BS_BILL_NO_DEMANDS_FOUND";
	
	public static final String BILL_NO_PAYABLE_DEMAND_ERROR_CODE = "EG_BS_BILL_ZERO_TAX";




	
	//  NOTIFICATION PLACEHOLDER

    public static final String NOTIFICATION_OWNERNAME = "{OWNER_NAME}";

    public static final String NOTIFICATION_EMAIL = "{EMAIL_ID}";

    public static final String NOTIFICATION_STATUS = "{STATUS}";
    
    public static final String NOTIFICATION_UPDATED_CREATED_REPLACE = "{updated/created}";
    
    public static final String CREATE_STRING = "Create";
    
    public static final String UPDATE_STRING = "Update";
    
    public static final String CREATED_STRING = "Created";
    
    public static final String UPDATED_STRING = "Updated";

    public static final String MUTATED_STRING = "MUTATED";

    public static final String PAYMENT_STRING = "PAYMENT";


    public static final String PT_BUSINESSSERVICE = "PT";

    public static final String MUTATION_BUSINESSSERVICE = "PT.MUTATION";


    public static final String NOTIFICATION_APPLICATIONNUMBER = "{applicationNumber}";
    
    public static final String PT_OWNER_NAME = "{ownername}";
    
    public static final String PT_ALTERNATE_NUMBER = "{alternatenumber}";
    
    public static final String PT_OLD_MOBILENUMBER = "{oldmobilenumber}";
    
    public static final String PT_NEW_MOBILENUMBER = "{newmobilenumber}";
    
    // PROPERTY & MUTATION
    public static final String NOTIFICATION_APPID =  "{APPID}";
    
    public static final String NOTIFICATION_CONSUMERCODE =  "{CONSUMERCODE}";
    
    public static final String NOTIFICATION_TENANTID =  "{TENANTID}";

    public static final String NOTIFICATION_BUSINESSSERVICE =  "{BUSINESSSERVICE}";
    
    public static final String NOTIFICATION_PAY_LINK =  "{PAYLINK}";
    
    public static final String NOTIFICATION_PROPERTY_LINK =  "{PTURL}";
    
    public static final String NOTIFICATION_MUTATION_LINK =  "{MTURL}";
    
    public static final String NOTIFICATION_AMOUNT    =  "{AMOUNT}";
    
    // ASSESSMENT
    
    public static final String NOTIFICATION_ASSESSMENTNUMBER = "{ASSESSMENTNUMBER}";

    public static final String NOTIFICATION_FINANCIALYEAR = "{FINANCIALYEAR}";

    public static final String NOTIFICATION_ASSESSMENT_CREATE = "ASMT_CREATE";

    public static final String NOTIFICATION_ASSESSMENT_UPDATE = "ASMT_UPDATE";

    public static final String LOCALIZATION_ASMT_PREFIX = "ASMT_";

    public static final String NOTIFICATION_ASMT_PREFIX = "ASMT_MSG_";

    public static final String NOTIFICATION_PAYMENT_LINK = "{PAYMENT_LINK}";


    public static final String ONLINE_PAYMENT_MODE = "ONLINE";

    public static final String URL_PARAMS_SEPARATER = "?";

    public static final String TENANT_ID_FIELD_FOR_SEARCH_URL = "tenantId=";

    public static final String LIMIT_FIELD_FOR_SEARCH_URL = "limit=";

    public static final String OFFSET_FIELD_FOR_SEARCH_URL = "offset=";

    public static final String SEPARATER = "&";



    public static final String ADHOC_PENALTY = "adhocPenalty";

    public static final String ADHOC_PENALTY_REASON = "adhocPenaltyReason";

    public static final String ADHOC_REBATE = "adhocExemption";

    public static final String ADHOC_REBATE_REASON = "adhocExemptionReason";

    // PDF CONSUMER

    public static final String KEY_ID = "id";

    public static final String KEY_FILESTOREID = "filestoreid";

    public static final String KEY_PDF_JOBS = "jobs";

    public static final String KEY_PDF_ENTITY_ID = "entityid";

    public static final String KEY_PDF_TENANT_ID = "tenantId";

    public static final String KEY_PDF_MODULE_NAME = "moduleName";

    public static final String KEY_PDF_FILESTOREID = "filestoreids";

    public static final String KEY_PDF_DOCUMENTTYPE = "documentType";

    public static final String PT_CORRECTION_PENDING = "CORRECTIONPENDING";

    public static final String ASMT_USER_EVENT_PAY = "pay";

    public static final String VIEW_APPLICATION_CODE = "View Application";

    public static final String VIEW_PROPERTY_CODE = "VIEW PROPERTY";

    public static final String TRACK_APPLICATION_CODE = "TRACK APPLICATION";

    public static final String DOWNLOAD_MUTATION_CERTIFICATE_CODE = "DOWNLOAD MUTATION CERTIFICATE";

    public static final String DOWNLOAD_MUTATION_RECEIPT_CODE = "DOWNLOAD RECEIPT";

    public static final String PAY_PENDING_PAYMENT_CODE = "DOWNLOAD RECEIPT";

    public static final String VIEW_PROPERTY = "view";

    public static final String TRACK_APPLICATION = "track";


    // Fuzzy Search
    public static final String ES_DATA_PATH = "$..Data";

    public static final String ES_DATA_TENANTID_PATH = "$.tenantData.code";

    public static final String ES_DATA_PROPERTYID_PATH = "$.propertyId";

    //Notification Enhancement
    public static final String CHANNEL_NAME_SMS = "SMS";

    public static final String CHANNEL_NAME_EVENT = "EVENT";

    public static final String CHANNEL_NAME_EMAIL = "EMAIL";

    public static final String MODULE = "module";

    public static final String ACTION = "action";

    public static final String CHANNEL_LIST = "channelList";

    public static final String CHANNEL = "Channel";

    public static final String LOCALIZATION_CODES_JSONPATH = "$.messages.*.code";
  
    public static final String LOCALIZATION_MSGS_JSONPATH = "$.messages.*.message";

  //EVENT PAY
    public static final String EVENT_PAY_TENANTID = "$tenantId";

    public static final String EVENT_PAY_BUSINESSSERVICE = "$businessService";

    public static final String EVENT_PAY_PROPERTYID = "$propertyId";

    //Notification Strings for In App

    public static final String TRACK_APPLICATION_STRING = "You can track your application on the link given below - {PTURL} Thank you";

    public static final String VIEW_PROPERTY_STRING = "You can view your property on the link given below - {PTURL} Thank you";

    public static final String PAY_ONLINE_STRING = "Click on the URL to view the details and pay online {PAYMENT_LINK}";

    public static final String PT_ONLINE_STRING = "You can pay your Property Tax online here - {PAYMENT_LINK}";

    public static final String MT_TRACK_APPLICATION_STRING ="You can track your application on the link given below - {MTURL} Thank you";

    public static final String MT_PAYLINK_STRING = "You can pay your mutation fee on the below link - {PAYLINK} or visit your ULB to pay your dues. Thank you";

    public static final String MT_CERTIFICATE_STRING = "You can download your mutation certificate on the below link - {MTURL} Thank you";

    public static final String MT_RECEIPT_STRING = "You can download your receipt on the below link - {MTURL} Thank you";

    public static final String PT_TAX_FAIL = "Please try again. Ignore this message if you have completed your payment. You can pay your Property Tax online here - {payLink}";

    public static final String PT_TAX_FULL = "Click on the link to download payment receipt {receipt download link}";

    public static final String PT_TAX_PARTIAL = "You can pay your Property Tax online here - {payLink} Click on the link to download payment receipt {receipt download link}";

    public static final String TENANT_MASTER_MODULE = "tenant";

    public static final String TENANTS_MASTER_ROOT = "tenants";

    public static final String TENANTS_JSONPATH_ROOT = "$.MdmsRes.tenant.tenants";

    public static final String PROPERTY_MODEL = "Property";

    public static final String PROPERTY_DECRYPT_MODEL = "PropertyDecrypDisabled";

    //Citizen Feedback Notifications

    public static final String FEEDBACK_URL = "{FeedbackURL}";

    public static final String PT_NOTIF_CF_CREATED = "PT_NOTIF_CF_CREATED";

    public static final String PT_NOTIF_CF_UPDATED = "PT_NOTIF_CF_UPDATED";

    public static final String PT_NOTIF_CF_MUTATED = "PT_NOTIF_CF_MUTATED";

    public static final String PT_NOTIF_CF_PAYMENT_ONLINE = "PT_NOTIF_CF_PAYMENT_ONLINE";

    public static final String CF_REDIRECT_REPLACE_CREATE = "pt/property/new-application/acknowledgement";
    public static final String CF_REDIRECT_REPLACE_UPDATE = "pt/property/edit-application/acknowledgement";
    public static final String CF_REDIRECT_REPLACE_MUTATE = "pt/property/property-mutation/acknowledgement";
    public static final String CF_REDIRECT_REPLACE_PAYMENT = "digit-ui/citizen/payment/success";

    public static final String TENANTID_REPLACE = "$tenantId";

    public static final String PROPERTYID_REPLACE = "$propertyId";

    public static final String ACKNOWLEDGEMENT_REPLACE = "$acknowldgementNumber";

    public static final String REDIRECTLINK_REPLACE = "$redirectedFrom";

    public static final String CREATIONREASON_REPLACE = "$creationReason";

    public static final String TRANSACTIONID_REPLACE = "{TRANSACTION_ID}";
    
	public static final String CALCULATION_TYPE = "CalculationType";
	public static final String CONSUMER_CODE_SEARCH_FIELD_NAME = "consumerCode=";
	public static final String DEMAND_STATUS_PARAM = "status=";
	public static final String DEMAND_STATUS_ACTIVE = "ACTIVE";
	public static final String PAYMENT_COMPLETED = "isPaymentCompleted=false";
	public static final String DEMAND_START_DATE_PARAM = "periodFrom=";
	public static final String DEMAND_END_DATE_PARAM = "periodTo=";
	public static final String EMPTY_DEMAND_ERROR_CODE = "EMPTY_DEMANDS";
	public static final String EMPTY_DEMAND_ERROR_MESSAGE = "No demand found for the given bill generate criteria";
    // BillingService Master

    // RL Service Master
    public static final String RL_SERVICES_MASTER_MODULE = "rl-services-masters";
    public static final String PENALTY_MASTER = "Penalty";
    public static final String BILLING_PERIOD_MASTER = "billingPeriod";
    public static final String PENALTY_TAXHEAD_CODE = "RL_PENALTY_FEE";
    public static final String BUSINESSSERVICE_FIELD_FOR_SEARCH_URL = "businessService=";
    public static final String TIME_INTEREST = "TIME_INTEREST";
    public static final String MDMS_TENANT_MODULE_NAME = "tenant";
    public static final String MDMS_TENANT_MASTER_NAME = "tenants";

    // JSONPath
    public static final String JSONPATH_TENANT_CODES = "$.MdmsRes.tenant.tenants[*].code";

}

