package org.egov.noc.util;


import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class NOCConstants {

	public static final String SEARCH_MODULE = "rainmaker-nocsrv";
	
	public static final String NOC_MODULE = "NOC";
	
	public static final String NOC_TYPE = "NocType";
	
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
	
	public static final String TENANTID_MDC_STRING = "TENANTID";

	// AAI Integration Constants
	public static final String AAI_INTEGRATION_ERROR = "AAI_INTEGRATION_ERROR";

	public static final String APPLICATION_STATUS_INPROGRESS = "INPROGRESS";

	public static final String APPLICATION_STATUS_APPROVED = "APPROVED";

	public static final String APPLICATION_STATUS_REJECTED = "REJECTED";

	public static final String CIVIL_AVIATION_NOC_TYPE = "CIVIL_AVIATION";

	public static final String CIVIL_NOC_WORKFLOW_CODE = "CIVIL_AVIATION_SRV";

	// AAI Document Type Constants
	public static final String DOC_TYPE_UNDERTAKING1A = "NOC.UNDERTAKING1A";
	public static final String DOC_TYPE_SITEELEVATION = "NOC.SITEELEVATION";
	public static final String DOC_TYPE_SITECORDINATES = "NOC.SITECORDINATES";
	public static final String DOC_TYPE_AUTHORIZATION = "NOC.AUTHORIZATION";
	public static final String DOC_TYPE_PERMISSION = "NOC.PERMISSION";
	public static final String DOC_TYPE_AAI_NOC_APPROVAL = "NOC.AAI_NOC_APPROVAL";

	// AAI Status Constants
	public static final String AAI_STATUS_ISSUED = "ISSUED";
	public static final String AAI_STATUS_AUTOSETTLED = "AUTOSETTLED";
	public static final String AAI_STATUS_APPROVED = "APPROVED";
	public static final String AAI_STATUS_REJECTED = "REJECTED";
	public static final String AAI_STATUS_VERIFICATIONREJECTED = "VERIFICATIONREJECTED";
	public static final String AAI_STATUS_INPROCESS = "INPROCESS";

	// Fire NOC Workflow Constants
	public static final String FIRE_NOC_WORKFLOW_CODE = "FIRE_SAFETY_SRV";
	public static final String FIRE_SAFETY_NOC_TYPE = "FIRE_SAFETY";

	// Coordinate Constants
	// Regex pattern for DMS format: "DD MM SS.SS"
	public static final Pattern DMS_PATTERN = Pattern.compile("^\\d{2}\\s+\\d{2}\\s+\\d{1,2}(\\.\\d{1,2})?$");

	// Unified list of all keys that might contain coordinates
	public static final List<String> ALL_COORDINATE_KEYS = Arrays.asList(
			"EAST", "WEST", "NORTH", "SOUTH", "CENTER"
	);

	// Coordinate field keys
	public static final String KEY_LAT = "latitude";
	public static final String KEY_LON = "longitude";

}
