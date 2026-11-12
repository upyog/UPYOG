package org.upyog.wms.constants;

/** Constants shared across the service. Master data codes live in MDMS, not here. */
public final class WmsConstants {

    private WmsConstants() {
    }

    // MDMS
    public static final String MDMS_MODULE = "WorksManagement";
    public static final String MASTER_FINANCIAL_YEAR = "FinancialYear";
    public static final String MASTER_PURPOSE = "Purpose";
    public static final String MASTER_TYPE_OF_WORK = "TypeOfWork";
    public static final String MASTER_WORK_CATEGORY = "WorkCategory";
    public static final String MASTER_RECOMMENDED_BY = "RecommendedBy";
    public static final String MASTER_SCHEME = "Scheme";
    public static final String MASTER_GOVERNMENT_GRANT = "GovernmentGrant";
    public static final String MASTER_OWN_SOURCE_REVENUE = "OwnSourceRevenue";
    public static final String MASTER_COMPONENT_CATEGORY = "ComponentCategory";
    public static final String MASTER_DOCUMENT_TYPE = "DocumentType";
    public static final String MASTER_APPROVING_DEPARTMENT = "ApprovingDepartment";
    public static final String MASTER_STATE = "State";
    public static final String MASTER_DISTRICT = "District";
    public static final String MASTER_ACTION_PLAN_CONFIG = "ActionPlanConfig";

    // Special master codes
    public static final String CODE_OTHERS = "OTHERS";
    public static final String RECOMMENDED_BY_MLA = "MLA";
    public static final String RECOMMENDED_BY_MP = "MP";

    // Document owners (MDMS DocumentType.owner)
    public static final String DOC_OWNER_ACTION_PLAN = "ACTION_PLAN";
    public static final String DOC_OWNER_RECOMMENDATION = "RECOMMENDATION";
    public static final String DOC_OWNER_COMPONENT = "COMPONENT";
    public static final String DOC_TYPE_TECHNICAL_SANCTION = "TECHNICAL_SANCTION";

    // Component business status
    public static final String COMPONENT_ACTIVE = "ACTIVE";
    public static final String COMPONENT_INACTIVE = "INACTIVE";

    // Field formats
    public static final String PROJECT_ID_REGEX = "^[A-Za-z0-9]{1,12}$";
    public static final String PLAIN_TEXT_REGEX = "^[\\p{L}\\p{N} .,()/&'\\-_:#]+$";
    public static final String ALPHABETIC_REGEX = "^[\\p{L} .'\\-]+$";

    public static final String USER_TYPE_EMPLOYEE = "EMPLOYEE";
}
