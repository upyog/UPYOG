package org.egov.noc.calculator.utils;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

@Component
public class NOCConstants {

	public static final String SEARCH_MODULE = "rainmaker-nocsrv";
	public static final String TENANT_ID_FIELD_FOR_SEARCH_URL = "tenantId=";
	public static final String URL_PARAMS_SEPARATER = "?";
	public static final String NOC_MODULE = "NOC";
	public static final String NOC_FEE_MODULE = "NocFee";
	public static final String NOC_BUSINESS_SERVICE = "obpas_noc";
	public static final String SEPARATER = "&";
	public static final String NOC_TYPE = "NOC";
	public static final String CONSUMER_CODE_SEARCH_FIELD_NAME = "consumerCode=";
	public static final String DEMAND_STATUS_PARAM = "status=";
	public static final String DEMAND_STATUS_ACTIVE = "ACTIVE";
	public static final String paymentcompleted = "isPaymentCompleted=false";
	public static final String DEMAND_START_DATE_PARAM = "periodFrom=";
	public static final String DEMAND_END_DATE_PARAM = "periodTo=";
	public static final String EMPTY_DEMAND_ERROR_CODE = "EMPTY_DEMANDS";
	public static final String EMPTY_DEMAND_ERROR_MESSAGE = "No demands found for the given bill generate criteria";


	// mdms path codes

    public static final String NOC_JSONPATH_CODE = "$.MdmsRes.NOC";

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
	
    public static final String NOCTYPE_JSONPATH_CODE = "$.MdmsRes.NOC.NocType";
    
    public static final String NOC_DOC_TYPE_MAPPING = "DocumentTypeMapping";
    
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
	
	public static final String FIRE_NOC_TYPE = "FIRE_NOC";
	
	public static final String AIRPORT_NOC_TYPE = "AIRPORT_AUTHORITY";

	public static final String PARSING_ERROR = "PARSING_ERROR";

    public static final String PROPERTY_BUSINESS_SERVICE_CODE = "PT";
	public static final String WATER_TAX_SERVICE_CODE = "WS";
	public static final String SEWERAGE_TAX_SERVICE_CODE = "SW";
	
	public static final String MDMS_CHARGES_TYPE_PATH  = "$.MdmsRes.noc.ChargesType.*.Charges.*.[?( @.applicable==true )]";
	
	public static final String MDMS_CHARGES_TYPE_CODE  = "101";

	public static final String NOC_PROCESSING_FEES = "NOC_PROCESSING_FEES";

	public static final String NOC_CLU_CHARGES = "NOC_CLU_CHARGES";

	public static final String NOC_EXTERNAL_DEVELOPMENT_CHARGES = "NOC_EXTERNAL_DEVELOPMENT_CHARGES";

	public static final String NOC_URBAN_DEVELOPMENT_CESS = "NOC_URBAN_DEVELOPMENT_CESS";

	public static final String NOC_COMPOUNDING_FEES = "NOC_COMPOUNDING_FEES";
	
	public static final BigDecimal SQYARD_TO_SQFEET = BigDecimal.valueOf(9.0);
	
	public static final BigDecimal SQMETER_TO_SQYARD = BigDecimal.valueOf(1.19599);
	
	public static final String MDMS_CHARGES_TYPE  = "ChargesType";
	
	public static final String MDMS_TAX_PERIOD_FROM_PATH  = "$.MdmsRes.noc.ChargesType.*.startingDate";
	
	public static final String MDMS_TAX_PERIOD_TO_PATH  = "$.MdmsRes.noc.ChargesType.*.endingDate";
	
	public static final String INVALID_UPDATE = "INVALID UPDATE";
	
	public static final String INVALID_APPLICATION_NUMBER = "INVALID APPLICATION NUMBER";

	public static final String MDMS_ROUNDOFF_TAXHEAD = "TL_ROUNDOFF";
		
}
