package org.egov.ndc.util;

import org.springframework.stereotype.Component;

@Component
public class NDCConstants {

	public static final String SEARCH_MODULE = "rainmaker-ndc";
	
	public static final String NDC_MODULE = "NDC";
	public static final String NDC_FEE_MODULE = "NdcFee";
	public static final String NDC_BUSINESS_SERVICE = "ndc-services";

	public static final String NDC_TYPE = "NdcType";

    public static final String NDC_JSONPATH_CODE = "$.MdmsRes.NDC";

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
    
	public static final String ACTION_VOID = "VOID";
	
	public static final String ACTION_INITIATE = "INITIATE";	

	public static final String INITIATED_TIME = "SubmittedOn";

	public static final String ACTION_STATUS_CREATED = "null_INITIATED";
	
	public static final String ACTION_STATUS_INITIATED = "INITIATE_INITIATED";
	public static final String ACTION_STATUS_PENDING_PAYMENT = "APPLY_PENDINGPAYMENT";
	public static final String ACTION_STATUS_PAYMENT_CONFIRMATION = "PAY_DOCUMENTVERIFY";
	public static final String ACTION_STATUS_FORWARD_REVIEW = "FORWARD_PT-VERIFICATION";
	public static final String ACTION_STATUS_CITIZEN_ACTION_REQ = "SENDBACKTOCITIZEN_CITIZENACTIONREQUIRED";

	public static final String ACTION_STATUS_REJECTED = "REJECT_REJECTED";
	
	public static final String ACTION_STATUS_APPROVED = "APPROVE_APPROVED";

	public static final String ACTION_PAY = "PAY";
	public static final String PROPERTY_BUSINESSSERVICE = "PT";
	public static final String ADDITIONAL_DETAILS_FEE_TYPE_PARAM = "propertyType";
	public static final String RESIDENTIAL = "RESIDENTIAL";
	public static final String COMMERCIAL = "COMMERCIAL";
	public static final String DEPARTMENT_PMIDC = "PMIDC";
	public static final String HELPLINE_NUMBER = "01828-222050";
	public static final String PORTAL_LINK = "https://mseva.lgpunjab.gov.in/digit-ui/citizen";
	public static final String OFFICE_NAME = "Local Office";
}
