package upyog.config;


import org.springframework.stereotype.Component;


@Component
public class ServiceConstants {

    public static final String EXTERNAL_SERVICE_EXCEPTION = "External Service threw an Exception: ";
    public static final String SEARCHER_SERVICE_EXCEPTION = "Exception while fetching from searcher: ";

    public static final String IDGEN_ERROR = "IDGEN ERROR";
    public static final String NO_IDS_FOUND_ERROR = "No ids returned from idgen Service";

    public static final String ERROR_WHILE_FETCHING_FROM_MDMS = "Exception occurred while fetching category lists from mdms: ";

    public static final String RES_MSG_ID = "uief87324";
    public static final String SUCCESSFUL = "successful";
    public static final String FAILED = "failed";

    public static final String URL = "url";
    public static final String URL_SHORTENING_ERROR_CODE = "URL_SHORTENING_ERROR";
    public static final String URL_SHORTENING_ERROR_MESSAGE = "Unable to shorten url: ";

    public static final String ILLEGAL_ARGUMENT_EXCEPTION_CODE = "IllegalArgumentException";
    public static final String OBJECTMAPPER_UNABLE_TO_CONVERT = "ObjectMapper not able to convertValue in userCall";
    public static final String INVALID_DATE_FORMAT_CODE = "INVALID_DATE_FORMAT";
    public static final String INVALID_DATE_FORMAT_MESSAGE = "Failed to parse date format in user";
    public static final String CITIZEN_UPPER = "CITIZEN";
    public static final String CITIZEN_LOWER = "Citizen";
    public static final String USER = "user";

    public static final String PARSING_ERROR = "PARSING ERROR";
    public static final String FAILED_TO_PARSE_BUSINESS_SERVICE_SEARCH = "Failed to parse response of workflow business service search";
    public static final String BUSINESS_SERVICE_NOT_FOUND = "BUSINESSSERVICE_NOT_FOUND";
    public static final String THE_BUSINESS_SERVICE = "The businessService ";
    public static final String NOT_FOUND = " is not found";
    public static final String TENANTID = "?tenantId=";
    public static final String BUSINESS_SERVICES = "&businessServices=";
    public static final String EST_BOOKING_FEE   = "EST_BOOKING_FEE";
    public static final String EST_PENALTY_FEE   = "EST_PENALTY_FEE";

    // Payment types
    public static final String PAYMENT_TYPE_MONTHLY_RENT = "MONTHLY_RENT";
    public static final String PAYMENT_TYPE_PARTIAL = "PARTIAL";
    public static final String PAYMENT_TYPE_FULL = "FULL";

    // Status
    public static final String STATUS_PENDING  = "PENDING";
    public static final String STATUS_PAID     = "PAID";
    public static final String STATUS_SYSTEM   = "system";
    public static final String STATUS_PENDING_FOR_PAYMENT = "PENDING_FOR_PAYMENT";
    public static final String STATUS_PENDING_FOR_ALLOTMENT = "PENDING_FOR_ALLOTMENT";
    public static final String STATUS_ALLOTTED = "ALLOTTED";

    // MDMS keys
    public static final String MDMS_RES               = "MdmsRes";
    public static final String MDMS_MODULE_ESTATE      = "Estate";
    public static final String MDMS_MASTER_PENALTY     = "Penalty";
    public static final String MDMS_PENALTY_RATE_KEY   = "rate";

    // Error codes
    public static final String PARSING_ERROR_CODE = "PARSING ERROR";

    public static final class EstateConstants {
        public static final String INVALID_TENANT = "INVALID TENANT";
        public static final String INVALID_REQUEST = "INVALID REQUEST";
    }

}
