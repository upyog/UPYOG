package org.upyog.Automation.Utils;

/**
 * Centralized constants used across multiple components of the UPYOG automation framework.
 *
 * <p>Only constants that are shared across multiple classes or modules are maintained
 * in this class. Single-use constants and class-specific configurations remain in their
 * respective classes.</p>
 */
public final class AutomationConstants {

    /**
     * Prevents instantiation of the constants utility class.
     */
    private AutomationConstants() {
        // Prevent object creation
    }

    // =========================================================================
    // Module Name Identifiers
    // =========================================================================

    public static final String MODULE_PET_REGISTRATION = "PET_REGISTRATION";
    public static final String MODULE_PET = "PET";
    public static final String MODULE_PET_EMP = "PET_EMP";
    public static final String MODULE_PET_CEMP = "PET_CEMP";

    public static final String MODULE_PUBLIC_GRIEVANCE_REDRESSAL = "PUBLIC_GRIEVANCE_REDRESSAL";
    public static final String MODULE_PGR = "PGR";
    public static final String MODULE_PGR_EMP = "PGR_EMP";

    public static final String MODULE_NO_DUE_CERTIFICATE = "NO_DUE_CERTIFICATE";
    public static final String MODULE_NDC = "NDC";
    public static final String MODULE_NO_DUE_CERTIFICATE_CITIZEN = "NO_DUE_CERTIFICATE_CITIZEN";
    public static final String MODULE_NO_DUE_CERTIFICATE_EMPLOYEE = "NO_DUE_CERTIFICATE_EMPLOYEE";

    public static final String MODULE_WATER_TANKER = "WATER_TANKER";
    public static final String MODULE_WATER_TANKER_CITIZEN = "WATER_TANKER_CITIZEN";
    public static final String MODULE_WATER_TANKER_EMP = "WATER_TANKER_EMP";
    public static final String MODULE_WATER_TANKER_VENDOR = "WATER_TANKER_VENDOR";

    public static final String MODULE_MOBILE_TOILET = "MOBILE_TOILET";
    public static final String MODULE_MOBILE_TOILET_CITIZEN = "MOBILE_TOILET_CITIZEN";
    public static final String MODULE_MOBILE_TOILET_EMP = "MOBILE_TOILET_EMP";
    public static final String MODULE_MOBILE_TOILET_VENDOR = "MOBILE_TOILET_VENDOR";

    public static final String MODULE_OBPAS = "OBPAS";
    public static final String MODULE_OBPAS_OC = "OBPAS_OC";
    public static final String MODULE_ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM = "ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM";
    public static final String MODULE_ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM_EMPLOYEE = "ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM_EMPLOYEE";
    public static final String MODULE_ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM_OC = "ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM_OC";
    public static final String MODULE_ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM_OC_EMP = "ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM_OC_EMP";

    public static final String MODULE_CND = "CND";
    public static final String MODULE_CONSTRUCTION_AND_DEMOLITION = "CONSTRUCTION_AND_DEMOLITION";
    public static final String MODULE_CND_REQUEST = "CND_REQUEST";
    public static final String MODULE_CND_EMP = "CND_EMP";
    public static final String MODULE_CND_EMPLOYEE = "CND_EMPLOYEE";
    public static final String MODULE_CND_VENDOR = "CND_VENDOR";

    public static final String MODULE_PROPERTY_TAX = "PROPERTY_TAX";
    public static final String MODULE_PT = "PT";
    public static final String MODULE_PROPERTY_TAX_EMP = "PROPERTY_TAX_EMP";

    public static final String MODULE_ADVERTISEMENT = "ADVERTISEMENT";
    public static final String MODULE_ADV = "ADV";
    public static final String MODULE_ADVERTISEMENT_CITIZEN = "ADVERTISEMENT_CITIZEN";
    public static final String MODULE_ADVERTISEMENT_EMPLOYEE = "ADVERTISEMENT_EMPLOYEE";

    public static final String MODULE_STREET_VENDING = "STREET_VENDING";
    public static final String MODULE_SV = "SV";
    public static final String MODULE_STREET_VENDING_EMP = "STREET_VENDING_EMP";

    public static final String MODULE_TRADE_LICENSE = "TRADE_LICENSE";
    public static final String MODULE_TL = "TL";
    public static final String MODULE_TRADE_LICENSE_EMP = "TRADE_LICENSE_EMP";

    public static final String MODULE_TREE_PRUNING = "TREE_PRUNING";
    public static final String MODULE_TREE_PRUNING_CITIZEN = "TREE_PRUNING_CITIZEN";
    public static final String MODULE_TREE_PRUNING_EMP = "TREE_PRUNING_EMP";
    public static final String MODULE_TREE_PRUNING_VERIFIER = "TREE_PRUNING_VERIFIER";
    public static final String MODULE_TREE_PRUNING_VENDOR = "TREE_PRUNING_VENDOR";

    public static final String MODULE_EWASTE = "EWASTE";
    public static final String MODULE_EWASTE_MANAGEMENT_SYSTEM = "EWASTE_MANAGEMENT_SYSTEM";
    public static final String MODULE_EWASTE_CITIZEN = "EWASTE_CITIZEN";
    public static final String MODULE_EWASTE_EMPLOYEE = "EWASTE_EMPLOYEE";

    public static final String MODULE_CHB = "CHB";
    public static final String MODULE_COMMUNITY_HALL_BOOKING = "COMMUNITY_HALL_BOOKING";
    public static final String MODULE_CHB_CITIZEN = "CHB_CITIZEN";
    public static final String MODULE_CHB_EMPLOYEE = "CHB_EMPLOYEE";

    public static final String MODULE_GARBAGE_COLLECTION = "GARBAGE_COLLECTION";
    public static final String MODULE_GARBAGE_COLLECTION_PAYMENT = "GARBAGE_COLLECTION_PAYMENT";
    public static final String MODULE_GC_CITIZEN = "GC_CITIZEN";
    public static final String MODULE_GC_EMPLOYEE = "GC_EMPLOYEE";

    public static final String MODULE_ESTATE_MANAGEMENT = "ESTATE_MANAGEMENT";
    public static final String MODULE_ESTATE_MANAGEMENT_CITIZEN = "ESTATE_MANAGEMENT_CITIZEN";
    public static final String MODULE_ESTATE_MANAGEMENT_EMPLOYEE = "ESTATE_MANAGEMENT_EMPLOYEE";

    public static final String MODULE_DESLUDGING = "DESLUDGING";
    public static final String MODULE_DESLUDGING_SERVICE = "DESLUDGING_SERVICE";
    public static final String MODULE_DESLUDGING_CITIZEN = "DESLUDGING_CITIZEN";
    public static final String MODULE_DESLUDGING_EMPLOYEE = "DESLUDGING_EMPLOYEE";
    public static final String MODULE_DESLUDGING_PAYMENT = "DESLUDGING_PAYMENT";
    public static final String MODULE_DESLUDGING_SERVICE_PAYMENT = "DESLUDGING_SERVICE_PAYMENT";
    public static final String MODULE_DESLUDGING_CITIZEN_PAYMENT = "DESLUDGING_CITIZEN_PAYMENT";
    public static final String MODULE_DESLUDGING_PAYMENT2 = "DESLUDGING_PAYMENT2";
    public static final String MODULE_DESLUDGING_SERVICE_PAYMENT2 = "DESLUDGING_SERVICE_PAYMENT2";
    public static final String MODULE_DESLUDGING_EMP_UPDATE = "DESLUDGING_EMP_UPDATE";
    public static final String MODULE_DESLUDGING_EMPLOYEE_UPDATE = "DESLUDGING_EMPLOYEE_UPDATE";
    public static final String MODULE_DESLUDGING_EMP_COMPLETE = "DESLUDGING_EMP_COMPLETE";
    public static final String MODULE_DESLUDGING_EMP_PSSO = "DESLUDGING_EMPLOYEE_PSSO";
    public static final String MODULE_DESLUDGING_ASSIGN_PSSO = "DESLUDGING_ASSIGN_PSSO";
    public static final String MODULE_DESLUDGING_EMP_FSTPO = "DESLUDGING_EMPLOYEE_FSTPO";
    public static final String MODULE_DESLUDGING_FSTPO = "DESLUDGING_FSTPO";

    public static final String MODULE_WATER_AND_SEWERAGE = "WATER_AND_SEWERAGE";
    public static final String MODULE_WS = "WS";
    public static final String MODULE_WATER = "WATER";
    public static final String MODULE_WATER_CITIZEN = "WATER_CITIZEN";
    public static final String MODULE_WATER_EMP = "WATER_EMP";
    public static final String MODULE_SEWERAGE = "SEWERAGE";
    public static final String MODULE_SEWERAGE_CITIZEN = "SEWERAGE_CITIZEN";
    public static final String MODULE_SEWERAGE_EMP = "SEWERAGE_EMP";

    public static final String MODULE_ASSET_MANAGEMENT = "ASSET_MANAGEMENT";
    public static final String MODULE_ASSET_MANAGEMENT_SYSTEM = "ASSET_MANAGEMENT_SYSTEM";
    public static final String MODULE_ASSET_EMPLOYEE = "ASSET_EMPLOYEE";
    public static final String MODULE_ASSET_VERIFIER = "ASSET_MANAGEMENT_SYSTEM_VERIFIER";
    public static final String MODULE_ASSET_VERIFIER_RUNNER = "ASSET_VERIFIER";
    public static final String MODULE_ASSET_APPROVER = "ASSET_MANAGEMENT_SYSTEM_APPROVER";
    public static final String MODULE_ASSET_APPROVER_RUNNER = "ASSET_APPROVER";

    public static final String MODULE_CHALLAN_GENERATION = "CHALLAN_GENERATION";
    public static final String MODULE_CHALLAN_GENERATION_CITIZEN = "CHALLAN_GENERATION_CITIZEN";
    public static final String MODULE_CHALLAN_GENERATION_EMPLOYEE = "CHALLAN_GENERATION_EMPLOYEE";

    // =========================================================================
    // JSON Module Configuration Paths - Citizen
    // =========================================================================

    public static final String CONFIG_STREET_VENDING_CITIZEN = "test-config/streetVending/street_vending_citizen_module.json";
    public static final String CONFIG_TRADE_LICENSE_CITIZEN = "test-config/tradeLicense/trade_license_citizen_module.json";
    public static final String CONFIG_PET_CITIZEN = "test-config/pet/pet_citizen_module.json";
    public static final String CONFIG_PET_CEMP = "test-config/pet/pet_cemp_create.json";
    public static final String CONFIG_ADVERTISEMENT_CITIZEN = "test-config/advertisement/adv_citizen_module.json";
    public static final String CONFIG_TREE_PRUNING_CITIZEN = "test-config/requestService/tree_pruning_citizen_module.json";
    public static final String CONFIG_WATER_TANKER_CITIZEN = "test-config/requestService/water_tanker_citizen_module.json";
    public static final String CONFIG_MOBILE_TOILET_CITIZEN = "test-config/requestService/mobile_toilet_citizen_module.json";
    public static final String CONFIG_PROPERTY_TAX_CITIZEN = "test-config/propertyTax/property_tax_citizen_module.json";
    public static final String CONFIG_PGR_CITIZEN = "test-config/pgr/pgr_citizen_module.json";
    public static final String CONFIG_OBPAS_CITIZEN = "test-config/obpas/obpas_citizen_module.json";
    public static final String CONFIG_OBPAS_OC_CITIZEN = "test-config/obpas/obpas_oc_create.json";
    public static final String CONFIG_OBPAS_OC_CITIZEN_MODULE = "test-config/obpas/obpas_oc_citizen_module.json";
    public static final String CONFIG_EWASTE_CITIZEN = "test-config/ewaste/ewaste_citizen_module.json";
    public static final String CONFIG_CHB_CITIZEN = "test-config/chb/chb_citizen_module.json";
    public static final String CONFIG_CND_CITIZEN = "test-config/cnd/cnd_citizen_module.json";
    public static final String CONFIG_DESLUDGING_CITIZEN = "test-config/desludging/desludging_citizen_module.json";
    public static final String CONFIG_DESLUDGING_CITIZEN_PAYMENT = "test-config/desludging/desludging_citizenPayment_module.json";
    public static final String CONFIG_DESLUDGING_CITIZEN_PAYMENT2 = "test-config/desludging/desludging_citizenPayment2_module.json";
    public static final String CONFIG_WATER_AND_SEWERAGE_CITIZEN = "test-config/waterAndSewerage/water_and_sewerage_citizen_module.json";
    public static final String CONFIG_GARBAGE_COLLECTION_CITIZEN = "test-config/garbageCollection/gc_citizen_module.json";
    public static final String CONFIG_GARBAGE_COLLECTION_PAYMENT = "test-config/garbageCollection/gc_citizen_payment_module.json";
    public static final String CONFIG_ESTATE_MANAGEMENT_CITIZEN = "test-config/estateManagement/estateManagement_citizen_module.json";
    public static final String CONFIG_CHALLAN_CITIZEN = "test-config/challanGeneration/cg_citizen_module.json";
    public static final String CONFIG_NDC_CITIZEN = "test-config/noDueCertificate/ndc_citizen__module.json";

    // =========================================================================
    // JSON Module Configuration Paths - Employee
    // =========================================================================

    public static final String CONFIG_STREET_VENDING_EMPLOYEE = "test-config/streetVending/street_vending_employee_module.json";
    public static final String CONFIG_PET_EMPLOYEE = "test-config/pet/pet_employee_module.json";
    public static final String CONFIG_TRADE_LICENSE_EMPLOYEE = "test-config/tradeLicense/trade_license_employee_module.json";
    public static final String CONFIG_ASSET_INITIATOR = "test-config/asset/asset_employee_module.json";
    public static final String CONFIG_ASSET_VERIFIER = "test-config/asset/asset_employeeVerifier_module.json";
    public static final String CONFIG_ASSET_APPROVER = "test-config/asset/asset_employeeApprover_module.json";
    public static final String CONFIG_ASSET_EMPLOYEE = "test-config/asset/asset_employee_module.json";
    public static final String CONFIG_ASSET_EMPLOYEE_VERIFIER = "test-config/asset/asset_employeeVerifier_module.json";
    public static final String CONFIG_ASSET_EMPLOYEE_APPROVER = "test-config/asset/asset_employeeApprover_module.json";
    public static final String CONFIG_ADVERTISEMENT_EMPLOYEE = "test-config/advertisement/adv_employee_module.json";
    public static final String CONFIG_PROPERTY_TAX_EMPLOYEE = "test-config/propertyTax/property_tax_employee_module.json";
    public static final String CONFIG_EWASTE_EMPLOYEE = "test-config/ewaste/ewaste_employee_module.json";
    public static final String CONFIG_DESLUDGING_EMPLOYEE = "test-config/desludging/desludging_employee_module.json";
    public static final String CONFIG_DESLUDGING_EMPLOYEE_UPDATE = "test-config/desludging/desludging_employee_update.json";
    public static final String CONFIG_DESLUDGING_EMPLOYEE_COMPLETE = "test-config/desludging/desludging_employee_complete.json";
    public static final String CONFIG_DESLUDGING_EMPLOYEE_MODULE = "test-config/desludging/desludging_employee_module.json";
    public static final String CONFIG_DESLUDGING_EMPLOYEE_PSSO = "test-config/desludging/desludging_assign_psso.json";
    public static final String CONFIG_DESLUDGING_ASSIGN_PSSO_MODULE = "test-config/desludging/desludging_assignPsso_module.json";
    public static final String CONFIG_DESLUDGING_EMPLOYEE_FSTPO = "test-config/desludging/desludging_fstpo.json";
    public static final String CONFIG_OBPAS_EMPLOYEE = "test-config/obpas/obpas_employee_module.json";
    public static final String CONFIG_OBPAS_OC_EMPLOYEE = "test-config/obpas/obpas_oc_employee_module.json";
    public static final String CONFIG_WATER_TANKER_EMPLOYEE = "test-config/requestService/water_tanker_employee_module.json";
    public static final String CONFIG_TREE_PRUNING_EMPLOYEE = "test-config/requestService/tree_pruning_employee_module.json";
    public static final String CONFIG_TREE_PRUNING_VERIFIER = "test-config/requestService/tree_pruning_verifier_module.json";
    public static final String CONFIG_MOBILE_TOILET_EMPLOYEE = "test-config/requestService/mobile_toilet_employee_module.json";
    public static final String CONFIG_CHB_EMPLOYEE = "test-config/chb/chb_employee_module.json";
    public static final String CONFIG_CND_EMPLOYEE = "test-config/cnd/cnd_employee_module.json";
    public static final String CONFIG_PGR_EMPLOYEE = "test-config/pgr/pgr_employee_module.json";
    public static final String CONFIG_SEWERAGE_EMPLOYEE = "test-config/waterAndSewerage/sewerage_employee_module.json";
    public static final String CONFIG_WATER_EMPLOYEE = "test-config/waterAndSewerage/water_employee_module.json";
    public static final String CONFIG_GARBAGE_COLLECTION_EMPLOYEE = "test-config/garbageCollection/gc_employee_module.json";
    public static final String CONFIG_ESTATE_MANAGEMENT_EMPLOYEE = "test-config/estateManagement/estateManagement_employee_module.json";
    public static final String CONFIG_CHALLAN_EMPLOYEE = "test-config/challanGeneration/cg_employee_module.json";
    public static final String CONFIG_NDC_EMPLOYEE = "test-config/noDueCertificate/ndc_employee_module.json";

    // =========================================================================
    // JSON Module Configuration Paths - Vendor
    // =========================================================================

    public static final String CONFIG_WATER_TANKER_VENDOR = "test-config/requestService/water_tanker_vendor_module.json";
    public static final String CONFIG_MOBILE_TOILET_VENDOR = "test-config/requestService/mobile_toilet_vendor_module.json";
    public static final String CONFIG_TREE_PRUNING_VENDOR = "test-config/requestService/tree_pruning_vendor_module.json";
    public static final String CONFIG_CND_VENDOR = "test-config/cnd/cnd_vendor_module.json";

    // =========================================================================
    // JSON Configuration Paths - Workflow
    // =========================================================================

    public static final String CONFIG_DESLUDGING_WORKFLOW = "test-config/desludging/desludging_workflow.json";
    public static final String CONFIG_PET_WORKFLOW = "test-config/pet/pet_workflow.json";
    public static final String CONFIG_EWASTE_WORKFLOW = "test-config/ewaste/ewaste_workflow.json";
    public static final String CONFIG_WATER_TANKER_WORKFLOW = "test-config/requestService/water_tanker_workflow.json";
    public static final String CONFIG_TREE_PRUNING_WORKFLOW = "test-config/requestService/tree_pruning_workflow.json";
    public static final String CONFIG_MOBILE_TOILET_WORKFLOW = "test-config/requestService/mobile_toilet_workflow.json";
    public static final String CONFIG_STREET_VENDING_WORKFLOW = "test-config/streetVending/street_vending_workflow.json";
    public static final String CONFIG_TRADE_LICENSE_WORKFLOW = "test-config/tradeLicense/trade_license_workflow.json";
    public static final String CONFIG_ADVERTISEMENT_WORKFLOW = "test-config/advertisement/adv_workflow.json";
    public static final String CONFIG_PROPERTY_TAX_WORKFLOW = "test-config/propertyTax/property_tax_workflow.json";
    public static final String CONFIG_PGR_WORKFLOW = "test-config/pgr/pgr_workflow.json";
    public static final String CONFIG_OBPAS_WORKFLOW = "test-config/obpas/obpas_workflow.json";
    public static final String CONFIG_CHB_WORKFLOW = "test-config/chb/chb_workflow.json";
    public static final String CONFIG_CND_WORKFLOW = "test-config/cnd/cnd_workflow.json";
    public static final String CONFIG_WATER_AND_SEWERAGE_WORKFLOW = "test-config/waterAndSewerage/water_and_sewerage_workflow.json";
    public static final String CONFIG_ASSET_WORKFLOW = "test-config/asset/asset_workflow.json";
    public static final String CONFIG_GARBAGE_COLLECTION_WORKFLOW = "test-config/garbageCollection/gc_workflow.json";
    public static final String CONFIG_ESTATE_MANAGEMENT_WORKFLOW = "test-config/estateManagement/estateManagement_workflow_module.json";
    public static final String CONFIG_CHALLAN_WORKFLOW = "test-config/challanGeneration/cg_workflow_module.json";
    public static final String CONFIG_NDC_WORKFLOW = "test-config/noDueCertificate/ndc_workflow.json";

    // =========================================================================
    // JSON Configuration Paths - Stakeholder
    // =========================================================================

    public static final String CONFIG_DESLUDGING_STAKEHOLDER = "test-config/desludging/desludging_stakeholder_module.json";
    public static final String CONFIG_PET_STAKEHOLDER = "test-config/pet/pet_stakeholder_module.json";
    public static final String CONFIG_EWASTE_STAKEHOLDER = "test-config/ewaste/ewaste_stakeholder_module.json";
    public static final String CONFIG_WATER_TANKER_STAKEHOLDER = "test-config/requestService/water_tanker_stakeholder_module.json";
    public static final String CONFIG_TREE_PRUNING_STAKEHOLDER = "test-config/requestService/tree_pruning_stakeholder_module.json";
    public static final String CONFIG_MOBILE_TOILET_STAKEHOLDER = "test-config/requestService/mobile_toilet_stakeholder_module.json";
    public static final String CONFIG_STREET_VENDING_STAKEHOLDER = "test-config/streetVending/street_vending_stakeholder_module.json";
    public static final String CONFIG_TRADE_LICENSE_STAKEHOLDER = "test-config/tradeLicense/trade_license_stakeholder_module.json";
    public static final String CONFIG_ADVERTISEMENT_STAKEHOLDER = "test-config/advertisement/adv_stakeholder_module.json";
    public static final String CONFIG_PROPERTY_TAX_STAKEHOLDER = "test-config/propertyTax/property_tax_stakeholder_module.json";
    public static final String CONFIG_PGR_STAKEHOLDER = "test-config/pgr/pgr_stakeholder_module.json";
    public static final String CONFIG_OBPAS_STAKEHOLDER = "test-config/obpas/obpas_stakeholder_module.json";
    public static final String CONFIG_CHB_STAKEHOLDER = "test-config/chb/chb_stakeholder_module.json";
    public static final String CONFIG_CND_STAKEHOLDER = "test-config/cnd/cnd_stakeholder_module.json";
    public static final String CONFIG_WATER_AND_SEWERAGE_STAKEHOLDER = "test-config/waterAndSewerage/water_and_sewerage_stakeholder_module.json";
    public static final String CONFIG_ASSET_STAKEHOLDER = "test-config/asset/asset_stakeholder_module.json";
    public static final String CONFIG_GARBAGE_COLLECTION_STAKEHOLDER = "test-config/garbageCollection/gc_stakeholder_module.json";
    public static final String CONFIG_ESTATE_MANAGEMENT_STAKEHOLDER = "test-config/estateManagement/estateManagement_stakeholder_module.json";
    public static final String CONFIG_CHALLAN_STAKEHOLDER = "test-config/challanGeneration/cg_stakeholder_module.json";
    public static final String CONFIG_NDC_STAKEHOLDER = "test-config/noDueCertificate/ndc_stakeholder_module.json";

    // =========================================================================
    // Properties & Messages
    // =========================================================================

    public static final String DEV_PROPERTIES_PATH = "config/dev.properties";
    public static final String MSG_WORKFLOW_EXECUTED = "Workflow Executed";

    // =========================================================================
    // Excel Sheet Name & Column Constants
    // =========================================================================

    public static final String DEFAULT_EXCEL_FILE = "test-data/test-data.xlsx";
    public static final String SHEET_SUFFIX = "_Test_Data";
    public static final String SHEET_PET = "PET_Test_Data";
    public static final String SHEET_PGR = "PGR_Test_Data";
    public static final String SHEET_NDC = "NDC_Test_Data";
    public static final String SHEET_PT = "PT_Test_Data";
    public static final String SHEET_ADVERTISEMENT = "Advertisement_Test_Data";
    public static final String SHEET_STREET_VENDING = "StreetVending_Test_Data";
    public static final String SHEET_TRADE_LICENSE = "TradeLicense_Test_Data";
    public static final String SHEET_TREE_PRUNING = "TreePruning_Test_Data";
    public static final String SHEET_WATER_TANKER = "WaterTanker_Test_Data";
    public static final String SHEET_MOBILE_TOILET = "MobileToilet_Test_Data";
    public static final String SHEET_OBPAS = "OBPAS_Test_Data";
    public static final String SHEET_OBPAS_OC_CREATE = "OBPAS_OC_Create_Test_Data";
    public static final String SHEET_EWASTE = "EWaste_Test_Data";
    public static final String SHEET_CHB = "CHB_Test_Data";
    public static final String SHEET_CND = "CND_Test_Data";
    public static final String SHEET_DESLUDGING = "Desludging_Test_Data";
    public static final String SHEET_DESLUDGING_PAYMENT = "Desludging_Payment_Test_Data";
    public static final String SHEET_DESLUDGING_PAYMENT2 = "Desludging_Payment2_Test_Data";
    public static final String SHEET_WATER_AND_SEWERAGE = "WaterAndSewerage_Test_Data";
    public static final String SHEET_GC = "GC_Test_Data";
    public static final String SHEET_GC_PAYMENT = "GC_Payment_Test_Data";
    public static final String SHEET_ESTATE_MANAGEMENT = "EstateManagement_Test_Data";
    public static final String SHEET_ASSET = "Asset_Test_Data";
    public static final String SHEET_CHALLAN = "Challan_Test_Data";
    public static final String SHEET_DESLUDGING_EMP_UPDATE = "Desludging_Employee_Update_Test_Data";
    public static final String SHEET_DESLUDGING_EMP_COMPLETE = "Desludging_Employee_Complete_Test_Data";

    public static final String EXCEL_COLUMN_STATUS = "Status";
    public static final String EXCEL_COLUMN_REMARK = "Remark";
    public static final String EXCEL_COLUMN_FAILED_STEPS = "Failed Steps";
    public static final String EXCEL_COLUMN_TEST_CASE = "TestCase";
    public static final String EXCEL_COLUMN_EXECUTE = "Execute";
    public static final String EXCEL_EXECUTE_YES = "YES";

    // =========================================================================
    // Locator Strategies (Used across LocatorResolver and ActionExecutor)
    // =========================================================================

    public static final String LOCATOR_ID = "ID";
    public static final String LOCATOR_NAME = "NAME";
    public static final String LOCATOR_XPATH = "XPATH";
    public static final String LOCATOR_CSS = "CSS";
    public static final String LOCATOR_CLASS_NAME = "CLASS_NAME";
    public static final String LOCATOR_TAG_NAME = "TAG_NAME";
    public static final String LOCATOR_LINK_TEXT = "LINK_TEXT";
    public static final String LOCATOR_PARTIAL_LINK_TEXT = "PARTIAL_LINK_TEXT";
    public static final String LOCATOR_URL = "URL";

    // =========================================================================
    // Action Types (Used across ActionExecutor)
    // =========================================================================

    public static final String ACTION_TYPE = "TYPE";
    public static final String ACTION_CLICK = "CLICK";
    public static final String ACTION_CLICK_JS = "CLICK_JS";
    public static final String ACTION_HOVER = "HOVER";
    public static final String ACTION_UPLOAD_FILE = "UPLOAD_FILE";
    public static final String ACTION_TYPE_OTP = "TYPE_OTP";
    public static final String ACTION_SELECT_RADIO_BY_TEXT = "SELECT_RADIO_BY_TEXT";
    public static final String ACTION_SELECT_DROPDOWN_BY_INDEX = "SELECT_DROPDOWN_BY_INDEX";
    public static final String ACTION_CHECK_LAST_CHECKBOX = "CHECK_LAST_CHECKBOX";
    public static final String ACTION_CAPTURE_TEXT = "CAPTURE_TEXT";
    public static final String ACTION_TYPE_FROM_STORE = "TYPE_FROM_STORE";
    public static final String ACTION_SET_DATE_TODAY = "SET_DATE_TODAY";
    public static final String ACTION_SET_DATE_PLUS_DAYS = "SET_DATE_PLUS_DAYS";
    public static final String ACTION_SWITCH_WINDOW = "SWITCH_WINDOW";
    public static final String ACTION_WAIT_FOR_TEXT = "WAIT_FOR_TEXT";
    public static final String ACTION_SET_DATE_TEXT = "SET_DATE_TEXT";
    public static final String ACTION_MULTI_SELECT_CHECKBOX = "MULTI_SELECT_CHECKBOX";
    public static final String ACTION_OPEN_URL = "OPEN_URL";
    public static final String ACTION_SET_CURRENT_TIME = "SET_CURRENT_TIME";
    public static final String ACTION_SET_CUSTOM_TIME = "SET_CUSTOM_TIME";
    public static final String ACTION_SET_DATE_JS = "SET_DATE_JS";
    public static final String ACTION_TYPE_BY_LABEL = "TYPE_BY_LABEL";
    public static final String ACTION_OPTIONAL_CLICK_JS = "OPTIONAL_CLICK_JS";
    public static final String ACTION_OPTIONAL_SELECT_DROPDOWN_BY_INDEX = "OPTIONAL_SELECT_DROPDOWN_BY_INDEX";
    public static final String ACTION_OPTIONAL_TYPE = "OPTIONAL_TYPE";
    public static final String ACTION_SELECT_BY_VALUE = "SELECT_BY_VALUE";
    public static final String ACTION_SCROLL_TO_ELEMENT = "SCROLL_TO_ELEMENT";
    public static final String ACTION_WAIT_VISIBLE = "WAIT_VISIBLE";
    public static final String ACTION_SELECT_DATE_RANGE = "SELECT_DATE_RANGE";
    public static final String ACTION_ACCEPT_ALERT = "ACCEPT_ALERT";
    public static final String ACTION_CLEAR_AND_TYPE = "CLEAR_AND_TYPE";
    public static final String ACTION_CAPTURE_SCREENSHOT = "CAPTURE_SCREENSHOT";
    public static final String ACTION_SCREENSHOT = "SCREENSHOT";

    // =========================================================================
    // Workflow Data Keys (Shared runtime data store keys)
    // =========================================================================

    public static final String APPLICATION_NO = "APPLICATION_NO";
    public static final String WATER_APPLICATION_NO = "WATER_APPLICATION_NO";
    public static final String SEWERAGE_APPLICATION_NO = "SEWERAGE_APPLICATION_NO";

    public static final String KEY_SELECTED_URL = "selected.url";
    public static final String KEY_SELECTED_ENV = "selected.env";
    public static final String KEY_SELECTED_CITY = "selected.city";
    public static final String KEY_SELECTED_MOBILE = "selected.mobile";
    public static final String KEY_SELECTED_OTP = "selected.otp";
    public static final String KEY_SELECTED_MODULE = "selected.module";
    public static final String KEY_SELECTED_APPLICATION_NO = "selected.applicationNo";
    public static final String KEY_SELECTED_USERNAME = "selected.username";
    public static final String KEY_SELECTED_PASSWORD = "selected.password";
    public static final String KEY_SELECTED_PERMIT_NO = "selected.permitNo";
    public static final String KEY_SELECTED_VALUE = "selectedValue";

    public static final String KEY_CURRENT_MODULE = "currentModule";
    public static final String KEY_CURRENT_TEST_CASE = "currentTestCase";

    public static final String KEY_FAILED_STEP = "FAILED_STEP";
    public static final String KEY_FAILED_ERROR = "FAILED_ERROR";
    public static final String KEY_FAILED_SCREENSHOT = "FAILED_SCREENSHOT";

    // =========================================================================
    // Report and Artifact Directories
    // =========================================================================

    public static final String REPORTS_DIR = "target/reports";
    public static final String SCREENSHOTS_DIR = "target/screenshots";
    public static final String MANUALS_DIR = "target/manuals";
    public static final String RECORDINGS_DIR = "target/recordings";

    // =========================================================================
    // Environment Identifiers
    // =========================================================================

    public static final String ENV_NIUATT = "NIUATT";
    public static final String ENV_UPYOG = "UPYOG";
    public static final String ENV_SANDBOX = "SANDBOX";

    // =========================================================================
    // Portal Authentication Paths
    // =========================================================================

    public static final String CITIZEN_LOGIN_PATH = "/citizen/login";
    public static final String EMPLOYEE_LOGIN_PATH = "/employee/login";

    // =========================================================================
    // Execution Status
    // =========================================================================

    public static final String STATUS_PASS = "PASS";
    public static final String STATUS_FAIL = "FAIL";
}