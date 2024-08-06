package digit.constants;

import org.springframework.stereotype.Component;

@Component
public class Constants {

    public static final String VALUE = "value";

    public static final String VALUE_JSON_PATH = "$.value";

    // Attribute Keys

    public static final String CONSUMER_CODE = "consumerCode";

    public static final String RATING = "rating";

    public static final String REFERENCE_ID = "referenceId";

    public static final String COMMENTS = "comments";

    public static final String CHANNEL = "channel";

    public static final String NEW_SURVEY = "New Survey";
    public static final String WEBAPP = "WEBAPP";
    public static final String SYSTEMGENERATED = "SYSTEMGENERATED";
    public static final String APPLICATION_NUMBER_PLACEHOLDER = "{APPNUMBER}";
    public static final String TENANTID_PLACEHOLDER = "{TENANTID}";
    public static final String LOCALIZATION_MODULE = "rainmaker-common";
    public static final String LOCALIZATION_CODE = "SS_SURVEY_NOTIFICATION_TEMPLATE";
    public static final String SURVEY_TITLE = "{survey_title}";
    public static final String LOCALIZATION_CODES_JSONPATH = "$.messages.*.code";
    public static final String LOCALIZATION_MSGS_JSONPATH = "$.messages.*.message";
}
