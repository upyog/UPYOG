package org.ksmart.birth.utils;

public class BirthConstants {

    public static final String CR_MDMS_TENANT = "kl";
    public static final String  CR_MDMS_MODULE = "birth-death-service";
    public static final String  COMMON_MDMS_MODULE = "common-masters";
    public static final String  LOCATION_MDMS_MODULE = "egov-location";
    public static final String  TENANTS_MODULE = "tenant";
    public static final String CR_MDMS_BIRTH_JSONPATH = "$.MdmsRes.birth-death-service";
    public static final String MDMS_COMMON_JSONPATH = "$.MdmsRes.common-masters";
    public static final String MDMS_TENANT_JSONPATH = "$.MdmsRes.tenant";
    public static final String CR_MDMS_TENANTS = "tenants";
    public static final String CR_MDMS_QUALIFICATION = "Qualification";
    public static final String CR_MDMS_PROFESSION = "Profession";
    public static final String COMMON_MDMS_RELIGION = "Religion";
    public static final String COMMON_MDMS_TALUK = "Taluk";
    public static final String COMMON_MDMS_STATE = "State";
    public static final String COMMON_MDMS_COUNTRY = "Country";
    public static final String COMMON_MDMS_INSTITUTION= "Institution";
    public static final String LOCATION_MDMS_HOSPITAL = "hospitalList";
    public static final String LOCATION_MDMS_INSTITUTION = "institutionList";
    public static final String LOCATION_MDMS_BOUNDARY = "TenantBoundary";
    public static final String COMMON_MDMS_MEDICAL_ATTENTION_TYPE= "MedicalAttentionType";
    public static final String COMMON_MDMS_VILLAGE = "Village";
    public static final String COMMON_MDMS_DISTRICT = "District";
    public static final String COMMON_MDMS_POSTOFFICE = "PostOffice";
    public static final String COMMON_MDMS_LBTYPE = "LBType";
    public static final String COMMON_MDMS_PLACEMASTER = "PlaceMaster";
    public static final String CR_MDMS_INSTITUTIONTYPE = "InstitutionType";
    public static final String CR_MDMS_DELIVERYMETHOD = "DeliveryMethod";
    public static final String CR_MDMS_TENANTS_CODE_JSONPATH = "$.MdmsRes.tenant.tenants[*].code";
    public static final String CR_MDMS_TENANTS_JSONPATH = "$.MdmsRes.tenant.tenants";
    public static final String CR_MDMS_HOSPITALS_CODE_JSONPATH = "$.MdmsRes.egov-location.hospitalList[*].code";
    public static final String CR_MDMS_HOSPITALS_CODES_JSONPATH = "$.MdmsRes.egov-location.hospitalList";
    public static final String CR_MDMS_INSTITUTIONS_CODE_JSONPATH = "$.MdmsRes.egov-location.institutionList[*].code";
    public static final String CR_MDMS_BOUNDARY_CODE_JSONPATH = "$.MdmsRes.egov-location.TenantBoundary[*].boundary.children[*].children[*].code";
    public static final String CR_MDMS_BOUNDARY_CODES_JSONPATH = "$.MdmsRes.egov-location.TenantBoundary[*].boundary.children[*].children[*]";
    public static final String CR_MDMS_INSTITUTIONS_CODES_JSONPATH = "$.MdmsRes.egov-location.institutionList";
    public static final String CR_MDMS_TENANTS_DIST_CODE_JSONPATH = "$.MdmsRes.tenant.tenants[*].city.distCodeStr";
    public static final String CR_MDMS_PROFESSION_CODE_JSONPATH = "$.MdmsRes.birth-death-service.Profession[*].code";
    public static final String CR_MDMS_QUALIFICATION_CODE_JSONPATH = "$.MdmsRes.birth-death-service.Qualification[*].code";
    public static final String CR_MDMS_RELIGION_CODE_JSONPATH = "$.MdmsRes.common-masters.Religion[*].code";
    public static final String CR_MDMS_TALUK_CODE_JSONPATH = "$.MdmsRes.common-masters.Taluk[*].code";
    public static final String CR_MDMS_TALUK_JSONPATH = "$.MdmsRes.common-masters.Taluk";
    public static final String CR_MDMS_STATE_CODE_JSONPATH = "$.MdmsRes.common-masters.State[*].code";
    public static final String CR_MDMS_STATE_JSONPATH = "$.MdmsRes.common-masters.State";
    public static final String CR_MDMS_COUNTRY_CODE_JSONPATH = "$.MdmsRes.common-masters.Country[*].code";
    public static final String CR_MDMS_COUNTRY_JSONPATH = "$.MdmsRes.common-masters.Country";
    public static final String CR_MDMS_INSTITUTION_CODE_JSONPATH = "$.MdmsRes.birth-death-service.Institution[*].code";
    public static final String CR_MDMS_MEDICAL_ATTENTION_TYPE_CODE_JSONPATH = "$.MdmsRes.birth-death-service.MedicalAttentionType[*].code";
    public static final String CR_MDMS_VILLAGE_CODE_JSONPATH = "$.MdmsRes.common-masters.Village[*].code";
    public static final String CR_MDMS_DISTRICT_CODE_JSONPATH = "$.MdmsRes.common-masters.District[*].code";
    public static final String CR_MDMS_DISTRICT_JSONPATH = "$.MdmsRes.common-masters.District";
    public static final String CR_MDMS_POSTOFFICE_CODE_JSONPATH = "$.MdmsRes.common-masters.PostOffice[*].code";
    public static final String CR_MDMS_POSTOFFICE_JSONPATH = "$.MdmsRes.common-masters.PostOffice";
    public static final String CR_MDMS_INTITUTIONTYPE_CODE_JSONPATH = "$.MdmsRes.birth-death-service.InstitutionType[*].code";
    public static final String CR_MDMS_DELIVERYMETHOD_CODE_JSONPATH = "$.MdmsRes.birth-death-service.DeliveryMethod[*].code";
    public static final String CR_MDMS_LBTYPE_JSONPATH = "$.MdmsRes.common-masters.LBType";
    public static final String CR_MDMS_LBTYPE_CODE_JSONPATH = "$.MdmsRes.common-masters.LBType[*].code";
    public static final String CR_MDMS_PLACEMASTER_CODE_JSONPATH = "$.MdmsRes.common-masters.PlaceMaster[*].code";
    public static final String CR_RESIDENCE_PLACE_TYPE_OUTSIDE = "CR_OUTSIDE_INDIA";
    public static final String BIRTH_PLACE_HOSPITAL = "HOSPITAL";
    public static final String BIRTH_PLACE_INSTITUTION = "INSTITUTION";
    public static final String BIRTH_PLACE_HOME= "HOME";
    public static final String BIRTH_PLACE_VEHICLE = "VEHICLE";
    public static final String BIRTH_PLACE_PUBLIC = "PUBLIC_PLACES";
    public static final String BIRTH_PLACE_OTHERS_COUNTRY = "OTHERS_COUNTRY";


    public static final String CR_MDMS_TENANT_BOUNDARY_CODE = "TenantBoundary";
    public static final String CR_MDMS_EGOV_LOCATION_MASTERS_CODE = "egov-location";

    //error constants
    public static final String INVALID_TENANT_ID_MDMS_KEY = "INVALID TENANTID";
    public static final String INVALID_TENANT_ID_MDMS_MSG = "No data found for this tenantID";

    public static final String LB_TYPE_CORPORATION = "LB_TYPE_CORPORATION";

    public static final String LB_TYPE_MUNICIPALITY = "LB_TYPE_MUNICIPALITY";
    public static final String LB_TYPE_GP = "LB_TYPE_GRAMA_PANCHAYAT";
    public static final String LB_TYPE_CORPORATION_CAPTION = "C";

    public static final String LB_TYPE_MUNICIPALITY_CAPTION = "M";

    public static final String APP_NUMBER_CAPTION = "AK";

    public static final String FILE_NUMBER_CAPTION = "FM";

    public static final String REGY_NUMBER_CAPTION = "RG";

    public static final String CERT_NUMBER_CAPTION = "CT";

    public static final String FUN_MODULE_NEW = "CRBRNR";

    public static final String STATE_CODE = "KL";

    public static final String COUNTRY_CODE = "COUNTRY_INDIA";

    public static final String STATE_CODE_SMALL = "kl";


}
