package org.egov.ndc.calculator.utils;

import org.springframework.stereotype.Component;

@Component
public class NDCConstants {

	public static final String SEARCH_MODULE = "rainmaker-ndcsrv";
	public static final String TENANT_ID_FIELD_FOR_SEARCH_URL = "tenantId=";
	public static final String URL_PARAMS_SEPARATER = "?";
	public static final String NDC_MODULE = "NDC";
	public static final String NDC_FEE_MODULE = "NdcFee";
	public static final String NDC_BUSINESS_SERVICE = "ndc-services";
	public static final String SEPARATER = "&";
	public static final String NDC_TYPE = "NdcType";
	public static final String CONSUMER_CODE_SEARCH_FIELD_NAME = "consumerCode=";
	public static final String DEMAND_STATUS_PARAM = "status=";
	public static final String DEMAND_STATUS_ACTIVE = "ACTIVE";
	public static final String paymentcompleted = "isPaymentCompleted=false";
	public static final String DEMAND_START_DATE_PARAM = "periodFrom=";
	public static final String DEMAND_END_DATE_PARAM = "periodTo=";
	public static final String EMPTY_DEMAND_ERROR_CODE = "EMPTY_DEMANDS";
	public static final String EMPTY_DEMAND_ERROR_MESSAGE = "No demands found for the given bill generate criteria";


	// mdms path codes

    public static final String NDC_JSONPATH_CODE = "$.MdmsRes.NDC";

    // error constants

	public static final String INVALID_TENANT_ID_MDMS_KEY = "INVALID TENANTID";

	public static final String INVALID_TENANT_ID_MDMS_MSG = "No data found for this tenentID";

	public static final String APPROVED_STATE = "APPROVED";	
	
	public static final String AUTOAPPROVED_STATE = "AUTO_APPROVED";	
	
	public static final String ACTION_APPROVE = "APPROVE";	
	
	public static final String ACTION_AUTO_APPROVE="AUTO_APPROVE";
	
	public static final String MODE = "mode";	
	
	public static final String ONLINE_MODE = "online";	
	
	public static final String OFFLINE_MODE = "offline";	
	
	public static final String ONLINE_WF = "onlineWF";	

	public static final String OFFLINE_WF = "offlineWF";
	
	public static final String ACTION_REJECT = "REJECT";	
	
	public static final String WORKFLOWCODE = "workflowCode";	
	
    public static final String NDCTYPE_JSONPATH_CODE = "$.MdmsRes.NDC.NdcType";
    
    public static final String NDC_DOC_TYPE_MAPPING = "DocumentTypeMapping";
    
	public static final String DOCUMENT_TYPE = "DocumentType";
	
	public static final String COMMON_MASTERS_MODULE = "common-masters";
	    
	public static final String COMMON_MASTER_JSONPATH_CODE = "$.MdmsRes.common-masters";
	
    public static final String CREATED_STATUS = "CREATED";	
    
	public static final String ACTION_VOID = "VOID";	
	
	public static final String VOIDED_STATUS = "VOIDED";	
	
	public static final String ACTION_INITIATE = "INITIATE";	

	public static final String INITIATED_TIME = "SubmittedOn";	
	
	//sms notification

	public static final String ACTION_STATUS_CREATED = "null_CREATED";
	
	public static final String ACTION_STATUS_INITIATED = "INITIATE_INPROGRESS";
	
	public static final String ACTION_STATUS_REJECTED = "REJECT_REJECTED";
	
	public static final String ACTION_STATUS_APPROVED = "APPROVE_APPROVED";
	
	public static final String FIRE_NDC_TYPE = "FIRE_NDC";
	
	public static final String AIRPORT_NDC_TYPE = "AIRPORT_AUTHORITY";

	public static final String PARSING_ERROR = "PARSING_ERROR";

    public static final String PROPERTY_BUSINESS_SERVICE_CODE = "PROPERTY_BUSINESSSERVICE";
	public static final String WATER_TAX_SERVICE_CODE = "WS";
	public static final String SEWERAGE_TAX_SERVICE_CODE = "SW";
    public static final String PROPERTY_BUSINESSSERVICE = "PT";
    public static final String ADDITIONAL_DETAILS_FEE_TYPE_PARAM = "propertyType";
	public static final String RESIDENTIAL = "RESIDENTIAL";
	public static final String COMMERCIAL = "COMMERCIAL";
}
