package org.upyog.Automation.runner;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.upyog.Automation.Base.BaseTest;
import org.upyog.Automation.Utils.AutomationConstants;
import org.upyog.Automation.Utils.WorkflowDataStore;
import org.upyog.Automation.engine.TestEngine;
import org.upyog.Automation.engine.TestEngine.ExecutionResult;


public class ModuleRunner {

    private static final Logger logger =
            LoggerFactory.getLogger(ModuleRunner.class);

    private static final String PROPERTIES_PATH =
            AutomationConstants.DEV_PROPERTIES_PATH;

    public static void main(String[] args) {

        String[] modulesToRun =
                args.length > 0
                        ? args
                        : new String[]{
                        AutomationConstants.MODULE_ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM,


                };

        WebDriver driver = null;

        try {

            WorkflowDataStore.put(AutomationConstants.KEY_SELECTED_URL,
                    "https://upyog.niua.org/upyog-ui/citizen/login");

            WorkflowDataStore.put("selected.cndCitizen.url",
                    "https://niuatt.niua.in/cnd-ui/citizen/login");

            WorkflowDataStore.put("selected.svCitizen.url",
                    "https://upyog.niua.org/sv-ui/citizen/login");

            WorkflowDataStore.put("selected.cndEmployee.url",
                    "https://upyog.niua.org/cnd-ui/employee/login");

            WorkflowDataStore.put("selected.svEmployee.url",
                    "https://upyog.niua.org/sv-ui/employee/login");

            if (WorkflowDataStore.get(AutomationConstants.KEY_SELECTED_MOBILE) == null)
                WorkflowDataStore.put(AutomationConstants.KEY_SELECTED_MOBILE, "9999999999");

            if (WorkflowDataStore.get(AutomationConstants.KEY_SELECTED_OTP) == null)
                WorkflowDataStore.put(AutomationConstants.KEY_SELECTED_OTP, "123456");

            if (WorkflowDataStore.get(AutomationConstants.KEY_SELECTED_CITY) == null)
                WorkflowDataStore.put(AutomationConstants.KEY_SELECTED_CITY, "Delhi");

            if (WorkflowDataStore.get(AutomationConstants.KEY_SELECTED_PERMIT_NO) == null)
                WorkflowDataStore.put(AutomationConstants.KEY_SELECTED_PERMIT_NO, "TEST123");

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

            case AutomationConstants.MODULE_EWASTE_CITIZEN:

                result = engine.executeModule(
                        AutomationConstants.CONFIG_EWASTE_CITIZEN
                );

                break;

            case AutomationConstants.MODULE_EWASTE_EMPLOYEE:

                result = engine.executeModule(
                        AutomationConstants.CONFIG_EWASTE_EMPLOYEE
                );

                break;


            // =====================================================
            // ADVERTISEMENT
            // =====================================================

            case AutomationConstants.MODULE_ADVERTISEMENT_CITIZEN:

                result = engine.executeModule(
                        AutomationConstants.CONFIG_ADVERTISEMENT_CITIZEN
                );

                break;

            case AutomationConstants.MODULE_ADVERTISEMENT_EMPLOYEE:

                result = engine.executeModule(
                        AutomationConstants.CONFIG_ADVERTISEMENT_EMPLOYEE
                );

                break;


            // =====================================================
            // ASSET
            // =====================================================

            case AutomationConstants.MODULE_ASSET_EMPLOYEE:

                result = engine.executeModule(
                        AutomationConstants.CONFIG_ASSET_EMPLOYEE
                );

                break;

            case AutomationConstants.MODULE_ASSET_VERIFIER_RUNNER:

                result = engine.executeModule(
                        AutomationConstants.CONFIG_ASSET_EMPLOYEE_VERIFIER
                );

                break;

            case AutomationConstants.MODULE_ASSET_APPROVER_RUNNER:

                result = engine.executeModule(
                        AutomationConstants.CONFIG_ASSET_EMPLOYEE_APPROVER
                );

                break;


            // =====================================================
            // CHB
            // =====================================================

            case AutomationConstants.MODULE_CHB_CITIZEN:

                result = engine.executeModule(
                        AutomationConstants.CONFIG_CHB_CITIZEN
                );

                break;

            case AutomationConstants.MODULE_CHB_EMPLOYEE:

                result = engine.executeModule(
                        AutomationConstants.CONFIG_CHB_EMPLOYEE
                );

                break;


            // =====================================================
            // CND
            // =====================================================

            case AutomationConstants.MODULE_CND_REQUEST:

                result = engine.executeModule(
                        AutomationConstants.CONFIG_CND_CITIZEN
                );

                break;

            case AutomationConstants.MODULE_CND_EMPLOYEE:

                result = engine.executeModule(
                        AutomationConstants.CONFIG_CND_EMPLOYEE
                );

                break;

            case AutomationConstants.MODULE_CND_VENDOR:

                result = engine.executeModule(
                        AutomationConstants.CONFIG_CND_VENDOR
                );

                break;

            // =====================================================
            // DESLUDGING SERVICE
            // =====================================================

            case AutomationConstants.MODULE_DESLUDGING_CITIZEN:

                result = engine.executeModule(
                        AutomationConstants.CONFIG_DESLUDGING_CITIZEN
                );

                break;

            case AutomationConstants.MODULE_DESLUDGING_EMPLOYEE:

                result = engine.executeModule(
                        AutomationConstants.CONFIG_DESLUDGING_EMPLOYEE_MODULE
                );

                break;

            case AutomationConstants.MODULE_DESLUDGING_CITIZEN_PAYMENT:

                result = engine.executeModule(
                        AutomationConstants.CONFIG_DESLUDGING_CITIZEN_PAYMENT
                );

                break;

            case AutomationConstants.MODULE_DESLUDGING_ASSIGN_PSSO:

                result = engine.executeModule(
                        AutomationConstants.CONFIG_DESLUDGING_ASSIGN_PSSO_MODULE
                );

                break;

            case AutomationConstants.MODULE_DESLUDGING_FSTPO:

                result = engine.executeModule(
                        AutomationConstants.CONFIG_DESLUDGING_EMPLOYEE_FSTPO
                );

                break;




            // =====================================================
            // OBPAS
            // =====================================================

            case AutomationConstants.MODULE_ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM:

                result = engine.executeModule(
                        AutomationConstants.CONFIG_OBPAS_CITIZEN
                );

                break;

            case AutomationConstants.MODULE_ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM_EMPLOYEE:

                result = engine.executeModule(
                        AutomationConstants.CONFIG_OBPAS_EMPLOYEE
                );

                break;

            case AutomationConstants.MODULE_ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM_OC:

                result = engine.executeModule(
                        AutomationConstants.CONFIG_OBPAS_OC_CITIZEN_MODULE
                );

                break;

            case AutomationConstants.MODULE_ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM_OC_EMP:

                result = engine.executeModule(
                        AutomationConstants.CONFIG_OBPAS_OC_EMPLOYEE
                );

                break;


            // =====================================================
            // PET
            // =====================================================

            case AutomationConstants.MODULE_PET:

                result = engine.executeModule(
                        AutomationConstants.CONFIG_PET_CITIZEN
                );

                break;

            case AutomationConstants.MODULE_PET_EMP:

                result = engine.executeModule(
                        AutomationConstants.CONFIG_PET_EMPLOYEE
                );

                break;


            // =====================================================
            // PROPERTY TAX
            // =====================================================

            case AutomationConstants.MODULE_PROPERTY_TAX:

                result = engine.executeModule(
                        AutomationConstants.CONFIG_PROPERTY_TAX_CITIZEN
                );

                break;

            case AutomationConstants.MODULE_PROPERTY_TAX_EMP:

                result = engine.executeModule(
                        AutomationConstants.CONFIG_PROPERTY_TAX_EMPLOYEE
                );

                break;


            // =====================================================
            // PGR
            // =====================================================

            case AutomationConstants.MODULE_PGR:

                result = engine.executeModule(
                        AutomationConstants.CONFIG_PGR_CITIZEN
                );

                break;

            case AutomationConstants.MODULE_PGR_EMP:

                result = engine.executeModule(
                        AutomationConstants.CONFIG_PGR_EMPLOYEE
                );

                break;

            // =====================================================
            //  REQUEST SERVICE
            // =====================================================

            case AutomationConstants.MODULE_MOBILE_TOILET_CITIZEN:

                result = engine.executeModule(
                        AutomationConstants.CONFIG_MOBILE_TOILET_CITIZEN
                );

                break;

            case AutomationConstants.MODULE_MOBILE_TOILET_EMP:

                result = engine.executeModule(
                        AutomationConstants.CONFIG_MOBILE_TOILET_EMPLOYEE
                );

                break;
            case AutomationConstants.MODULE_MOBILE_TOILET_VENDOR:

                result = engine.executeModule(
                        AutomationConstants.CONFIG_MOBILE_TOILET_VENDOR
                );

                break;

            case AutomationConstants.MODULE_TREE_PRUNING_CITIZEN:

                result = engine.executeModule(
                        AutomationConstants.CONFIG_TREE_PRUNING_CITIZEN
                );

                break;

            case AutomationConstants.MODULE_TREE_PRUNING_EMP:

                result = engine.executeModule(
                        AutomationConstants.CONFIG_TREE_PRUNING_EMPLOYEE
                );

                break;

            case AutomationConstants.MODULE_TREE_PRUNING_VENDOR:

                result = engine.executeModule(
                        AutomationConstants.CONFIG_TREE_PRUNING_VENDOR
                );

                break;

            case AutomationConstants.MODULE_WATER_TANKER_CITIZEN:

                result = engine.executeModule(
                        AutomationConstants.CONFIG_WATER_TANKER_CITIZEN
                );

                break;

            case AutomationConstants.MODULE_WATER_TANKER_EMP:

                result = engine.executeModule(
                        AutomationConstants.CONFIG_WATER_TANKER_EMPLOYEE
                );

                break;

            case AutomationConstants.MODULE_WATER_TANKER_VENDOR:

                result = engine.executeModule(
                        AutomationConstants.CONFIG_WATER_TANKER_VENDOR
                );

                break;


            // =====================================================
            // STREET VENDING
            // =====================================================

            case AutomationConstants.MODULE_STREET_VENDING:

                result = engine.executeModule(
                        AutomationConstants.CONFIG_STREET_VENDING_CITIZEN
                );

                break;

            case AutomationConstants.MODULE_STREET_VENDING_EMP:

                result = engine.executeModule(
                        AutomationConstants.CONFIG_STREET_VENDING_EMPLOYEE
                );

                break;


            // =====================================================
            // TRADE LICENSE
            // =====================================================

            case AutomationConstants.MODULE_TRADE_LICENSE:

                result = engine.executeModule(
                        AutomationConstants.CONFIG_TRADE_LICENSE_CITIZEN
                );

                break;

            case AutomationConstants.MODULE_TRADE_LICENSE_EMP:

                result = engine.executeModule(
                        AutomationConstants.CONFIG_TRADE_LICENSE_EMPLOYEE
                );

                break;


            // =====================================================
            // WATER + SEWERAGE
            // =====================================================

            case AutomationConstants.MODULE_WATER_AND_SEWERAGE:

                result = engine.executeModule(
                        AutomationConstants.CONFIG_WATER_AND_SEWERAGE_CITIZEN
                );

                break;

            case AutomationConstants.MODULE_WATER_EMP:

                result = engine.executeModule(
                        AutomationConstants.CONFIG_WATER_EMPLOYEE
                );

                break;

            case AutomationConstants.MODULE_SEWERAGE_EMP:

                result = engine.executeModule(
                        AutomationConstants.CONFIG_SEWERAGE_EMPLOYEE
                );

                break;

            // =====================================================
            // GARBAGE COLLECTION
            // =====================================================


            case AutomationConstants.MODULE_GC_CITIZEN:
                result = engine.executeModule(AutomationConstants.CONFIG_GARBAGE_COLLECTION_CITIZEN);
                break;

            case AutomationConstants.MODULE_GC_EMPLOYEE:
                result = engine.executeModule(AutomationConstants.CONFIG_GARBAGE_COLLECTION_EMPLOYEE);
                break;


            // =====================================================
            // ESTATE MANAGEMENT
            // =====================================================


            case AutomationConstants.MODULE_ESTATE_MANAGEMENT_CITIZEN:
                result = engine.executeModule(AutomationConstants.CONFIG_ESTATE_MANAGEMENT_CITIZEN);
                break;

            case AutomationConstants.MODULE_ESTATE_MANAGEMENT_EMPLOYEE:
                result = engine.executeModule(AutomationConstants.CONFIG_ESTATE_MANAGEMENT_EMPLOYEE);
                break;


            // =====================================================
            // CHALLAN GENERATION
            // =====================================================


            case AutomationConstants.MODULE_CHALLAN_GENERATION_CITIZEN:
                result = engine.executeModule(AutomationConstants.CONFIG_CHALLAN_CITIZEN);
                break;

            case AutomationConstants.MODULE_CHALLAN_GENERATION_EMPLOYEE:
                result = engine.executeModule(AutomationConstants.CONFIG_CHALLAN_EMPLOYEE);
                break;

            // =====================================================
            // NO DUE CERTIFICATE
            // =====================================================


            case AutomationConstants.MODULE_NO_DUE_CERTIFICATE_CITIZEN:
                result = engine.executeModule(AutomationConstants.CONFIG_NDC_CITIZEN);
                break;

            case AutomationConstants.MODULE_NO_DUE_CERTIFICATE_EMPLOYEE:
                result = engine.executeModule(AutomationConstants.CONFIG_NDC_EMPLOYEE);
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