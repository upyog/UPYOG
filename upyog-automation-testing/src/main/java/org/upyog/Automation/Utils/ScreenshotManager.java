package org.upyog.Automation.Utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class responsible for capturing and storing failure screenshots and filled screen snapshots during test execution.
 *
 * <p>When a step or assertion fails during Selenium automation, this manager captures
 * the current browser screen, sanitizes the file name based on module name, test case,
 * and failed step, and saves the image to the designated screenshot directory (default: {@code target/screenshots}).</p>
 */
public class ScreenshotManager {

    private static final Logger logger = LoggerFactory.getLogger(ScreenshotManager.class);

    /**
     * Target directory where failure screenshots are saved.
     */
    private static final String SCREENSHOT_DIR = AutomationConstants.SCREENSHOTS_DIR;

    /**
     * Captures the current screen as a Base64-encoded string for inline embedding in HTML reports.
     *
     * @param driver the active {@link WebDriver} instance
     * @return the Base64 image string, or an empty string if capture failed
     */
    public static String captureBase64(WebDriver driver) {
        try {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
        } catch (Exception e) {
            logger.error("Unable to capture Base64 screenshot: {}", e.getMessage());
            return "";
        }
    }

    /**
     * Captures a screenshot of the filled screen during normal test execution and saves it to disk.
     *
     * @param driver the active {@link WebDriver} instance
     * @param moduleName the name of the module currently executing
     * @param testCase the name or identifier of the test case
     * @param screenName the name or description of the screen/step
     * @return the file path of the saved screenshot, or an empty string if capture failed
     */
    public static String captureScreenScreenshot(
            WebDriver driver,
            String moduleName,
            String testCase,
            String screenName
    ) {

        try {

            Path directory = Paths.get(SCREENSHOT_DIR);

            // Create screenshot directory if it does not exist
            Files.createDirectories(directory);

            String safeModule = sanitize(moduleName);
            String safeTestCase = sanitize(testCase);
            String safeScreen = sanitize(screenName);
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());

            // Construct sanitized screenshot file name with structured delimiters
            String fileName =
                    safeModule + "__"
                            + safeTestCase + "__"
                            + safeScreen + "__"
                            + timestamp + ".png";

            File screenshot =
                    ((TakesScreenshot) driver)
                            .getScreenshotAs(OutputType.FILE);

            Path destination =
                    directory.resolve(fileName);

            // Copy captured screenshot file to the target destination
            Files.copy(
                    screenshot.toPath(),
                    destination
            );

            logger.info("Screen screenshot saved: {}", destination);

            return destination.toString();

        } catch (Exception e) {

            logger.error("Unable to save screen screenshot: {}", e.getMessage(), e);

            return "";
        }
    }

    /**
     * Captures a screenshot of the current browser state upon test failure and saves it to disk.
     *
     * @param driver the active {@link WebDriver} instance
     * @param moduleName the name of the module currently executing (e.g., "TL", "PT", "WS")
     * @param testCase the name or identifier of the test case
     * @param failedStep the name or description of the step where failure occurred
     * @return the absolute or relative file path of the saved screenshot, or an empty string if capture failed
     */
    public static String captureFailureScreenshot(
            WebDriver driver,
            String moduleName,
            String testCase,
            String failedStep
    ) {

        try {

            Path directory = Paths.get(SCREENSHOT_DIR);

            // Create screenshot directory if it does not exist
            Files.createDirectories(directory);

            String safeModule = sanitize(moduleName);
            String safeTestCase = sanitize(testCase);
            String safeStep = sanitize(failedStep);
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());

            // Construct sanitized unique screenshot file name
            String fileName =
                    safeModule + "__"
                            + safeTestCase + "__"
                            + safeStep + "__FAIL__"
                            + timestamp + ".png";

            File screenshot =
                    ((TakesScreenshot) driver)
                            .getScreenshotAs(OutputType.FILE);

            Path destination =
                    directory.resolve(fileName);

            // Copy captured screenshot file to the target destination
            Files.copy(
                    screenshot.toPath(),
                    destination
            );

            logger.info("Failure screenshot saved: {}", destination);

            return destination.toString();

        } catch (IOException e) {

            logger.error("Unable to save failure screenshot: {}", e.getMessage(), e);

            return "";
        }
    }

    /**
     * Sanitizes a string to make it safe for use as a file name.
     *
     * <p>Replaces all characters that are not alphanumeric, dot, underscore, or hyphen with underscores.</p>
     *
     * @param value the string value to sanitize
     * @return the sanitized string safe for file naming, or "UNKNOWN" if null/empty
     */
    public static String sanitize(String value) {

        if (value == null || value.trim().isEmpty()) {
            return "UNKNOWN";
        }

        return value
                .trim()
                .replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    /**
     * Generates a step-by-step User Manual HTML document for the given module.
     *
     * @param moduleName the target module name or "ALL"
     * @return the generated HTML file
     * @throws IOException if generation fails
     */
    public static File generateUserManual(String moduleName) throws IOException {
        return org.upyog.Automation.Reports.UserManualGenerator.generateHtmlManual(moduleName);
    }
}

