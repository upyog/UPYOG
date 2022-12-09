package org.bel.birthdeath.crdeath.util;
/**
     * Creates CrDeathService
     * Rakhi S IKM
     * 
     */
public class CrDeathConstants {
    

    public static final String TENANTS = "tenants";
    public static final String TENANT_MODULE_NAME = "tenant";    

    public static final String GENDERTYPE = "GenderType";
    public static final String GENDER_MODULE_NAME = "common-masters";

    public static final String BND_MODULE_NAME = "birth-death-service";
    public static final String HOSPITAL_LIST = "hospitalList";
    public static final String DEATH_PLACE = "PlaceMaster";    
    //Rakhi S on 07.12.2022
    public static final String DEATH_CAUSE_MAIN = "DeathCause";
    public static final String DEATH_CAUSE_SUB = "DeathCauseSub";
    public static final String MALE_DEPENDENT_TYPE = "MaleDependentType";
    public static final String FEMALE_DEPENDENT_TYPE = "FemaleDependentType";   
    public static final String AGE_UNIT = "AgeUnit"; 
    public static final String MEDICAL_ATTENTION_TYPE = "MedicalAttentionType"; 
    public static final String PROFESSION = "Profession";

    //mdms path codes
    public static final String COMMON_MASTER_JSONPATH = "$.MdmsRes.common-masters";
    public static final String TENANT_JSONPATH = "$.MdmsRes.tenant";
    public static final String BND_LIST_JSONPATH = "$.MdmsRes.birth-death-service";

    //error constants
    public static final String INVALID_TENANT_ID_MDMS_KEY = "INVALID TENANTID";
    public static final String INVALID_TENANT_ID_MDMS_MSG = "No data found for this tenantID";



}
