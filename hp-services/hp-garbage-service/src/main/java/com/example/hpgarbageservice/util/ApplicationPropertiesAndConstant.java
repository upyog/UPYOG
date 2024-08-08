package com.example.hpgarbageservice.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class ApplicationPropertiesAndConstant {


//	public static final String ACCOUNT_STATUS_INITIATED = "INITIATED";

	public static final String APPLICATION_PREFIX = "GB-";

	public static final String DOCUMENT_ACCOUNT = "ACCOUNT";
	
	public static final String STATUS_INITIATED = "INITIATED";
    
    public static final String STATUS_PENDINGFORMODIFICATION = "PENDINGFORMODIFICATION";
    
    public static final String STATUS_PENDINGFORVERIFICATION = "PENDINGFORVERIFICATION";
    
    public static final String STATUS_PENDINGFORPAYMENT = "PENDINGFORPAYMENT";
    
    public static final String STATUS_PENDINGFORAPPROVAL = "PENDINGFORAPPROVAL";

    public static final String STATUS_APPROVED  = "APPROVED";

    public static final String STATUS_REJECTED  = "REJECTED";

    public static final String STATUS_CANCELLED  = "CANCELLED";

    public static final String WORKFLOW_ACTION_INITIATE  = "INITIATE";

    public static final String WORKFLOW_BUSINESS_SERVICE = "GarbageCollection";

    public static final String WORKFLOW_MODULE_NAME  = "GB";
    

    @Value("${workflow.context.path}")
    public String workflowHost;
    
    @Value("${workflow.transition.path}")
    public String workflowEndpointTransition;
    
}
