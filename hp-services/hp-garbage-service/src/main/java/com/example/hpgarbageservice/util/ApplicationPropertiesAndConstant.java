package com.example.hpgarbageservice.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class ApplicationPropertiesAndConstant {


	public static final String ACCOUNT_STATUS_INITIATED = "INITIATED";

	public static final String APPLICATION_PREFIX = "GB-";

	public static final String DOCUMENT_ACCOUNT = "ACCOUNT";
	
	public static final String APPLICATION_STATUS_INITIATED = "INITIATED";
    
    public static final String APPLICATION_STATUS_PENDINGFORMODIFICATION = "PENDINGFORMODIFICATION";
    
    public static final String APPLICATION_STATUS_PENDINGFORVERIFICATION = "PENDINGFORVERIFICATION";
    
    public static final String APPLICATION_STATUS_PENDINGFORPAYMENT = "PENDINGFORPAYMENT";
    
    public static final String APPLICATION_STATUS_PENDINGFORAPPROVAL = "PENDINGFORAPPROVAL";

    public static final String APPLICATION_STATUS_APPROVED  = "APPROVED";

    public static final String APPLICATION_STATUS_REJECTED  = "REJECTED";

    public static final String APPLICATION_STATUS_CANCELLED  = "CANCELLED";

    public static final String WORKFLOW_ACTION_INITIATE  = "INITIATE";

    @Value("${workflow.context.path}")
    public String workflowHost;
    
    @Value("${workflow.transition.path}")
    public String workflowEndpointTransition;
    
}
