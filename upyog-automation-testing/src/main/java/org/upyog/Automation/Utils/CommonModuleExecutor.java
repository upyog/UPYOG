package org.upyog.Automation.Utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.upyog.Automation.engine.TestEngine;

/**
 * Centralized common module executor for all UPYOG automated test flows.
 *
 * <p>Executes module JSON test configurations through {@link TestEngine},
 * providing a single unified entry point for all Citizen, Employee, and Vendor flows.</p>
 */
@Component
public class CommonModuleExecutor {

    private static final Logger logger = LoggerFactory.getLogger(CommonModuleExecutor.class);

    private static final String DEFAULT_CONFIG_PROPERTIES = "config/dev.properties";

    /**
     * Executes the specified JSON module test configuration.
     *
     * @param driver WebDriver instance used for browser automation
     * @param wait WebDriverWait instance for explicit element synchronization
     * @param js JavascriptExecutor instance for executing browser JavaScript actions
     * @param fileName relative classpath path of the module JSON configuration file
     */
    public void execute(
            WebDriver driver,
            WebDriverWait wait,
            JavascriptExecutor js,
            String fileName) {

        logger.info("Executing module configuration: [{}]", fileName);

        try {
            TestEngine engine = new TestEngine(driver, DEFAULT_CONFIG_PROPERTIES);
            engine.executeModule(fileName);
            logger.info("Successfully executed module configuration: [{}]", fileName);

        } catch (Exception e) {
            logger.error("Module execution failed for: [{}] - Error: {}", fileName, e.getMessage(), e);
            throw new RuntimeException("Module execution failed for: " + fileName, e);
        }
    }
}