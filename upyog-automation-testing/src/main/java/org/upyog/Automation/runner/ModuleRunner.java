package org.upyog.Automation.runner;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.upyog.Automation.Base.BaseTest;
import org.upyog.Automation.Utils.WorkflowDataStore;
import org.upyog.Automation.engine.TestEngine;
import org.upyog.Automation.engine.TestEngine.ExecutionResult;


public class ModuleRunner {

    private static final Logger logger =
            LoggerFactory.getLogger(ModuleRunner.class);

    private static final String PROPERTIES_PATH =
            "config/dev.properties";

    public static void main(String[] args) {

        String[] modulesToRun =
                args.length > 0
                        ? args
                        : new String[]{
                        "ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM",


                };

        WebDriver driver = null;

        try {

            WorkflowDataStore.put("selected.url",
                    "https://upyog.niua.org/upyog-ui/citizen/login");

            WorkflowDataStore.put("selected.cndCitizen.url",
                    "https://niuatt.niua.in/cnd-ui/citizen/login");

            WorkflowDataStore.put("selected.svCitizen.url",
                    "https://upyog.niua.org/sv-ui/citizen/login");

            WorkflowDataStore.put("selected.cndEmployee.url",
                    "https://upyog.niua.org/cnd-ui/employee/login");

            WorkflowDataStore.put("selected.svEmployee.url",
                    "https://upyog.niua.org/sv-ui/employee/login");

            if (WorkflowDataStore.get("selected.mobile") == null)
                WorkflowDataStore.put("selected.mobile", "9999999999");

            if (WorkflowDataStore.get("selected.otp") == null)
                WorkflowDataStore.put("selected.otp", "123456");

            if (WorkflowDataStore.get("selected.city") == null)
                WorkflowDataStore.put("selected.city", "Delhi");

            if (WorkflowDataStore.get("selected.permitNo") == null)
                WorkflowDataStore.put("selected.permitNo", "TEST123");

            BaseTest baseTest = new BaseTest();
            baseTest.setUp();

            driver = baseTest.getDriver();

            TestEngine engine =
                    new TestEngine(driver, PROPERTIES_PATH);

            for (String module : modulesToRun) {
                logger.info("Running module: {}", module);
                runModule(engine, module.toUpperCase());
            }

            baseTest.tearDown();

        } catch (Exception e) {
            logger.error("Execution Failed", e);
        }
    }

    private static void runModule(
            TestEngine engine,
            String moduleName) {

        ExecutionResult result;

        switch (moduleName) {

            // =====================================================
            // EWASTE
            // =====================================================

            case "EWASTE_CITIZEN":

                result = engine.executeModule(
                        "test-config/ewaste/ewaste_citizen_module.json"
                );

                break;

            case "EWASTE_EMPLOYEE":

                result = engine.executeModule(
                        "test-config/ewaste/ewaste_employee_module.json"
                );

                break;


            // =====================================================
            // ADVERTISEMENT
            // =====================================================

            case "ADVERTISEMENT_CITIZEN":

                result = engine.executeModule(
                        "test-config/advertisement/adv_citizen_module.json"
                );

                break;

            case "ADVERTISEMENT_EMPLOYEE":

                result = engine.executeModule(
                        "test-config/advertisement/adv_employee_module.json"
                );

                break;


            // =====================================================
            // ASSET
            // =====================================================

            case "ASSET_EMPLOYEE":

                result = engine.executeModule(
                        "test-config/asset/asset_employee_module.json"
                );

                break;

            case "ASSET_VERIFIER":

                result = engine.executeModule(
                        "test-config/asset/asset_employeeVerifier_module.json"
                );

                break;

            case "ASSET_APPROVER":

                result = engine.executeModule(
                        "test-config/asset/asset_employeeApprover_module.json"
                );

                break;


            // =====================================================
            // CHB
            // =====================================================

            case "CHB_CITIZEN":

                result = engine.executeModule(
                        "test-config/chb/chb_citizen_module.json"
                );

                break;

            case "CHB_EMPLOYEE":

                result = engine.executeModule(
                        "test-config/chb/chb_employee_module.json"
                );

                break;


            // =====================================================
            // CND
            // =====================================================

            case "CND_REQUEST":

                result = engine.executeModule(
                        "test-config/cnd/cnd_citizen_module.json"
                );

                break;

            case "CND_EMPLOYEE":

                result = engine.executeModule(
                        "test-config/cnd/cnd_employee_module.json"
                );

                break;

            case "CND_VENDOR":

                result = engine.executeModule(
                        "test-config/cnd/cnd_vendor_module.json"
                );

                break;

            // =====================================================
            // DESLUDGING SERVICE
            // =====================================================

            case "DESLUDGING_CITIZEN":

                result = engine.executeModule(
                        "test-config/desludging/desludging_citizen_module.json"
                );

                break;

            case "DESLUDGING_EMPLOYEE":

                result = engine.executeModule(
                        "test-config/desludging/desludging_employee_module.json"
                );

                break;

            case "DESLUDGING_CITIZEN_PAYMENT":

                result = engine.executeModule(
                        "test-config/desludging/desludging_citizenPayment_module.json"
                );

                break;

            case "DESLUDGING_ASSIGN_PSSO":

                result = engine.executeModule(
                        "test-config/desludging/desludging_assignPsso_module.json"
                );

                break;

            case "DESLUDGING_FSTPO":

                result = engine.executeModule(
                        "test-config/desludging/desludging_fstpo_module.json"
                );

                break;




            // =====================================================
            // OBPAS
            // =====================================================

            case "ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM":

                result = engine.executeModule(
                        "test-config/obpas/obpas_citizen_module.json"
                );

                break;

            case "ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM_EMPLOYEE":

                result = engine.executeModule(
                        "test-config/obpas/obpas_employee_module.json"
                );

                break;

            case "ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM_OC":

                result = engine.executeModule(
                        "test-config/obpas/obpas_oc_citizen_module.json"
                );

                break;

            case "ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM_OC_EMP":

                result = engine.executeModule(
                        "test-config/obpas/obpas_oc_employee_module.json"
                );

                break;


            // =====================================================
            // PET
            // =====================================================

            case "PET":

                result = engine.executeModule(
                        "test-config/pet/pet_citizen_module.json"
                );

                break;

            case "PET_EMP":

                result = engine.executeModule(
                        "test-config/pet/pet_employee_module.json"
                );

                break;


            // =====================================================
            // PROPERTY TAX
            // =====================================================

            case "PROPERTY_TAX":

                result = engine.executeModule(
                        "test-config/propertyTax/property_tax_citizen_module.json"
                );

                break;

            case "PROPERTY_TAX_EMP":

                result = engine.executeModule(
                        "test-config/propertyTax/property_tax_employee_module.json"
                );

                break;


            // =====================================================
            // PGR
            // =====================================================

            case "PGR":

                result = engine.executeModule(
                        "test-config/pgr/pgr_citizen_module.json"
                );

                break;

            case "PGR_EMP":

                result = engine.executeModule(
                        "test-config/pgr/pgr_employee_module.json"
                );

                break;

            // =====================================================
            //  REQUEST SERVICE
            // =====================================================

            case "MOBILE_TOILET_CITIZEN":

                result = engine.executeModule(
                        "test-config/requestService/mobile_toilet_citizen_module.json"
                );

                break;

            case "MOBILE_TOILET_EMP":

                result = engine.executeModule(
                        "test-config/requestService/mobile_toilet_employee_module.json"
                );

                break;
            case "MOBILE_TOILET_VENDOR":

                result = engine.executeModule(
                        "test-config/requestService/mobile_toilet_vendor_module.json"
                );

                break;

            case "TREE_PRUNING_CITIZEN":

                result = engine.executeModule(
                        "test-config/requestService/tree_pruning_citizen_module.json"
                );

                break;

            case "TREE_PRUNING_EMP":

                result = engine.executeModule(
                        "test-config/requestService/tree_pruning_employee_module.json"
                );

                break;

            case "TREE_PRUNING_VENDOR":

                result = engine.executeModule(
                        "test-config/requestService/tree_pruning_vendor_module.json"
                );

                break;

            case "WATER_TANKER_CITIZEN":

                result = engine.executeModule(
                        "test-config/requestService/water_tanker_citizen_module.json"
                );

                break;

            case "WATER_TANKER_EMP":

                result = engine.executeModule(
                        "test-config/requestService/water_tanker_employee_module.json"
                );

                break;

            case "WATER_TANKER_VENDOR":

                result = engine.executeModule(
                        "test-config/requestService/water_tanker_vendor_module.json"
                );

                break;


            // =====================================================
            // STREET VENDING
            // =====================================================

            case "STREET_VENDING":

                result = engine.executeModule(
                        "test-config/streetVending/street_vending_citizen_module.json"
                );

                break;

            case "STREET_VENDING_EMP":

                result = engine.executeModule(
                        "test-config/streetVending/street_vending_employee_module.json"
                );

                break;


            // =====================================================
            // TRADE LICENSE
            // =====================================================

            case "TRADE_LICENSE":

                result = engine.executeModule(
                        "test-config/tradelicense/trade_license_citizen_module.json"
                );

                break;

            case "TRADE_LICENSE_EMP":

                result = engine.executeModule(
                        "test-config/tradelicense/trade_license_employee_module.json"
                );

                break;


            // =====================================================
            // WATER + SEWERAGE
            // =====================================================

            case "WATER_AND_SEWERAGE":

                result = engine.executeModule(
                        "test-config/waterAndSewerage/water_and_sewerage_citizen_module.json"
                );

                break;

            case "WATER_EMP":

                result = engine.executeModule(
                        "test-config/waterAndSewerage/water_employee_module.json"
                );

                break;

            case "SEWERAGE_EMP":

                result = engine.executeModule(
                        "test-config/waterAndSewerage/sewerage_employee_module.json"
                );

                break;


            default:

                throw new RuntimeException(
                        "Unknown module : " + moduleName
                );
        }

        reportResult(result);
    }

    private static void reportResult(
            ExecutionResult result) {

        if (result.isSuccess()) {

            logger.info(
                    "PASSED : {}",
                    result
            );

        } else {

            logger.error(
                    "FAILED : {}",
                    result
            );
        }
    }
}