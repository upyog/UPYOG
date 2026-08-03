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

    // Workflow Statuses
    public static final String STATUS_INITIATED = "INITIATED";
    public static final String STATUS_PENDING_FOR_VERIFICATION = "PENDING_FOR_VERIFICATION";
    public static final String STATUS_PENDING_FOR_APPROVAL = "PENDING_FOR_APPROVAL";
    public static final String STATUS_EDIT_APPLICATION = "EDIT_APPLICATION";
    public static final String STATUS_APPROVED = "APPROVED";
    public static final String STATUS_REJECTED = "REJECTED";

    // Workflow Actions
    public static final String WORKFLOW_ACTION_APPLY = "APPLY";
    public static final String WORKFLOW_ACTION_VERIFY = "VERIFY";
    public static final String WORKFLOW_ACTION_REJECT = "REJECT";
    public static final String WORKFLOW_ACTION_APPROVE = "APPROVE";
    public static final String WORKFLOW_ACTION_RAISE_QUERY_TO_CITIZEN = "RAISE_QUERY_TO_CITIZEN";
    public static final String WORKFLOW_ACTION_SEND_BACK_TO_VERIFIER = "SEND_BACK_TO_VERIFIER";
    public static final String WORKFLOW_ACTION_EDIT = "EDIT";

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
    public static final String USER_ROLE_GB_VERIFIER = "GC_VERIFIER";
    public static final String USER_ROLE_GB_APPROVER = "GC_APPROVER";

    public static final String GARBAGE_MODEL = "Garbage";

    public static final String GARBAGE_PENALTY_TAX_HEAD = "GARBAGE_PENALTY";

}