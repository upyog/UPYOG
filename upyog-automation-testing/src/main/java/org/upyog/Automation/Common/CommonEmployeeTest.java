package org.upyog.Automation.Common;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.upyog.Automation.Base.BaseTest;
import org.upyog.Automation.Modules.TradeLicense.InboxEmpTl;
import org.upyog.Automation.Utils.AutomationConstants;
import org.upyog.Automation.Utils.CommonModuleExecutor;
import org.upyog.Automation.Utils.DriverFactory;
import org.upyog.Automation.Utils.ModuleWrapper;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Common entry point for all UPYOG employee module tests.
 *
 * <p>This class maps employee module identifiers to their corresponding JSON configuration
 * files and executes them using {@link CommonModuleExecutor}, eliminating duplicate module code.</p>
 */
@Component
public class CommonEmployeeTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(CommonEmployeeTest.class);

    /**
     * Immutable mapping of employee module identifiers to their respective JSON configuration file paths.
     */
    private static final Map<String, String> EMPLOYEE_MODULE_CONFIG_MAP;

    static {
        Map<String, String> map = new HashMap<>();

        // Street Vending
        map.put(AutomationConstants.MODULE_STREET_VENDING, AutomationConstants.CONFIG_STREET_VENDING_EMPLOYEE);
        map.put(AutomationConstants.MODULE_SV, AutomationConstants.CONFIG_STREET_VENDING_EMPLOYEE);
        map.put(AutomationConstants.MODULE_STREET_VENDING_EMP, AutomationConstants.CONFIG_STREET_VENDING_EMPLOYEE);

        // Pet Registration
        map.put(AutomationConstants.MODULE_PET_REGISTRATION, AutomationConstants.CONFIG_PET_EMPLOYEE);
        map.put(AutomationConstants.MODULE_PET, AutomationConstants.CONFIG_PET_EMPLOYEE);
        map.put(AutomationConstants.MODULE_PET_EMP, AutomationConstants.CONFIG_PET_EMPLOYEE);
        map.put(AutomationConstants.MODULE_PET_CEMP, AutomationConstants.CONFIG_PET_CEMP);

        // Trade License
        map.put(AutomationConstants.MODULE_TRADE_LICENSE, AutomationConstants.CONFIG_TRADE_LICENSE_EMPLOYEE);
        map.put(AutomationConstants.MODULE_TL, AutomationConstants.CONFIG_TRADE_LICENSE_EMPLOYEE);
        map.put(AutomationConstants.MODULE_TRADE_LICENSE_EMP, AutomationConstants.CONFIG_TRADE_LICENSE_EMPLOYEE);

        // Asset Management
        map.put(AutomationConstants.MODULE_ASSET_MANAGEMENT, AutomationConstants.CONFIG_ASSET_EMPLOYEE);
        map.put(AutomationConstants.MODULE_ASSET_MANAGEMENT_SYSTEM, AutomationConstants.CONFIG_ASSET_EMPLOYEE);
        map.put(AutomationConstants.MODULE_ASSET_EMPLOYEE, AutomationConstants.CONFIG_ASSET_EMPLOYEE);
        map.put(AutomationConstants.MODULE_ASSET_VERIFIER, AutomationConstants.CONFIG_ASSET_EMPLOYEE_VERIFIER);
        map.put(AutomationConstants.MODULE_ASSET_APPROVER, AutomationConstants.CONFIG_ASSET_EMPLOYEE_APPROVER);

        // Advertisement
        map.put(AutomationConstants.MODULE_ADVERTISEMENT, AutomationConstants.CONFIG_ADVERTISEMENT_EMPLOYEE);
        map.put(AutomationConstants.MODULE_ADV, AutomationConstants.CONFIG_ADVERTISEMENT_EMPLOYEE);
        map.put(AutomationConstants.MODULE_ADVERTISEMENT_EMPLOYEE, AutomationConstants.CONFIG_ADVERTISEMENT_EMPLOYEE);

        // Property Tax
        map.put(AutomationConstants.MODULE_PROPERTY_TAX, AutomationConstants.CONFIG_PROPERTY_TAX_EMPLOYEE);
        map.put(AutomationConstants.MODULE_PT, AutomationConstants.CONFIG_PROPERTY_TAX_EMPLOYEE);
        map.put(AutomationConstants.MODULE_PROPERTY_TAX_EMP, AutomationConstants.CONFIG_PROPERTY_TAX_EMPLOYEE);

        // E-Waste Management
        map.put(AutomationConstants.MODULE_EWASTE, AutomationConstants.CONFIG_EWASTE_EMPLOYEE);
        map.put(AutomationConstants.MODULE_EWASTE_MANAGEMENT_SYSTEM, AutomationConstants.CONFIG_EWASTE_EMPLOYEE);
        map.put(AutomationConstants.MODULE_EWASTE_EMPLOYEE, AutomationConstants.CONFIG_EWASTE_EMPLOYEE);

        // Desludging Services
        map.put(AutomationConstants.MODULE_DESLUDGING, AutomationConstants.CONFIG_DESLUDGING_EMPLOYEE);
        map.put(AutomationConstants.MODULE_DESLUDGING_EMP_UPDATE, AutomationConstants.CONFIG_DESLUDGING_EMPLOYEE_UPDATE);
        map.put(AutomationConstants.MODULE_DESLUDGING_EMPLOYEE_UPDATE, AutomationConstants.CONFIG_DESLUDGING_EMPLOYEE_UPDATE);
        map.put(AutomationConstants.MODULE_DESLUDGING_EMP_COMPLETE, AutomationConstants.CONFIG_DESLUDGING_EMPLOYEE_COMPLETE);
        map.put(AutomationConstants.MODULE_DESLUDGING_EMP_PSSO, AutomationConstants.CONFIG_DESLUDGING_EMPLOYEE_PSSO);
        map.put(AutomationConstants.MODULE_DESLUDGING_EMP_FSTPO, AutomationConstants.CONFIG_DESLUDGING_EMPLOYEE_FSTPO);

        // Online Building Plan Approval System
        map.put(AutomationConstants.MODULE_OBPAS, AutomationConstants.CONFIG_OBPAS_EMPLOYEE);
        map.put(AutomationConstants.MODULE_OBPAS_OC, AutomationConstants.CONFIG_OBPAS_OC_EMPLOYEE);
        map.put(AutomationConstants.MODULE_ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM, AutomationConstants.CONFIG_OBPAS_EMPLOYEE);
        map.put(AutomationConstants.MODULE_ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM_EMPLOYEE, AutomationConstants.CONFIG_OBPAS_EMPLOYEE);

        // Request Services
        map.put(AutomationConstants.MODULE_WATER_TANKER, AutomationConstants.CONFIG_WATER_TANKER_EMPLOYEE);
        map.put(AutomationConstants.MODULE_WATER_TANKER_EMP, AutomationConstants.CONFIG_WATER_TANKER_EMPLOYEE);
        map.put(AutomationConstants.MODULE_WATER_TANKER_VENDOR, AutomationConstants.CONFIG_WATER_TANKER_VENDOR);
        map.put(AutomationConstants.MODULE_TREE_PRUNING, AutomationConstants.CONFIG_TREE_PRUNING_EMPLOYEE);
        map.put(AutomationConstants.MODULE_TREE_PRUNING_EMP, AutomationConstants.CONFIG_TREE_PRUNING_EMPLOYEE);
        map.put(AutomationConstants.MODULE_TREE_PRUNING_VERIFIER, AutomationConstants.CONFIG_TREE_PRUNING_VERIFIER);
        map.put(AutomationConstants.MODULE_TREE_PRUNING_VENDOR, AutomationConstants.CONFIG_TREE_PRUNING_VENDOR);
        map.put(AutomationConstants.MODULE_MOBILE_TOILET, AutomationConstants.CONFIG_MOBILE_TOILET_EMPLOYEE);
        map.put(AutomationConstants.MODULE_MOBILE_TOILET_EMP, AutomationConstants.CONFIG_MOBILE_TOILET_EMPLOYEE);
        map.put(AutomationConstants.MODULE_MOBILE_TOILET_VENDOR, AutomationConstants.CONFIG_MOBILE_TOILET_VENDOR);

        // Community Hall Booking
        map.put(AutomationConstants.MODULE_CHB, AutomationConstants.CONFIG_CHB_EMPLOYEE);
        map.put(AutomationConstants.MODULE_COMMUNITY_HALL_BOOKING, AutomationConstants.CONFIG_CHB_EMPLOYEE);
        map.put(AutomationConstants.MODULE_CHB_EMPLOYEE, AutomationConstants.CONFIG_CHB_EMPLOYEE);

        // Construction and Demolition
        map.put(AutomationConstants.MODULE_CND, AutomationConstants.CONFIG_CND_EMPLOYEE);
        map.put(AutomationConstants.MODULE_CONSTRUCTION_AND_DEMOLITION, AutomationConstants.CONFIG_CND_EMPLOYEE);
        map.put(AutomationConstants.MODULE_CND_EMP, AutomationConstants.CONFIG_CND_EMPLOYEE);
        map.put(AutomationConstants.MODULE_CND_EMPLOYEE, AutomationConstants.CONFIG_CND_EMPLOYEE);
        map.put(AutomationConstants.MODULE_CND_VENDOR, AutomationConstants.CONFIG_CND_VENDOR);

        // Public Grievance Redressal
        map.put(AutomationConstants.MODULE_PUBLIC_GRIEVANCE_REDRESSAL, AutomationConstants.CONFIG_PGR_EMPLOYEE);
        map.put(AutomationConstants.MODULE_PGR, AutomationConstants.CONFIG_PGR_EMPLOYEE);
        map.put(AutomationConstants.MODULE_PGR_EMP, AutomationConstants.CONFIG_PGR_EMPLOYEE);

        // Water and Sewerage
        map.put(AutomationConstants.MODULE_SEWERAGE_EMP, AutomationConstants.CONFIG_SEWERAGE_EMPLOYEE);
        map.put(AutomationConstants.MODULE_SEWERAGE, AutomationConstants.CONFIG_SEWERAGE_EMPLOYEE);
        map.put(AutomationConstants.MODULE_WATER_EMP, AutomationConstants.CONFIG_WATER_EMPLOYEE);
        map.put(AutomationConstants.MODULE_WATER, AutomationConstants.CONFIG_WATER_EMPLOYEE);
        map.put(AutomationConstants.MODULE_WATER_AND_SEWERAGE, AutomationConstants.CONFIG_WATER_EMPLOYEE);
        map.put(AutomationConstants.MODULE_WS, AutomationConstants.CONFIG_WATER_EMPLOYEE);

        // Garbage Collection
        map.put(AutomationConstants.MODULE_GARBAGE_COLLECTION, AutomationConstants.CONFIG_GARBAGE_COLLECTION_EMPLOYEE);
        map.put(AutomationConstants.MODULE_GC_EMPLOYEE, AutomationConstants.CONFIG_GARBAGE_COLLECTION_EMPLOYEE);

        // Estate Management
        map.put(AutomationConstants.MODULE_ESTATE_MANAGEMENT, AutomationConstants.CONFIG_ESTATE_MANAGEMENT_EMPLOYEE);
        map.put(AutomationConstants.MODULE_ESTATE_MANAGEMENT_EMPLOYEE, AutomationConstants.CONFIG_ESTATE_MANAGEMENT_EMPLOYEE);

        // Challan Generation
        map.put(AutomationConstants.MODULE_CHALLAN_GENERATION, AutomationConstants.CONFIG_CHALLAN_EMPLOYEE);

        // No Due Certificate
        map.put(AutomationConstants.MODULE_NO_DUE_CERTIFICATE, AutomationConstants.CONFIG_NDC_EMPLOYEE);
        map.put(AutomationConstants.MODULE_NDC, AutomationConstants.CONFIG_NDC_EMPLOYEE);
        map.put(AutomationConstants.MODULE_NO_DUE_CERTIFICATE_EMPLOYEE, AutomationConstants.CONFIG_NDC_EMPLOYEE);

        EMPLOYEE_MODULE_CONFIG_MAP = Collections.unmodifiableMap(map);
    }

    @Autowired
    private CommonModuleExecutor commonModuleExecutor;

    @Autowired(required = false)
    private InboxEmpTl inboxEmpTl;

    /**
     * Initializes the WebDriver instance for employee portal test execution.
     *
     * @param baseUrl the employee portal login URL
     */
    private void employeeSetUp(String baseUrl) {
        driver = DriverFactory.createChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        js = (JavascriptExecutor) driver;

        // Navigate to employee login URL
        driver.get(baseUrl);
    }

    /**
     * Executes an employee module test workflow.
     *
     * @param baseUrl the employee portal login base URL
     * @param moduleName the name of the employee module to execute
     * @param username employee login username
     * @param password employee login password
     * @param applicationNumber target application number for inbox search/actions
     */
    public void runEmployeeTest(String baseUrl,
                                String moduleName,
                                String username,
                                String password,
                                String applicationNumber) {

        // Initialize employee session
        employeeSetUp(baseUrl);
        logger.info("Starting {} employee test", moduleName);

        try {
            // Handle custom workflow if specific module requires legacy handler
            if ("TRADE_LICENSE1".equalsIgnoreCase(moduleName) && inboxEmpTl != null) {
                inboxEmpTl.inboxEmpTl(baseUrl, username, password, applicationNumber);
            } else {
                // Look up JSON configuration path
                String configPath = EMPLOYEE_MODULE_CONFIG_MAP.get(moduleName.toUpperCase());
                if (configPath == null) {
                    logger.error("Unknown employee module: {}", moduleName);
                    throw new RuntimeException("Unknown module: " + moduleName);
                }

                // Execute employee workflow via CommonModuleExecutor
                ModuleWrapper.execute(
                        moduleName.toUpperCase(),
                        () -> commonModuleExecutor.execute(driver, wait, js, configPath)
                );
            }

            logger.info("{} employee test completed successfully", moduleName);

        } catch (Exception e) {
            logger.error("Error in {} employee test: {}", moduleName, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}