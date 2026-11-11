package org.upyog.wms.constants;

/** Validation rule IDs returned as error codes (format of an error key: RULE_ID:fieldPath). */
public final class ErrorCodes {

    private ErrorCodes() {
    }

    public static final String REQUIRED = "WMS_AP_REQUIRED";
    public static final String INVALID_FORMAT = "WMS_AP_INVALID_FORMAT";
    public static final String INVALID_MASTER = "WMS_AP_INVALID_MASTER_VALUE";
    public static final String INVALID_AMOUNT = "WMS_AP_INVALID_AMOUNT";
    public static final String INVALID_DATE = "WMS_AP_INVALID_DATE";
    public static final String COMPONENT_TOTAL_MISMATCH = "WMS_AP_COMPONENT_TOTAL_MISMATCH";
    public static final String COMPONENT_OVER_ALLOCATED = "WMS_AP_COMPONENT_OVER_ALLOCATED";
    public static final String COMPONENT_COUNT_MISMATCH = "WMS_AP_COMPONENT_COUNT_MISMATCH";
    public static final String FUND_TOTAL_MISMATCH = "WMS_AP_FUND_TOTAL_MISMATCH";
    public static final String FUND_SOURCE_CONFLICT = "WMS_AP_FUND_SOURCE_CONFLICT";
    public static final String DUPLICATE = "WMS_AP_DUPLICATE";
    public static final String DOCUMENT_REQUIRED = "WMS_AP_DOCUMENT_REQUIRED";
    public static final String DOCUMENT_LIMIT = "WMS_AP_DOCUMENT_LIMIT_EXCEEDED";
    public static final String DECLARATION_REQUIRED = "WMS_AP_DECLARATION_REQUIRED";
    public static final String INVALID_ACTION = "WMS_AP_INVALID_ACTION";
    public static final String NOT_EDITABLE = "WMS_AP_NOT_EDITABLE";
    public static final String NOT_FOUND = "WMS_AP_NOT_FOUND";
    public static final String UNAUTHORIZED = "WMS_AP_UNAUTHORIZED";
    public static final String MAKER_CHECKER = "WMS_AP_MAKER_CHECKER_VIOLATION";
    public static final String REVISION_NOT_ALLOWED = "WMS_AP_REVISION_NOT_ALLOWED";
    public static final String PROJECT_ALREADY_REGISTERED = "WMS_AP_PROJECT_ALREADY_REGISTERED";
    public static final String INVALID_SEARCH = "WMS_AP_INVALID_SEARCH";
    public static final String CONFIG_MISSING = "WMS_AP_CONFIGURATION_MISSING";
    public static final String EXTERNAL_SERVICE = "WMS_EXTERNAL_SERVICE_ERROR";
}
