package org.upyog.Automation.engine;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.upyog.Automation.Utils.WorkflowDataStore;
import org.upyog.Automation.model.TestInstruction;
import org.upyog.Automation.model.TestModule;
import org.upyog.Automation.Utils.JsonConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

/**
 * Main Test Engine
 *
 * Orchestrates test execution by:
 * 1. Loading module configuration from JSON
 * 2. Initializing WebDriver and action executor
 * 3. Executing instructions sequentially
 * 4. Handling failures and cleanup
 *
 * The engine is completely decoupled from specific test logic—all steps
 * are defined in JSON configuration files.
 */
public class TestEngine {

    private static final Logger logger = LoggerFactory.getLogger(TestEngine.class);
    private static final int DEFAULT_TIMEOUT_SECONDS = 30;

    private final JsonConfigLoader configLoader;
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final ActionExecutor actionExecutor;

    /**
     * Creates a new TestEngine instance.
     *
     * @param driver WebDriver instance (Chrome, Firefox, etc.)
     * @param propertiesPath Path to properties file for placeholder resolution
     */
    public TestEngine(WebDriver driver, String propertiesPath) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS));
        this.configLoader = new JsonConfigLoader(propertiesPath);
        this.actionExecutor = new ActionExecutor(driver, wait);
    }

    /**
     * Executes a complete test module.
     *
     * @param modulePath Path to JSON config file (e.g., "test-config/ewaste/ewaste_citizen_module.json")
     * @return ExecutionResult with pass/fail status and details
     */
    public ExecutionResult executeModule(String modulePath) {
        TestModule module = configLoader.loadModule(modulePath);

        logger.info("========================================");
        logger.info("Starting module: {}", module.getModuleName());
        logger.info("Description: {}", module.getDescription());
        logger.info("Steps to execute: {}", module.getInstructions().size());
        logger.info("========================================");

        // Navigate to base URL if specified
        String baseUrl = WorkflowDataStore.get("selected.url");
        String currentUrl = driver.getCurrentUrl();

        if (currentUrl == null || currentUrl.contains("login")) {
            driver.get(baseUrl);
            logger.info("Navigated to base URL: {}", baseUrl);
        } else {
            logger.info("Already logged in, skipping navigation");
        }
        List<TestInstruction> instructions = module.getInstructions();
        int totalSteps = instructions.size();
        int passedSteps = 0;
        int failedStep = -1;
        String failureReason = null;

        for (int i = 0; i < totalSteps; i++) {
            TestInstruction instruction = instructions.get(i);
            logger.info("Step [{}/{}]: {}", i + 1, totalSteps, instruction.getStepName());

            try {
                actionExecutor.execute(instruction);
                passedSteps++;

            } catch (Exception e) {
                failedStep = i + 1;
                failureReason = e.getMessage();
                logger.error("Step {} failed: {}", failedStep, failureReason);

                // Optionally continue to next step or halt execution
                // For now, we halt on first failure
                break;
            }
        }

        ExecutionResult result = new ExecutionResult(
                module.getModuleName(),
                totalSteps,
                passedSteps,
                failedStep,
                failureReason
        );


        logger.info("========================================================================================================================");
        logger.info("Module execution complete: {}", result.isSuccess() ? "PASSED" : "FAILED");
        logger.info("Steps passed: {}/{}", passedSteps, totalSteps);
        logger.info("========================================================================================================================");

        if (!result.isSuccess()) {
            throw new RuntimeException(
                    "Module Failed : "
                            + result.getFailureReason()
            );
        }

        return result;
    }

    /**
     * Result container for module execution.
     */
    public static class ExecutionResult {
        private final String moduleName;
        private final int totalSteps;
        private final int passedSteps;
        private final int failedAtStep;
        private final String failureReason;

        public ExecutionResult(String moduleName, int totalSteps, int passedSteps,
                               int failedAtStep, String failureReason) {
            this.moduleName = moduleName;
            this.totalSteps = totalSteps;
            this.passedSteps = passedSteps;
            this.failedAtStep = failedAtStep;
            this.failureReason = failureReason;
        }

        public boolean isSuccess() {
            return failedAtStep == -1;
        }

        public String getModuleName() {
            return moduleName;
        }

        public int getTotalSteps() {
            return totalSteps;
        }

        public int getPassedSteps() {
            return passedSteps;
        }

        public int getFailedAtStep() {
            return failedAtStep;
        }

        public String getFailureReason() {
            return failureReason;
        }


        @Override
        public String toString() {
            if (isSuccess()) {
                return String.format("Module '%s': PASSED (%d/%d steps)",
                        moduleName, passedSteps, totalSteps);
            } else {
                return String.format("Module '%s': FAILED at step %d (%s)",
                        moduleName, failedAtStep, failureReason);
            }
        }
    }
}
