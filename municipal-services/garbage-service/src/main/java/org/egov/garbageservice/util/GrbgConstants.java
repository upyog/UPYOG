package org.egov.garbageservice.util;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * Central configuration and constants for garbage-service (status codes, workflow actions, service URLs).
 * Injects host endpoints and feature flags from application properties; also holds static MDMS and business keys.
 */
@Data
@Component
public class GrbgConstants {
    public static final String MDMS_MODULE_NAME_FEE_STRUCTURE = "Garbage";

    public static final String MDMS_MASTER_NAME_FEE_STRUCTURE = "CalculationType";

    public static final String STATE_LEVEL_TENANT_ID = "hp";

    public static final String APPLICATION_PREFIX = "GC/HP/DISTRICT/ULBNAME/MMYYYY/XXXXXX";

    public static final String DOCUMENT_ACCOUNT = "ACCOUNT";

    // Workflow Statuses
    public static final String STATUS_INITIATED = "INITIATED"; // Initial status before workflow start
    public static final String STATUS_PENDING_FOR_VERIFICATION = "PENDING_FOR_VERIFICATION";
    public static final String STATUS_PENDING_FOR_APPROVAL = "PENDING_FOR_APPROVAL";
    public static final String STATUS_EDIT_APPLICATION = "EDIT_APPLICATION";
    public static final String STATUS_APPROVED = "APPROVED";
    public static final String STATUS_REJECTED = "REJECTED";

    public static final String STATUS_PENDINGFORMODIFICATION = "PENDINGFORMODIFICATION";
    public static final String STATUS_PENDINGFORVERIFICATION = "PENDINGFORVERIFICATION";
    public static final String STATUS_PENDINGFORPAYMENT = "PENDINGFORPAYMENT";
    public static final String STATUS_PENDINGFORAPPROVAL = "PENDINGFORAPPROVAL";
    public static final String STATUS_CLOSED = "CLOSED";
    public static final String STATUS_TEMPERORYCLOSED = "TEMPERORYCLOSED";
    public static final String STATUS_CANCELLED = "CANCELLED";

    // Workflow Actions
    public static final String WORKFLOW_ACTION_APPLY = "APPLY";
    public static final String WORKFLOW_ACTION_VERIFY = "VERIFY";
    public static final String WORKFLOW_ACTION_REJECT = "REJECT";
    public static final String WORKFLOW_ACTION_APPROVE = "APPROVE";
    public static final String WORKFLOW_ACTION_RAISE_QUERY_TO_CITIZEN = "RAISE_QUERY_TO_CITIZEN";
    public static final String WORKFLOW_ACTION_SEND_BACK_TO_VERIFIER = "SEND_BACK_TO_VERIFIER";
    public static final String WORKFLOW_ACTION_EDIT = "EDIT";

    public static final String WORKFLOW_ACTION_INITIATE = "APPLY";
    public static final String WORKFLOW_ACTION_FORWARD_TO_VERIFIER = "FORWARD_TO_VERIFIER";
    public static final String WORKFLOW_ACTION_FORWARD_TO_APPROVER = "FORWARD_TO_APPROVER";
    public static final String WORKFLOW_ACTION_RETURN_TO_INITIATOR = "RETURN_TO_INITIATOR";
    public static final String WORKFLOW_ACTION_CLOSE = "CLOSE";
    public static final String WORKFLOW_ACTION_TEMPERORY_CLOSED = "TEMPERORY_CLOSED";
    public static final String WORKFLOW_ACTION_RETURN_TO_VERIFIER = "RETURN_TO_VERIFIER";
    public static final String WORKFLOW_ACTION_RETURN_TO_INITIATOR_FOR_PAYMENT = "RETURN_TO_INITIATOR_FOR_PAYMENT";
    public static final String WORKFLOW_ACTION_PENDING_FOR_MODIFICATION = "PENDING_FOR_MODIFICATION";

    public static final String BUSINESS_SERVICE_GB_CITIZEN = "garbage-service";
    public static final String BUSINESS_SERVICE_GB_EMPLOYEE = "garbage-service";

    public static final String WORKFLOW_MODULE_NAME = "GC";

    public static final String USER_TYPE_CITIZEN = "CITIZEN";

    public static final String USER_TYPE_EMPLOYEE = "EMPLOYEE";

    public static final String USER_TYPE_SYSTEM = "SYSTEM";

    public static final String CHANNEL_TYPE_CREATE = "CREATE";

    public static final String CHANNEL_TYPE_MIGRATE = "MIGRATION";

    public static final String BILLING_TAX_HEAD_MASTER_CODE = "LCF.Garbage_Collection_Fee";

    // Workflow Roles
    public static final String USER_ROLE_GC_CEMP = "GC_CEMP";
    public static final String USER_ROLE_GC_VERIFIER = "GC_VERIFIER";
    public static final String USER_ROLE_GC_APPROVER = "GC_APPROVER";

    // Obsolete Roles
    public static final String USER_ROLE_GB_VERIFIER = "GC_VERIFIER";
    public static final String USER_ROLE_GB_APPROVER = "GC_APPROVER";
    public static final String USER_ROLE_SUPERVISOR = "SUPERVISOR";
    public static final String USER_ROLE_SECRETARY = "SECRETARY";

    public static final String GARBAGE_MODEL = "Garbage";

    public static final String GARBAGE_PENALTY_TAX_HEAD = "GARBAGE_PENALTY";


    // Alfresco keys
    public static final Long ALFRESCO_COMMON_DOCUMENT_ID = 0L;
    public static final String ALFRESCO_COMMON_CERTIFICATE_DESCRIPTION = "GB certificate";
    public static final String ALFRESCO_COMMON_CERTIFICATE_ID = "";
    public static final String ALFRESCO_COMMON_CERTIFICATE_TYPE = "PDF";
    public static final String ALFRESCO_DOCUMENT_TYPE = "CERT";
    public static final String ALFRESCO_TL_CERTIFICATE_COMMENT = "Signed Certificate";

}