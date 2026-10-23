package org.upyog.Automation.Common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.upyog.Automation.Base.BaseTest;
import org.upyog.Automation.Utils.AutomationConstants;
import org.upyog.Automation.Utils.CommonModuleExecutor;
import org.upyog.Automation.Utils.ModuleWrapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Common entry point for all UPYOG vendor module tests.
 *
 * <p>Routes vendor test workflows to their respective JSON configurations
 * executed through {@link CommonModuleExecutor}.</p>
 */
@Component
public class CommonVendorTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(CommonVendorTest.class);

    private static final Map<String, String> VENDOR_MODULE_CONFIG_MAP;

    static {
        Map<String, String> map = new HashMap<>();
        map.put(AutomationConstants.MODULE_WATER_TANKER, AutomationConstants.CONFIG_WATER_TANKER_VENDOR);
        map.put(AutomationConstants.MODULE_MOBILE_TOILET, AutomationConstants.CONFIG_MOBILE_TOILET_VENDOR);
        map.put(AutomationConstants.MODULE_CND, AutomationConstants.CONFIG_CND_VENDOR);
        map.put(AutomationConstants.MODULE_CONSTRUCTION_AND_DEMOLITION, AutomationConstants.CONFIG_CND_VENDOR);
        map.put(AutomationConstants.MODULE_CND_VENDOR, AutomationConstants.CONFIG_CND_VENDOR);

        VENDOR_MODULE_CONFIG_MAP = Collections.unmodifiableMap(map);
    }

    @Autowired
    private CommonModuleExecutor commonModuleExecutor;

    /**
     * Executes a vendor module test workflow.
     *
     * @param baseUrl the vendor portal login base URL
     * @param moduleName the name of the vendor module to execute
     * @param mobileNumber vendor mobile number for authentication
     * @param otp one-time password for authentication
     * @param city selected city name
     * @param applicationNumber application identifier
     * @throws InterruptedException if thread sleep is interrupted
     */
    public void runVendorTest(
            String baseUrl,
            String moduleName,
            String mobileNumber,
            String otp,
            String city,
            String applicationNumber)
            throws InterruptedException {

        // Initialize browser and authentication session
        setUp();
        logger.info("Driver initialized: {}", driver);
        logger.info("Starting {} vendor test", moduleName);

        try {
            String configPath = VENDOR_MODULE_CONFIG_MAP.get(moduleName.toUpperCase());
            if (configPath == null) {
                logger.error("Unknown vendor module: {}", moduleName);
                throw new RuntimeException("Unknown vendor module: " + moduleName);
            }

            // Execute vendor workflow using CommonModuleExecutor
            ModuleWrapper.execute(
                    moduleName.toUpperCase(),
                    () -> commonModuleExecutor.execute(driver, wait, js, configPath)
            );

            logger.info("{} vendor test completed successfully", moduleName);

        } catch (Exception e) {
            logger.error("Error in {} vendor test: {}", moduleName, e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            // Teardown driver
            tearDown();
        }
    }
}
