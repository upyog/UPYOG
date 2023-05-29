package org.ksmart.birth.utils;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;
public class BirthConstants {

    public static final String CR_MDMS_TENANT = "kl";
    //public static final String  MODULE = "birth-services";
    public static final String  CR_MDMS_MODULE = "birth-death-service";
    public static final String  COMMON_MDMS_MODULE = "common-masters";
    public static final String  LOCATION_MDMS_MODULE = "egov-location";
    public static final String NOTIFICATION_LOCALE = "en_IN";
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
    public static final String COMMON_MDMS_HOSPITAL= "Hospital";
 
    public static final String LOCATION_MDMS_HOSPITAL = "hospitalList";
    public static final String LOCATION_MDMS_INSTITUTION = "institutionList";
    public static final String LOCATION_MDMS_BOUNDARY = "TenantBoundary";
    public static final String COMMON_MDMS_MEDICAL_ATTENTION_TYPE = "AttentionOfDelivery";
    public static final String COMMON_MDMS_VILLAGE = "Village";
    public static final String COMMON_MDMS_DISTRICT = "District";

    public static final String COMMON_MDMS_POSTOFFICE = "PostOffice";
    public static final String COMMON_MDMS_LBTYPE = "LBType";
    public static final String CR_MDMS_PLACEMASTER = "PlaceMaster";
    public static final String CR_MDMS_MARITAL = "MaritalStatus";
    public static final String CR_MDMS_INITIATOR= "Initiator";
    public static final String CR_MDMS_CARETAKER= "CareTakerDesignation";
    public static final String CR_MDMS_RELATION= "Relation";
    public static final String CR_MDMS_IPOP= "IPOPList";
    public static final String CR_MDMS_OTHER_BP= "OtherBithPlace";
    public static final String CR_MDMS_INSTITUTIONTYPE = "InstitutionType";
    public static final String CR_MDMS_PLACE_INSTITUTION_TYPE = "InstitutionTypePlaceOfEvent";
    public static final String CR_MDMS_DELIVERYMETHOD = "DeliveryMethod";
    public static final String CR_MDMS_FOETAL = "FoetalDeath";
    public static final String CR_MDMS_VEHICLETYPEe = "VehicleType";
    public static final String CR_MDMS_WORKFLOW_NEW = "WorkFlowBirth";

    public static final String CR_MDMS_TENANTS_CODE_JSONPATH = "$.MdmsRes.tenant.tenants[*].code";
    public static final String CR_MDMS_TENANTS_JSONPATH = "$.MdmsRes.tenant.tenants";

  //  public static final String CR_MDMS_BIRTH_NEW_WF_CODE_JSONPATH = "$.MdmsRes.birth-death-service.tenants[*].code";
    public static final String CR_MDMS_BIRTH_NEW_WF_JSONPATH = "$.MdmsRes.birth-death-service.WorkFlowBirth";
    public static final String CR_MDMS_BIRTH_NEW_WF_APPTYPE_JSONPATH = "$.MdmsRes.birth-death-service.WorkFlowBirth[*].ApplicationType";
    public static final String CR_MDMS_HOSPITALS_CODE_JSONPATH = "$.MdmsRes.egov-location.hospitalList[*].code";
    public static final String CR_MDMS_HOSPITALS_CODES_JSONPATH = "$.MdmsRes.egov-location.hospitalList";
    public static final String CR_MDMS_BIRTH_PLACES_CODE_JSONPATH = "$.MdmsRes.birth-death-service.PlaceMaster[*].code";
    public static final String CR_MDMS_BIRTH_PLACES_JSONPATH = "$.MdmsRes.birth-death-service.PlaceMaster";
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
    public static final String CR_MDMS_INST_TYPE_CODE_JSONPATH = "$.MdmsRes.birth-death-service.InstitutionTypePlaceOfEvent[*].code";
    public static final String CR_MDMS_VEHI_TYPE_CODE_JSONPATH = "$.MdmsRes.birth-death-service.VehicleType[*].code";
    public static final String CR_MDMS_PUBLIC_PLACE_TYPE_CODE_JSONPATH = "$.MdmsRes.birth-death-service.OtherBithPlace[*].code";
    public static final String CR_MDMS_PUBLIC_PLACE_TYPE_JSONPATH = "$.MdmsRes.birth-death-service.OtherBithPlace";
    public static final String CR_MDMS_PROF_TYPE_JSONPATH = "$.MdmsRes.birth-death-service.Profession";
    public static final String CR_MDMS_QUAL_TYPE_JSONPATH = "$.MdmsRes.birth-death-service.Qualification";
    public static final String CR_MDMS_RELIGION_JSONPATH = "$.MdmsRes.common-masters.Religion";
    public static final String CR_MDMS_PROF_TYPE_CODE_JSONPATH = "$.MdmsRes.birth-death-service.Profession[*].code";
    public static final String CR_MDMS_QUAL_TYPE_CODE_JSONPATH = "$.MdmsRes.birth-death-service.Qualification[*].code";
    public static final String CR_MDMS_RELIGION_TYPE_CODE_JSONPATH = "$.MdmsRes.common-masters.Religion[*].code";
    public static final String CR_MDMS_VEHI_TYPE_JSONPATH = "$.MdmsRes.birth-death-service.VehicleType";
    public static final String CR_MDMS_INST_TYPE_JSONPATH = "$.MdmsRes.birth-death-service.InstitutionTypePlaceOfEvent";
    public static final String CR_MDMS_MEDICAL_ATTENTION_TYPE_CODE_JSONPATH = "$.MdmsRes.birth-death-service.AttentionOfDelivery[*].code";
    public static final String CR_MDMS_MEDICAL_ATTENTION_TYPE_JSONPATH = "$.MdmsRes.birth-death-service.AttentionOfDelivery";

    public static final String CR_MDMS_VILLAGE_CODE_JSONPATH = "$.MdmsRes.common-masters.Village[*].code";
    public static final String CR_MDMS_VILLAGE_JSONPATH = "$.MdmsRes.common-masters.Village";

    public static final String CR_MDMS_DISTRICT_CODE_JSONPATH = "$.MdmsRes.common-masters.District[*].code";
    public static final String CR_MDMS_DISTRICT_JSONPATH = "$.MdmsRes.common-masters.District";

    public static final String CR_MDMS_POSTOFFICE_CODE_JSONPATH = "$.MdmsRes.common-masters.PostOffice[*].code";
    public static final String CR_MDMS_POSTOFFICE_JSONPATH = "$.MdmsRes.common-masters.PostOffice";
    public static final String CR_MDMS_INTITUTIONTYPE_CODE_JSONPATH = "$.MdmsRes.birth-death-service.InstitutionType[*].code";
    public static final String CR_MDMS_DELIVERYMETHOD_CODE_JSONPATH = "$.MdmsRes.birth-death-service.DeliveryMethod[*].code";
    public static final String CR_MDMS_DELIVERY_METHOD_JSONPATH = "$.MdmsRes.birth-death-service.DeliveryMethod";
    public static final String CR_MDMS_MARITAL_STATUS_CODE_JSONPATH = "$.MdmsRes.birth-death-service.MaritalStatus[*].code";
    public static final String CR_MDMS_MARITAL_STATUS_JSONPATH = "$.MdmsRes.birth-death-service.MaritalStatus";
    public static final String CR_MDMS_RELATION_CODE_JSONPATH = "$.MdmsRes.birth-death-service.Relation[*].code";
    public static final String CR_MDMS_RELATION_JSONPATH = "$.MdmsRes.birth-death-service.Relation";
    public static final String CR_MDMS_INITIATOR_CODE_JSONPATH = "$.MdmsRes.birth-death-service.Initiator[*].code";
    public static final String CR_MDMS_INITIATOR_JSONPATH = "$.MdmsRes.birth-death-service.Initiator";
    public static final String CR_MDMS_CT_CODE_JSONPATH = "$.MdmsRes.birth-death-service.CareTakerDesignation[*].code";
    public static final String CR_MDMS_CT_JSONPATH = "$.MdmsRes.birth-death-service.CareTakerDesignation";
    public static final String CR_MDMS_IP_OP_CODE_JSONPATH = "$.MdmsRes.birth-death-service.IPOPList[*].code";
    public static final String CR_MDMS_IP_OP_JSONPATH = "$.MdmsRes.birth-death-service.IPOPList";
    public static final String CR_MDMS_FOETALDEATH_CODE_JSONPATH = "$.MdmsRes.birth-death-service.FoetalDeath";
    public static final String CR_MDMS_FOETALDEATH_JSONPATH = "$.MdmsRes.birth-death-service.FoetalDeath";
    public static final String CR_MDMS_LBTYPE_JSONPATH = "$.MdmsRes.common-masters.LBType";

    public static final String CR_MDMS_LBTYPE_CODE_JSONPATH = "$.MdmsRes.common-masters.LBType[*].code";
    public static final String CR_RESIDENCE_PLACE_TYPE_OUTSIDE = "CR_OUTSIDE_INDIA";
    public static final String BIRTH_PLACE_HOSPITAL = "HOSPITAL";
    public static final String BIRTH_PLACE_INSTITUTION = "INSTITUTION";
    public static final String BIRTH_PLACE_HOME= "HOME";
    public static final String BIRTH_PLACE_VEHICLE = "VEHICLE";
    public static final String BIRTH_PLACE_PUBLIC = "PUBLIC_PLACES";
    public static final String BIRTH_PLACE_OTHERS_COUNTRY = "OUTSIDE_COUNTRY";//OUTSIDE_COUNTRY
    public static final String BIRTH_PLACE_OTHERS_MIGRATION = "OTHERS";//OUTSIDE_COUNTRY

    public static final String CR_MDMS_TENANT_BOUNDARY_CODE = "TenantBoundary";
    public static final String CR_MDMS_EGOV_LOCATION_MASTERS_CODE = "egov-location";
    //error constants
    public static final String INVALID_TENANT_ID_MDMS_KEY = "INVALID TENANTID";
    public static final String INVALID_TENANT_ID_MDMS_MSG = "No data found for this tenantID";
    public static final String LB_TYPE_CORPORATION = "LB_TYPE_CORPORATION";
    public static final String LB_TYPE_MUNICIPALITY = "LB_TYPE_MUNICIPALITY";
    public static final String LB_TYPE_GP = "LB_TYPE_GRAMA_PANCHAYAT";
    public static final String FUN_MODULE_NEW = "CRBRNR";
    public static final String FUN_MODULE_STL = "CRBRSB";
    public static final String FUN_MODULE_OSC= "CRBRBO";
    public static final String FUN_MODULE_NAC= "CRBRNC";
    public static final String FUN_MODULE_COR= "CRBRCN";
    public static final String FUN_MODULE_ADOP= "CRBRAD";
    public static final String FUN_MODULE_ABAN= "CRBRAB";

    public static final String STATE_CODE = "KL";

    public static final String COUNTRY_CODE = "COUNTRY_INDIA";
    public static final String TCS_CODE_1 = "COC";
    public static final String TCS_CODE_2 = "CoC";
    public static final String TCS_ERROR_HOSP = "HOSPITAL_";

    public static final String TCS_ERROR_STR = "എറണാകുളം, കേരളം, ഇ൯ഡ്യ";

    public static final String STATE_CODE_SMALL = "kl";
    ///Application Status
    public static final String STATUS_APPROVED = "APPROVED";
    public static final String STATUS_FOR_PAYMENT = "PENDINGPAYMENT";
    public static final String STATUS_INITIATED = "INITIATED";
    public static final String STATUS_CITIZENACTIONREQUIRED = "CITIZENACTIONREQUIRED";
    //WF Action
    public static final String WF_APPROVE = "APPROVE";
    //Registration Status
    public static final String REG_STATUS_ACTIVE = "ACTIVE";
    //Registration Status
    public static final String REG_STATUS_CANCEL = "CANCEL";
    // Unique numbers
    public static final String APPLICATION_NO = "APPL";
    public static final String FILE_NO = "FILE";
    public static final String REGISTRATION_NO = "REG";
    public static final String CERTIFICATE_NO = "CERT";
    public static final String NACMODULE = "CRN";
     

    public static final String GL_CODE = "glcode";

    public static final String GL_CODE_MASTER = "GLCode";
    //Migration details
    public static final String MIGRATED_FROM_TCS = "TCS";
    public static final String MIGRATED_FROM_SEVANA_CR = "SEVANA";

    // Patterns for constraint validations
    public static final String PATTERN_NAME = "^[^\\\\$\\\"<>?\\\\\\\\~`!@#$%^()+={}\\\\[\\\\]*,:;“”‘’]*$";
    public static final String PATTERN_AADHAAR = "^[1-9][0-9]{11}$";
    public static final String PATTERN_MOBILE = "^[1-9][0-9]{9,14}$";
    public static final String PATTERN_TENANT = "^kl\\.[a-z]+$";
    public static final String PATTERN_PINCODE = "^[1-9][0-9]{5}$";

    // Encryption / Decryption
    public static final String Birth_APPLICANT_ENC_KEY = "BirthDetails";

    public static final String NOT_APPLICABLE = "NOT_APPLICABLE";
    
    public static final String FALSE = "false";

    public static final String RDO_PROC_DOC = "CR_RDO_PROCEEDINGS_DOC";

    public static final String INITIATOR_NAME_KEY = "{INITIATOR_NAME}";
    
    // ACTION_STATUS combinations for notification

    public static final String ACTION_STATUS_INITIATED = "INITIATE_INITIATED";
    
    public static final String ACTION_STATUS_PAYMENT = "INITIATE_PENDINGPAYMENT";

    public static final String ACTION_STATUS_APPLIED = "APPLY_APPLIED";
    
    public static final String ACTION_STATUS_APPROVED = "APPROVE_APPROVED";
    
    public static final String ACTION_STATUS_REJECTED = "REJECT_REJECTED";
    
    public static final String ACTION_STATUS_FIELDINSPECTION = "FORWARD_FIELDINSPECTION";
    
    public static final String ACTION_STATUS_PENDINGAPPROVAL = "FORWARD_PENDINGAPPROVAL";
    
    public static final String ACTION_SENDBACKTOCITIZEN_FIELDINSPECTION = "SENDBACKTOCITIZEN_CITIZENACTIONREQUIRED";
    
    public static final String ACTION_FORWARD_CITIZENACTIONREQUIRED = "FORWARDTOEMPLOYEE_FIELDINSPECTION";
    
    public static final String ACTION_CANCEL_CANCELLED = "CANCEL_CANCELLED";
    
    public static final String ACTION_STATUS_EXPIRED = "EXPIRE_EXPIRED";
    
    public static final String ACTION_STATUS_MANUAL_EXPIRED = "MANUALEXPIRE_MANUALEXPIRED";

    public static final String NOTIFICATION_INITIATED = "tl.en.counter.initiate";

    public static final String NOTIFICATION_APPLIED = "tl.en.counter.submit";
    
    public static final String NOTIFICATION_APPROVED = "tl.en.counter.approved";
    
    public static final String NOTIFICATION_REJECTED = "tl.en.counter.rejected";
    
    public static final String NOTIFICATION_FIELD_INSPECTION = "tl.en.field.inspection";
    
    public static final String NOTIFICATION_PENDING_APPROVAL = "tl.en.pending.approval";
    
    public static final String NOTIFICATION_SENDBACK_CITIZEN = "tl.en.sendback.citizen";
    
    public static final String NOTIFICATION_FORWARD_CITIZEN = "tl.en.forward.citizen";
    
    public static final String NOTIFICATION_CANCELLED = "tl.en.counter.cancelled";
    
    public static final String NOTIFICATION_EXPIRED = "tl.en.counter.expire";
    
    public static final String NOTIFICATION_MANUAL_EXPIRED = "tl.en.counter.manual.expire";
    
    public static final String NOTIF_OWNER_NAME_KEY = "{OWNER_NAME}";
    
    public static final String MODULE = "rainmaker-cr";
//  egov-user  		
    
    
//    public static final List<String> NOTIFICATION_CODES = Collections.unmodifiableList(Arrays.asList(
//            NOTIFICATION_INITIATED,
//            NOTIFICATION_APPLIED,
//            NOTIFICATION_FIELD_INSPECTION,             
//            NOTIFICATION_APPROVED, NOTIFICATION_REJECTED, NOTIFICATION_CANCELLED,
//             
//            NOTIFICATION_SENDBACK_CITIZEN,
//            NOTIFICATION_FORWARD_CITIZEN,  
//          
//             
//            NOTIFICATION_PENDING_APPROVAL,  
//            NOTIFICATION_EXPIRED, NOTIFICATION_MANUAL_EXPIRED
//           ));
    
    public static final String NOTIFICATION_INITIATED_TEST = "cr.en.counter.approved";
    public static final List<String> NOTIFICATION_CODES = Collections.unmodifiableList(Arrays.asList(
    		NOTIFICATION_INITIATED_TEST
           ));

}


