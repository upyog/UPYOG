package org.bel.birthdeath.crdeath.util;

public class CrDeathConstants {
    

    public static final String TENANTS = "tenants";
    public static final String TENANT_MODULE_NAME = "tenant";    

    public static final String GENDERTYPE = "GenderType";
    public static final String GENDER_MODULE_NAME = "common-masters";

    public static final String HOSPITAL_LIST = "hospitalList";
    public static final String BND_MODULE_NAME = "birth-death-service";


    public static final String DEATH_PLACE = "PlaceMaster";
    // public static final String DEATH_PLACE_MODULE_NAME = "birth-death-service";


    //mdms path codes
    public static final String COMMON_MASTER_JSONPATH = "$.MdmsRes.common-masters";
    public static final String TENANT_JSONPATH = "$.MdmsRes.tenant";
    public static final String HOSPITAL_LIST_JSONPATH = "$.MdmsRes.birth-death-service";

    //error constants
    public static final String INVALID_TENANT_ID_MDMS_KEY = "INVALID TENANTID";
    public static final String INVALID_TENANT_ID_MDMS_MSG = "No data found for this tenantID";



}
