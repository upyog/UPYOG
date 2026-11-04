package org.upyog.Automation.Common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.upyog.Automation.Base.BaseTest;
import org.upyog.Automation.Utils.AutomationConstants;
import org.upyog.Automation.Utils.CommonModuleExecutor;
import org.upyog.Automation.Utils.ModuleTask;
import org.upyog.Automation.Utils.ModuleWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Common entry point for all UPYOG citizen module tests.
 *
 * <p>This class routes incoming test requests to their corresponding JSON configuration files
 * and executes them via {@link CommonModuleExecutor}, eliminating the need for individual
 * duplicate module wrapper classes.</p>
 */
@Component
public class CommonCitizenTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(CommonCitizenTest.class);

    /**
     * Immutable mapping of citizen module identifiers to their respective JSON configuration file paths.
     */
    private static final Map<String, String> MODULE_CONFIG_MAP;

    static {
        Map<String, String> map = new HashMap<>();

        // Street Vending
        map.put(AutomationConstants.MODULE_STREET_VENDING, AutomationConstants.CONFIG_STREET_VENDING_CITIZEN);
        map.put(AutomationConstants.MODULE_SV, AutomationConstants.CONFIG_STREET_VENDING_CITIZEN);

        // Trade License
        map.put(AutomationConstants.MODULE_TRADE_LICENSE, AutomationConstants.CONFIG_TRADE_LICENSE_CITIZEN);
        map.put(AutomationConstants.MODULE_TL, AutomationConstants.CONFIG_TRADE_LICENSE_CITIZEN);

        // Pet Registration
        map.put(AutomationConstants.MODULE_PET_REGISTRATION, AutomationConstants.CONFIG_PET_CITIZEN);
        map.put(AutomationConstants.MODULE_PET, AutomationConstants.CONFIG_PET_CITIZEN);
        map.put(AutomationConstants.MODULE_PET_CEMP, AutomationConstants.CONFIG_PET_CEMP);

        // Advertisement
        map.put(AutomationConstants.MODULE_ADVERTISEMENT, AutomationConstants.CONFIG_ADVERTISEMENT_CITIZEN);
        map.put(AutomationConstants.MODULE_ADV, AutomationConstants.CONFIG_ADVERTISEMENT_CITIZEN);

        // Request Services
        map.put(AutomationConstants.MODULE_TREE_PRUNING, AutomationConstants.CONFIG_TREE_PRUNING_CITIZEN);
        map.put(AutomationConstants.MODULE_WATER_TANKER, AutomationConstants.CONFIG_WATER_TANKER_CITIZEN);
        map.put(AutomationConstants.MODULE_MOBILE_TOILET, AutomationConstants.CONFIG_MOBILE_TOILET_CITIZEN);

        // Property Tax
        map.put(AutomationConstants.MODULE_PROPERTY_TAX, AutomationConstants.CONFIG_PROPERTY_TAX_CITIZEN);
        map.put(AutomationConstants.MODULE_PT, AutomationConstants.CONFIG_PROPERTY_TAX_CITIZEN);

        // Public Grievance Redressal
        map.put(AutomationConstants.MODULE_PUBLIC_GRIEVANCE_REDRESSAL, AutomationConstants.CONFIG_PGR_CITIZEN);
        map.put(AutomationConstants.MODULE_PGR, AutomationConstants.CONFIG_PGR_CITIZEN);

        // Online Building Plan Approval System
        map.put(AutomationConstants.MODULE_OBPAS, AutomationConstants.CONFIG_OBPAS_CITIZEN);
        map.put(AutomationConstants.MODULE_OBPAS_OC, AutomationConstants.CONFIG_OBPAS_OC_CITIZEN);
        map.put(AutomationConstants.MODULE_ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM, AutomationConstants.CONFIG_OBPAS_CITIZEN);

        // E-Waste Management System
        map.put(AutomationConstants.MODULE_EWASTE, AutomationConstants.CONFIG_EWASTE_CITIZEN);
        map.put(AutomationConstants.MODULE_EWASTE_MANAGEMENT_SYSTEM, AutomationConstants.CONFIG_EWASTE_CITIZEN);

        // Community Hall Booking
        map.put(AutomationConstants.MODULE_CHB, AutomationConstants.CONFIG_CHB_CITIZEN);
        map.put(AutomationConstants.MODULE_COMMUNITY_HALL_BOOKING, AutomationConstants.CONFIG_CHB_CITIZEN);

        // Construction and Demolition
        map.put(AutomationConstants.MODULE_CND, AutomationConstants.CONFIG_CND_CITIZEN);
        map.put(AutomationConstants.MODULE_CONSTRUCTION_AND_DEMOLITION, AutomationConstants.CONFIG_CND_CITIZEN);

        // Desludging Services
        map.put(AutomationConstants.MODULE_DESLUDGING, AutomationConstants.CONFIG_DESLUDGING_CITIZEN);
        map.put(AutomationConstants.MODULE_DESLUDGING_SERVICE, AutomationConstants.CONFIG_DESLUDGING_CITIZEN);
        map.put(AutomationConstants.MODULE_DESLUDGING_PAYMENT, AutomationConstants.CONFIG_DESLUDGING_CITIZEN_PAYMENT);
        map.put(AutomationConstants.MODULE_DESLUDGING_SERVICE_PAYMENT, AutomationConstants.CONFIG_DESLUDGING_CITIZEN_PAYMENT);
        map.put(AutomationConstants.MODULE_DESLUDGING_PAYMENT2, AutomationConstants.CONFIG_DESLUDGING_CITIZEN_PAYMENT2);
        map.put(AutomationConstants.MODULE_DESLUDGING_SERVICE_PAYMENT2, AutomationConstants.CONFIG_DESLUDGING_CITIZEN_PAYMENT2);

        // Water and Sewerage
        map.put(AutomationConstants.MODULE_WATER_AND_SEWERAGE, AutomationConstants.CONFIG_WATER_AND_SEWERAGE_CITIZEN);
        map.put(AutomationConstants.MODULE_WS, AutomationConstants.CONFIG_WATER_AND_SEWERAGE_CITIZEN);

        // Garbage Collection
        map.put(AutomationConstants.MODULE_GARBAGE_COLLECTION, AutomationConstants.CONFIG_GARBAGE_COLLECTION_CITIZEN);
        map.put(AutomationConstants.MODULE_GARBAGE_COLLECTION_PAYMENT, AutomationConstants.CONFIG_GARBAGE_COLLECTION_PAYMENT);

        // Estate Management
        map.put(AutomationConstants.MODULE_ESTATE_MANAGEMENT, AutomationConstants.CONFIG_ESTATE_MANAGEMENT_CITIZEN);

        // No Due Certificate
        map.put(AutomationConstants.MODULE_NO_DUE_CERTIFICATE, AutomationConstants.CONFIG_NDC_CITIZEN);
        map.put(AutomationConstants.MODULE_NDC, AutomationConstants.CONFIG_NDC_CITIZEN);

        MODULE_CONFIG_MAP = Collections.unmodifiableMap(map);
    }

    @Autowired
    private CommonModuleExecutor commonModuleExecutor;

    /**
     * Executes a single citizen module test flow.
     *
     * @param baseUrl the citizen portal login base URL
     * @param moduleName the name of the module to execute (e.g. "PET_REGISTRATION", "PROPERTY_TAX")
     * @param mobileNumber citizen mobile number for authentication
     * @param otp one-time password for citizen authentication
     * @param cityName selected city/municipality name
     * @param permitNumber permit number (applicable for OBPAS / specific modules)
     * @throws InterruptedException if browser automation thread sleep is interrupted
     */
    public void runCitizenTest(String baseUrl,
                               String moduleName,
                               String mobileNumber,
                               String otp,
                               String cityName,
                               String permitNumber) throws InterruptedException {

        // Initialize browser and login session
        setUp();
        logger.info("Starting {} citizen test", moduleName);

        try {
            // Resolve the JSON module configuration path
            String configPath = MODULE_CONFIG_MAP.get(moduleName.toUpperCase());
            if (configPath == null) {
                logger.error("Unknown module: {}", moduleName);
                throw new RuntimeException("Unknown module: " + moduleName);
            }

            // Execute test through CommonModuleExecutor
            ModuleWrapper.execute(
                    moduleName.toUpperCase(),
                    () -> commonModuleExecutor.execute(driver, wait, js, configPath)
            );

            logger.info("{} test completed successfully", moduleName);

        } catch (Exception e) {
            logger.error("Error in {} citizen test: {}", moduleName, e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            // Ensure browser driver is closed and resources are released
            tearDown();
        }
    }

    /**
     * Executes multiple citizen module test flows sequentially in a single session.
     *
     * @param baseUrl the citizen portal login base URL
     * @param selectedModules list of module names to execute sequentially
     * @param mobileNumber citizen mobile number for authentication
     * @param otp one-time password for citizen authentication
     * @param cityName selected city/municipality name
     * @param permitNumber permit number (applicable for OBPAS / specific modules)
     * @throws InterruptedException if browser automation thread sleep is interrupted
     */
    public void runMultipleModules(String baseUrl,
                                   List<String> selectedModules,
                                   String mobileNumber,
                                   String otp,
                                   String cityName,
                                   String permitNumber) throws InterruptedException {

        // Initialize browser and login session
        setUp();
        logger.info("Starting multiple citizen modules execution: {}", selectedModules);

        try {
            List<ModuleTask> modules = new ArrayList<>();

            // Build task list for each selected module
            for (String moduleName : selectedModules) {
                String configPath = MODULE_CONFIG_MAP.get(moduleName.toUpperCase());
                if (configPath != null) {
                    modules.add(
                            new ModuleTask(
                                    moduleName.toUpperCase(),
                                    () -> commonModuleExecutor.execute(driver, wait, js, configPath)
                            )
                    );
                } else {
                    logger.warn("Skipping unknown module: {}", moduleName);
                }
            }

            // Execute tasks in batch
            ModuleWrapper.executeBatch(modules);
            logger.info("Multiple citizen modules batch execution completed.");

        } finally {
            // Ensure browser driver is closed and resources are released
            tearDown();
        }
    }
}