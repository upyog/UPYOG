package org.upyog.Automation.engine;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.upyog.Automation.Reports.ReportManager;
import org.upyog.Automation.Utils.AutomationConstants;
import org.upyog.Automation.Utils.TestDataStore;
import org.upyog.Automation.Utils.WorkflowDataStore;
import org.upyog.Automation.model.TestInstruction;
import org.upyog.Automation.Utils.ScreenshotManager;
import org.upyog.Automation.Utils.ScreenRecorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * Action Execution Engine
 *
 * Executes actions defined in JSON configuration against web elements.
 * Uses Strategy Pattern via switch expression to handle different action types.
 *
 * Each action type maps to a specific Selenium operation:
 * - TYPE: Clear field and enter text
 * - CLICK: Standard element click
 * - CLICK_JS: JavaScript click (bypasses overlays)
 * - HOVER: Mouse hover action
 * - SELECT_DROPDOWN_BY_INDEX: Custom dropdown selection
 * - UPLOAD_FILE: File input handling
 * - WAIT_FOR_VISIBLE: Explicit wait for visibility
 * - CHECK_LAST_CHECKBOX: Select last checkbox in a group
 * - SET_DATE_TODAY: Set date input to current date
 *
 * The executor handles exceptions gracefully and provides detailed logging
 * for debugging failed steps.
 */
public class ActionExecutor {

    /**
     * Resolves an environment-specific value if piped syntax (value1||value2) is used.
     *
     * <p>If the input contains '||', the first segment is returned for NIUATT environment,
     * and the second segment is returned for other environments.</p>
     *
     * @param value the raw configuration string (may contain '||')
     * @return the resolved string according to the active environment
     */
    private String resolveByEnv(String value) {
        if (value == null || !value.contains("||")) {
            return value;
        }

        String env = WorkflowDataStore.get(AutomationConstants.KEY_SELECTED_ENV);
        String[] parts = value.split("\\|\\|");

        return AutomationConstants.ENV_NIUATT.equalsIgnoreCase(env)
                ? parts[0]
                : parts[1];
    }

    private static final Logger logger = LoggerFactory.getLogger(ActionExecutor.class);

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final JavascriptExecutor js;
    private final Actions actions;
    private final LocatorResolver locatorResolver;
    private String lastCapturedFingerprint = "";
    private long lastCapturedTime = 0;

    /**
     * Resets the screen capture fingerprint state at the start of a new module or test case.
     */
    public void resetAcknowledgementCaptureState() {
        this.lastCapturedFingerprint = "";
        this.lastCapturedTime = 0;
    }

    /**
     * Constructs a new {@link ActionExecutor} with the provided {@link WebDriver} and {@link WebDriverWait}.
     *
     * @param driver the Selenium WebDriver instance
     * @param wait the explicit WebDriverWait instance
     */
    public ActionExecutor(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
        this.js = (JavascriptExecutor) driver;
        this.actions = new Actions(driver);
        this.locatorResolver = new LocatorResolver();
    }

    /**
     * Executes a single test instruction.
     * <p>
     * Flow:
     * 1. Resolve the locator from config
     * 2. Execute the action based on action type
     * 3. Apply dynamic sleep if specified
     *
     * @param instruction The instruction to execute
     * @throws Exception if action execution fails
     */
    public void execute(TestInstruction instruction) throws Exception {

        String action = resolveByEnv(instruction.getAction());
        String locatorValue = resolveByEnv(instruction.getLocatorValue());
        String inputValue = resolveByEnv(instruction.getInputValue());

        instruction.setAction(action);
        instruction.setLocatorValue(locatorValue);
        instruction.setInputValue(inputValue);

        String stepName = instruction.getStepName();


        logger.info(
                "Executing Step: {} | Action: {} | Locator: {}",
                instruction.getStepName(),
                instruction.getAction(),
                instruction.getLocatorValue()
        );

        // If this step navigates away or submits a filled screen, capture screenshot before clicking
        if (isScreenTransitionStep(instruction)) {
            captureFilledScreen(stepName);
        }

        try {
            // Dispatch to appropriate action handler based on action type
            // Using switch expression for clean, exhaustive handling
            switch (action.toUpperCase()) {

                case AutomationConstants.ACTION_TYPE:
                    executeType(instruction);
                    break;

                case AutomationConstants.ACTION_CLICK:
                    executeClick(instruction);
                    break;

                case AutomationConstants.ACTION_CLICK_JS:
                    executeJsClick(instruction);
                    break;

                case AutomationConstants.ACTION_HOVER:
                    executeHover(instruction);
                    break;

                case AutomationConstants.ACTION_UPLOAD_FILE:
                    executeFileUpload(instruction);
                    break;

                case AutomationConstants.ACTION_TYPE_OTP:
                    executeOtpType(instruction);
                    break;

                case AutomationConstants.ACTION_SELECT_RADIO_BY_TEXT:
                    executeRadioSelectionByText(instruction);
                    break;

                case AutomationConstants.ACTION_SELECT_DROPDOWN_BY_INDEX:
                    executeDropdownSelectionByIndex(instruction);
                    break;

                case AutomationConstants.ACTION_CHECK_LAST_CHECKBOX:
                    checkLastCheckbox(instruction);
                    break;

                case AutomationConstants.ACTION_CAPTURE_TEXT:
                    captureText(instruction);
                    break;

                case AutomationConstants.ACTION_TYPE_FROM_STORE:
                    typeFromStore(instruction);
                    break;

                case AutomationConstants.ACTION_SET_DATE_TODAY:
                    executeSetDateToday(instruction);
                    break;

                case AutomationConstants.ACTION_SET_DATE_PLUS_DAYS:
                    executeSetDatePlusDays(instruction);
                    break;

                case AutomationConstants.ACTION_SWITCH_WINDOW:
                    switchWindow();
                    break;

                case AutomationConstants.ACTION_WAIT_FOR_TEXT:
                    waitForText(instruction);
                    break;

                case AutomationConstants.ACTION_SET_DATE_TEXT:
                    executeSetDateText(instruction);
                    break;

                case AutomationConstants.ACTION_MULTI_SELECT_CHECKBOX:
                    executeMultiSelectCheckbox(instruction);
                    break;

                case AutomationConstants.ACTION_OPEN_URL:
                    openUrl(instruction);
                    break;

                case AutomationConstants.ACTION_SET_CURRENT_TIME:
                    executeSetCurrentTime(instruction);
                    break;

                case AutomationConstants.ACTION_SET_CUSTOM_TIME:
                    executeSetCustomTime(instruction);
                    break;

                case AutomationConstants.ACTION_SET_DATE_JS:
                    executeSetDateJs(instruction);
                    break;

                case AutomationConstants.ACTION_TYPE_BY_LABEL:
                    executeTypeByLabel(instruction);
                    break;

                case AutomationConstants.ACTION_OPTIONAL_CLICK_JS:
                    try {
                        executeJsClick(instruction);
                    } catch (Exception e) {
                        logger.info("Optional step skipped");
                    }
                    break;

                case AutomationConstants.ACTION_OPTIONAL_SELECT_DROPDOWN_BY_INDEX:
                    try {
                        executeDropdownSelectionByIndex(instruction);
                        logger.info("Optional dropdown executed");
                    } catch (Exception e) {
                        logger.info("Optional dropdown step skipped");
                    }
                    break;

                case AutomationConstants.ACTION_OPTIONAL_TYPE:
                    try {
                        executeType(instruction);
                        logger.info("Optional type executed");
                    } catch (Exception e) {
                        logger.info("Optional type skipped");
                    }
                    break;

                case AutomationConstants.ACTION_SELECT_BY_VALUE:
                    executeSelectByValue(instruction);
                    break;

                case AutomationConstants.ACTION_SCROLL_TO_ELEMENT:
                    executeScrollToElement(instruction);
                    break;

                case AutomationConstants.ACTION_WAIT_VISIBLE:
                    executeWaitVisible(instruction);
                    break;

                case AutomationConstants.ACTION_SELECT_DATE_RANGE:
                    executeSelectDateRange();
                    break;

                case AutomationConstants.ACTION_ACCEPT_ALERT:

                    Alert alert = wait.until(ExpectedConditions.alertIsPresent());

                    logger.info("Alert Message : {}", alert.getText());

                    alert.accept();

                    break;

                case AutomationConstants.ACTION_CAPTURE_SCREENSHOT:
                case AutomationConstants.ACTION_SCREENSHOT:
                    executeCaptureScreenshot(instruction);
                    break;


                default:
                    throw new IllegalArgumentException(
                            "Unknown action: " + action
                    );
            }

            // Apply dynamic sleep after action
            applyDynamicSleep(instruction);

            // If this step is a submission or payment action, check if we arrived at an acknowledgement/response screen
            if (isSubmissionOrPaymentStep(instruction)) {
                try {
                    Thread.sleep(1500);
                    if (isAcknowledgementScreen()) {
                        captureFilledScreen("Acknowledgement - " + stepName);
                    }
                } catch (Exception e) {
                    logger.debug("Acknowledgement screen capture check failed for step '{}': {}", stepName, e.getMessage());
                }
            }

            logger.info("✓ Completed step: {}", stepName);

            // Record frame for smooth video timeline
            ScreenRecorder.recordFrame(driver);

            String reportValue = getReportValue(instruction, action);

            if (reportValue != null && !reportValue.isBlank()) {

                ReportManager.logStep(
                        "PASSED : " + stepName + " → " + reportValue
                );

            } else {

                ReportManager.logStep(
                        "PASSED : " + stepName
                );
            }

        } catch (NoSuchElementException e) {

            WorkflowDataStore.put(
                    AutomationConstants.KEY_FAILED_STEP,
                    stepName
            );

            WorkflowDataStore.put(
                    AutomationConstants.KEY_FAILED_ERROR,
                    e.getMessage()
            );

            String screenshotPath =
                    ScreenshotManager.captureFailureScreenshot(
                            driver,
                            WorkflowDataStore.get(AutomationConstants.KEY_CURRENT_MODULE),
                            WorkflowDataStore.get(AutomationConstants.KEY_CURRENT_TEST_CASE),
                            stepName
                    );

            String base64Fail = ScreenshotManager.captureBase64(driver);

            WorkflowDataStore.put(
                    AutomationConstants.KEY_FAILED_SCREENSHOT,
                    screenshotPath
            );

            ReportManager.logFailure(
                    "FAILED : " + stepName + " | " + e.getMessage(),
                    base64Fail
            );

            logger.error(
                    "Element not found for step '{}': {}",
                    stepName,
                    e.getMessage()
            );

            throw new RuntimeException(
                    "Step failed - element not found: " + stepName,
                    e
            );
        } catch (TimeoutException e) {

            WorkflowDataStore.put(
                    AutomationConstants.KEY_FAILED_STEP,
                    stepName
            );

            String screenshotPath =
                    ScreenshotManager.captureFailureScreenshot(
                            driver,
                            WorkflowDataStore.get(AutomationConstants.KEY_CURRENT_MODULE),
                            WorkflowDataStore.get(AutomationConstants.KEY_CURRENT_TEST_CASE),
                            stepName
                    );

            String base64Fail = ScreenshotManager.captureBase64(driver);

            WorkflowDataStore.put(
                    AutomationConstants.KEY_FAILED_SCREENSHOT,
                    screenshotPath
            );

            WorkflowDataStore.put(
                    AutomationConstants.KEY_FAILED_ERROR,
                    e.getMessage()
            );

            ReportManager.logFailure(
                    "TIMEOUT : " + stepName + " | " + e.getMessage(),
                    base64Fail
            );

            logger.error(
                    "Timeout waiting for element in step '{}': {}",
                    stepName,
                    e.getMessage()
            );

            throw new RuntimeException(
                    "Step failed - timeout: " + stepName,
                    e
            );
        } catch (ElementClickInterceptedException e) {
            logger.warn("Click intercepted for step '{}', attempting JS click", stepName);
            // Fallback to JS click when standard click is intercepted
            executeJsClick(instruction);

            ReportManager.logStep(
                    "PASSED (JS CLICK) : " + stepName
            );

            applyDynamicSleep(instruction);
        }
        catch (Exception e) {

            WorkflowDataStore.put(
                    AutomationConstants.KEY_FAILED_STEP,
                    stepName
            );

            WorkflowDataStore.put(
                    AutomationConstants.KEY_FAILED_ERROR,
                    e.getMessage()
            );

            String base64Fail = ScreenshotManager.captureBase64(driver);

            ReportManager.logFailure(
                    "FAILED : " + stepName + " | " + e.getMessage(),
                    base64Fail
            );

            throw new RuntimeException(
                    "Step execution failed: " + stepName,
                    e
            );
        }

    }

    /**
     * TYPE action: Clears the field and types the input value.
     */
    private void executeType(TestInstruction instruction) {

        By locator = locatorResolver.resolveLocator(instruction);

        WebElement element =
                wait.until(
                        ExpectedConditions.elementToBeClickable(locator)
                );

        // Scroll into view
        js.executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                element
        );

        // Resolve dynamic value from WorkflowDataStore
        String value = instruction.getInputValue();

        String storedValue =
                WorkflowDataStore.get(
                        instruction.getInputValue()
                );

        if (storedValue != null) {
            value = storedValue;
        }

        element.clear();

        element.sendKeys(value);

        logger.debug(
                "Typed '{}' into element",
                value
        );
    }

    /**
     * CLICK action: Standard Selenium click.
     */
    private void executeClick(TestInstruction instruction) {
        By locator = locatorResolver.resolveLocator(instruction);
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));

        js.executeScript("arguments[0].scrollIntoView({block:'center'});", element);
        element.click();

        logger.debug("Clicked element");
    }

    /**
     * CLICK_JS action: JavaScript click that bypasses overlay elements.
     * Useful when standard click is intercepted by modals, loading spinners, etc.
     */
    private void executeJsClick(TestInstruction instruction) {

        String resolvedLocator =
                resolveByEnv(instruction.getLocatorValue());

        By locator;

        switch (instruction.getLocatorStrategy().toUpperCase()) {
            case AutomationConstants.LOCATOR_XPATH:
                locator = By.xpath(resolvedLocator);
                break;

            case AutomationConstants.LOCATOR_CSS:
                locator = By.cssSelector(resolvedLocator);
                break;

            case AutomationConstants.LOCATOR_ID:
                locator = By.id(resolvedLocator);
                break;

            case AutomationConstants.LOCATOR_NAME:
                locator = By.name(resolvedLocator);
                break;

            default:
                throw new RuntimeException(
                        "Unsupported locator strategy: "
                                + instruction.getLocatorStrategy()
                );
        }

        WebElement element = wait.until(
                ExpectedConditions.presenceOfElementLocated(locator)
        );

        js.executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                element
        );

        js.executeScript("arguments[0].click();", element);

        logger.info("Resolved locator used: {}", resolvedLocator);
    }

    /**
     * HOVER action: Mouse hover using Actions API.
     */
    private void executeHover(TestInstruction instruction) {
        By locator = locatorResolver.resolveLocator(instruction);
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));

        actions.moveToElement(element).perform();

        logger.debug("Hovered over element");
    }

    /**
     * SELECT_DROPDOWN_BY_INDEX action: Handles custom dropdown components.
     * <p>
     * Input format: "dropdownIndex:optionIndex" (e.g., "0:0" for first dropdown, first option)
     * <p>
     * This handles non-standard dropdowns that use div/span elements
     * instead of native HTML select elements.
     */
    private void executeDropdownSelect(TestInstruction instruction) throws InterruptedException {
        By locator = locatorResolver.resolveLocator(instruction);
        String[] indices = instruction.getInputValue().split(":");
        int dropdownIndex = Integer.parseInt(indices[0]);
        int optionIndex = Integer.parseInt(indices[1]);

        // Find all dropdown triggers on the page
        List<WebElement> dropdowns = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(locator)
        );

        if (dropdownIndex >= dropdowns.size()) {
            throw new IndexOutOfBoundsException(
                    "Dropdown index " + dropdownIndex + " out of range (found " + dropdowns.size() + ")"
            );
        }

        WebElement dropdown = dropdowns.get(dropdownIndex);
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", dropdown);
        Thread.sleep(200);

        // Click to open dropdown
        try {
            dropdown.click();
        } catch (ElementClickInterceptedException e) {
            js.executeScript("arguments[0].click();", dropdown);
        }

        // Wait for options to appear and select by index
        WebElement optionsContainer = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.options-card"))
        );

        List<WebElement> options = optionsContainer.findElements(
                By.cssSelector("div.profile-dropdown--item")
        );

        if (optionIndex < options.size()) {
            js.executeScript("arguments[0].click();", options.get(optionIndex));
            logger.debug("Selected dropdown option at index {}", optionIndex);
        }
    }

    /**
     * SELECT_FIRST_DROPDOWN_OPTION: Opens dropdown and selects first available option.
     */
    private void executeSelectFirstDropdownOption(TestInstruction instruction) throws InterruptedException {
        By locator = locatorResolver.resolveLocator(instruction);
        WebElement dropdownTrigger = wait.until(ExpectedConditions.elementToBeClickable(locator));

        actions.moveToElement(dropdownTrigger).click().perform();

        WebElement optionsContainer = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.options-card"))
        );

        WebElement firstOption = optionsContainer.findElement(
                By.cssSelector(".profile-dropdown--item:first-child")
        );

        actions.moveToElement(firstOption).click().perform();
        logger.debug("Selected first dropdown option");
    }

    /**
     * UPLOAD_FILE action: Handles file input elements.
     * Makes hidden file inputs visible before sending file path.
     */
    private void executeFileUpload(TestInstruction instruction)
            throws InterruptedException {

        By locator = locatorResolver.resolveLocator(instruction);

        String rawPath = instruction.getInputValue();
        File file = resolveUploadFile(rawPath);

        List<WebElement> fileInputs =
                wait.until(
                        ExpectedConditions
                                .presenceOfAllElementsLocatedBy(locator)
                );

        if (fileInputs.isEmpty()) {
            throw new NoSuchElementException(
                    "No file input found with locator: " + locator
            );
        }

        WebElement fileInput = fileInputs.get(0);

        js.executeScript(
                "arguments[0].style.opacity='1';" +
                        "arguments[0].style.display='block';",
                fileInput
        );

        Thread.sleep(300);

        fileInput.sendKeys(file.getAbsolutePath());

        logger.info("Uploaded file: {}", file.getAbsolutePath());
    }

    /**
     * Resolves upload file from absolute path, classpath resources, or bundled document folders.
     * Prevents failures when running on Docker or different environments.
     */
    private File resolveUploadFile(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("Upload file path is empty");
        }

        // 1. Check if direct file path exists on current system
        File directFile = new File(filePath);
        if (directFile.exists() && directFile.isFile()) {
            return directFile;
        }

        // 2. Search within bundled classpath resources (Documents/...)
        String cleanedPath = filePath.replace("\\", "/");
        String fileName = new File(cleanedPath).getName();

        List<String> searchPaths = Arrays.asList(
                cleanedPath,
                cleanedPath.startsWith("/") ? cleanedPath.substring(1) : cleanedPath,
                "Documents/" + fileName,
                "Documents/tradelicense/" + fileName,
                "Documents/Advertisement/" + fileName,
                "Documents/pet/" + fileName,
                "Documents/streetVending/" + fileName,
                "Documents/OBPASDoc/" + fileName,
                "Documents/PropertyTax/" + fileName,
                "Documents/assetManagement/" + fileName,
                "Documents/challanGeneration/" + fileName,
                "Documents/pgr/" + fileName,
                "Documents/TreePrunining/" + fileName,
                "Documents/tradelicense/tradelicense.pdf",
                "Documents/Advertisement/advertisement.pdf",
                "Documents/pet/deep.png",
                "Documents/assetManagement/send (1).png"
        );

        for (String path : searchPaths) {
            InputStream is = getClass().getClassLoader().getResourceAsStream(path);
            if (is != null) {
                try {
                    String extension = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf(".")) : ".tmp";
                    File tempFile = File.createTempFile("upyog_upload_", extension);
                    tempFile.deleteOnExit();
                    try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                        is.transferTo(fos);
                    }
                    logger.info("Resolved upload file from bundled resources [{}] -> [{}]", path, tempFile.getAbsolutePath());
                    return tempFile;
                } catch (Exception e) {
                    logger.warn("Could not extract bundled resource [{}]: {}", path, e.getMessage());
                }
            }
        }

        // 3. Fallback: create a temporary valid sample file so automation proceeds without failure
        try {
            String extension = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf(".")) : ".pdf";
            File fallbackFile = File.createTempFile("sample_upload_", extension);
            fallbackFile.deleteOnExit();
            try (FileOutputStream fos = new FileOutputStream(fallbackFile)) {
                fos.write("UPYOG Automation Test Document Sample".getBytes());
            }
            logger.warn("Upload file [{}] not found on disk or classpath. Created fallback sample: {}", filePath, fallbackFile.getAbsolutePath());
            return fallbackFile;
        } catch (Exception e) {
            throw new IllegalArgumentException("Upload file not found: " + filePath, e);
        }
    }

    /**
     * WAIT_FOR_VISIBLE action: Explicit wait for element visibility.
     */
    private void executeWaitForVisible(TestInstruction instruction) {
        By locator = locatorResolver.resolveLocator(instruction);
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        logger.debug("Element is now visible");
    }

    /**
     * WAIT_FOR_URL_CONTAINS action: Waits until URL contains specified string.
     */
    private void executeWaitForUrlContains(TestInstruction instruction) {
        String urlPart = instruction.getLocatorValue();
        wait.until(ExpectedConditions.urlContains(urlPart));
        logger.debug("URL now contains: {}", urlPart);
    }

    /**
     * CHECK_LAST_CHECKBOX action: Finds all checkboxes and checks the last one.
     * Useful for declaration/terms checkboxes at end of forms.
     */
    private void executeCheckLastCheckbox(TestInstruction instruction) throws InterruptedException {
        By locator = locatorResolver.resolveLocator(instruction);
        List<WebElement> checkboxes = driver.findElements(locator);

        if (checkboxes.isEmpty()) {
            logger.warn("No checkboxes found");
            return;
        }

        WebElement lastCheckbox = checkboxes.get(checkboxes.size() - 1);

        if (!lastCheckbox.isSelected()) {
            js.executeScript("arguments[0].scrollIntoView({block:'center'});", lastCheckbox);
            Thread.sleep(300);
            js.executeScript("arguments[0].click();", lastCheckbox);
            logger.debug("Checked last checkbox");
        }
    }

    /**
     * SET_DATE_TODAY action: Sets a date input to today's date.
     * Uses JavaScript to set value and dispatch change event.
     */
    private void executeSetDateToday(TestInstruction instruction) {

        By locator = locatorResolver.resolveLocator(instruction);

        List<WebElement> dateInputs =
                wait.until(driver -> driver.findElements(locator));

        int fieldIndex = Integer.parseInt(instruction.getInputValue());

        WebElement dateInput = dateInputs.get(fieldIndex);

        LocalDate dateToSet;

        if (fieldIndex == 0) {
            // From date = tomorrow
            dateToSet = LocalDate.now();
        } else {
            // To date = +15 days
            dateToSet = LocalDate.now().plusDays(15);
        }

        String date = dateToSet.toString();

        js.executeScript(
                "const input = arguments[0];" +
                        "const value = arguments[1];" +

                        // React ke liye native setter
                        "const nativeInputValueSetter = Object.getOwnPropertyDescriptor(" +
                        "window.HTMLInputElement.prototype, 'value').set;" +

                        "nativeInputValueSetter.call(input, value);" +

                        // React events
                        "input.dispatchEvent(new Event('input', { bubbles: true }));" +
                        "input.dispatchEvent(new Event('change', { bubbles: true }));" +
                        "input.dispatchEvent(new Event('blur', { bubbles: true }));",

                dateInput,
                date
        );

        logger.info("Date field {} set to {}", fieldIndex, date);
    }

    /**
     * SET_DATE_PLUS_DAYS action: Sets a date input to plus 5 days.
     * Uses JavaScript to set value and dispatch change event.
     */

    private void executeSetDatePlusDays(TestInstruction instruction) {

        By locator = locatorResolver.resolveLocator(instruction);

        List<WebElement> dateInputs =
                wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));

        String[] values = instruction.getInputValue().split(",");

        int fieldIndex = Integer.parseInt(values[0]);
        int plusDays = Integer.parseInt(values[1]);

        WebElement dateInput = dateInputs.get(fieldIndex);

        String dateToSet =
                LocalDate.now().plusDays(plusDays).toString();

        js.executeScript(
                "arguments[0].value = arguments[1];" +
                        "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                        "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                dateInput,
                dateToSet
        );

        logger.debug("Set future date to: {}", dateToSet);
    }
    /**
     * CLEAR_AND_TYPE action: Clears existing content using keyboard shortcuts
     * before typing. Useful when element.clear() doesn't work properly.
     */
    private void executeClearAndType(TestInstruction instruction) {
        By locator = locatorResolver.resolveLocator(instruction);
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));

        element.click();
        element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        element.sendKeys(Keys.BACK_SPACE);
        element.sendKeys(instruction.getInputValue());

        logger.debug("Cleared and typed: {}", instruction.getInputValue());
    }

    /**
     * Applies the dynamic sleep specified in the instruction.
     * This allows per-step sleep configuration in JSON without hardcoding.
     */
    private void applyDynamicSleep(TestInstruction instruction) throws InterruptedException {
        long sleepMs = instruction.getDynamicSleep();
        if (sleepMs > 0) {
            Thread.sleep(sleepMs);
            logger.debug("Applied dynamic sleep: {}ms", sleepMs);
        }
    }

    /**
     * TYPE_OTP- for OTP submission
     */
    private void executeOtpType(TestInstruction instruction) {

        List<WebElement> otpInputs =
                driver.findElements(By.cssSelector(instruction.getLocatorValue()));

        String otp = instruction.getInputValue();

        for (int i = 0; i < otp.length() && i < otpInputs.size(); i++) {
            otpInputs.get(i).sendKeys(String.valueOf(otp.charAt(i)));
        }
    }
    /**
     * SELECT_RADIO_BY_TEXT action: Selects a radio button by its visible text label.
     */

    private void executeRadioSelectionByText(
            TestInstruction instruction)
            throws InterruptedException {

        List<WebElement> options = driver.findElements(
                        By.cssSelector(instruction.getLocatorValue()));

        String expectedText = instruction.getInputValue();

        for (WebElement option : options) {

            WebElement label = option.findElement(
                            By.tagName("label"));

            if (label.getText().trim()
                    .equals(expectedText)) {

                WebElement radio =
                        option.findElement(
                                By.cssSelector(
                                        "input[type='radio']"
                                )
                        );

                if (!radio.isSelected()) {

                    js.executeScript(
                            "arguments[0].click();",
                            radio
                    );

                    Thread.sleep(
                            instruction.getDynamicSleep()
                    );
                }

                return;
            }
        }

        throw new RuntimeException(
                "City not found: "
                        + expectedText
        );
    }

    /**
     * SELECT_DROPDOWN_BY_INDEX action: Selects a dropdown by its visible text label.
     */

    private void executeDropdownSelectionByIndex(
            TestInstruction instruction)
            throws InterruptedException {

        String[] indexes = instruction.getInputValue().split(":");

        int dropdownIndex = Integer.parseInt(indexes[0]);
        int optionIndex = Integer.parseInt(indexes[1]);

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(instruction.getLocatorValue())
                )
        );

        List<WebElement> dropdownInputs = driver.findElements(
                By.cssSelector(instruction.getLocatorValue())
        );

        logger.info("Found {} dropdown(s)", dropdownInputs.size());

        if (dropdownInputs.isEmpty()) {
            throw new RuntimeException(
                    "No dropdowns found with locator: "
                            + instruction.getLocatorValue()
            );
        }

        if (dropdownIndex >= dropdownInputs.size()) {
            throw new RuntimeException(
                    "Dropdown index out of range: "
                            + dropdownIndex
            );
        }

        WebElement dropdown = dropdownInputs.get(dropdownIndex);

        js.executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                dropdown
        );

        Thread.sleep(300);

        js.executeScript(
                "arguments[0].focus();" +
                        "arguments[0].dispatchEvent(new MouseEvent('mousedown',{bubbles:true}));" +
                        "arguments[0].dispatchEvent(new MouseEvent('click',{bubbles:true}));",
                dropdown
        );

        Thread.sleep(1000);

        List<WebElement> options = driver.findElements(
                By.cssSelector("div.profile-dropdown--item, div[role='option'], .employee-select-option")
        );

        if (options.isEmpty()) {
            logger.info("Primary dropdown selector failed, trying fallback...");
            options = driver.findElements(By.cssSelector("li"));
        }

        options = options.stream()
                .filter(WebElement::isDisplayed)
                .filter(e -> !e.getText().trim().isEmpty())
                .toList();

        logger.info("Filtered {} option(s)", options.size());

        for (int i = 0; i < options.size(); i++) {
            logger.info("Option {} : {}", i, options.get(i).getText());
        }

        if (optionIndex >= options.size()) {
            throw new RuntimeException(
                    "Option index out of range: " + optionIndex
            );
        }

        WebElement option = options.get(optionIndex);

// Capture the actual visible text BEFORE clicking
        String selectedOptionText = option.getText().trim();

        js.executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                option
        );

        Thread.sleep(200);

        js.executeScript("arguments[0].click();", option);

        Thread.sleep(instruction.getDynamicSleep());

        // Store the actual selected dropdown value for reporting
        WorkflowDataStore.put(
                AutomationConstants.KEY_SELECTED_VALUE,
                selectedOptionText
        );

        logger.info(
                "Selected dropdown {} option {} = {}",
                dropdownIndex,
                optionIndex,
                selectedOptionText
        );
    }

    /**
     * CHECK_LAST_CHECKBOX action: Selects a checkbox by its visible label.
     */

    private void checkLastCheckbox(
            TestInstruction instruction)
            throws InterruptedException {

        List<WebElement> checkboxes =
                driver.findElements(
                        By.cssSelector(
                                instruction.getLocatorValue()
                        )
                );

        if (checkboxes.isEmpty()) {
            throw new RuntimeException(
                    "No checkboxes found with locator: "
                            + instruction.getLocatorValue()
            );
        }

        WebElement lastCheckbox =
                checkboxes.get(
                        checkboxes.size() - 1
                );

        js.executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                lastCheckbox
        );

        Thread.sleep(300);

        if (!lastCheckbox.isSelected()) {

            js.executeScript(
                    "arguments[0].click();",
                    lastCheckbox
            );

            logger.info(
                    "Last checkbox selected"
            );
        } else {

            logger.info(
                    "Last checkbox already selected"
            );
        }

        Thread.sleep(
                instruction.getDynamicSleep()
        );
    }

    /**
     * CAPTURE_TEXT action: Captures the text by its visible text label.
     */

    private void captureText(TestInstruction instruction)
            throws InterruptedException {

        WebElement element =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                locatorResolver.resolveLocator(instruction)
                        )
                );

        js.executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                element
        );

        Thread.sleep(300);

        String capturedValue =
                element.getText().trim();

        if (capturedValue.isEmpty()) {

            throw new RuntimeException(
                    "Captured text is empty"
            );
        }

        String key = instruction.getInputValue();

        WorkflowDataStore.put(key, capturedValue);

        if (!AutomationConstants.WATER_APPLICATION_NO.equals(key)
                && !AutomationConstants.SEWERAGE_APPLICATION_NO.equals(key)) {

            WorkflowDataStore.put(AutomationConstants.APPLICATION_NO, capturedValue);
        }

// Existing logic
        if ("PERMIT_NUMBER".equals(key)) {
            TestDataStore.PERMIT_NUMBER = capturedValue;
        }

        if ("PERMIT_DATE".equals(key)) {
            TestDataStore.PERMIT_DATE = capturedValue;
        }
        logger.info(
                "Captured [{}] = {}",
                key,
                capturedValue
        );

        Thread.sleep(instruction.getDynamicSleep());
        logger.info(
                "Water App No = {}",
                WorkflowDataStore.get(AutomationConstants.WATER_APPLICATION_NO)
        );

        logger.info(
                "Sewerage App No = {}",
                WorkflowDataStore.get(AutomationConstants.SEWERAGE_APPLICATION_NO)
        );
        logger.info(
                "Captured Value = {}",
                capturedValue
        );
        logger.info(
                "APPLICATION_NO STORED = {}",
                WorkflowDataStore.get(AutomationConstants.APPLICATION_NO)
        );

        // Capture screenshot of the acknowledgement/response screen displaying the captured value
        try {
            captureFilledScreen("Acknowledgement - " + (instruction.getStepName() != null ? instruction.getStepName() : "Response"));
        } catch (Exception e) {
            logger.debug("Could not capture acknowledgement screen in captureText: {}", e.getMessage());
        }
    }

    /**
     * TYPE_FROM_STORE action: Reads a value previously stored in {@link WorkflowDataStore}
     * and types it into the target input element.
     *
     * @param instruction the test instruction containing store key and locator
     * @throws InterruptedException if thread sleep is interrupted
     * @throws RuntimeException if key is not found in the workflow store
     */
    private void typeFromStore(
            TestInstruction instruction)
            throws InterruptedException {

        WebElement element =
                wait.until(
                        ExpectedConditions
                                .visibilityOfElementLocated(
                        locatorResolver.resolveLocator(instruction)

                                )
                );

        String key = instruction.getInputValue();

        String storedValue =
                WorkflowDataStore.get(key);

        if ((storedValue == null || storedValue.isEmpty())
                && AutomationConstants.APPLICATION_NO.equals(key)) {

            storedValue =
                    WorkflowDataStore.get(
                            AutomationConstants.KEY_SELECTED_APPLICATION_NO
                    );
        }

        if (storedValue == null) {

            throw new RuntimeException(
                    "No value found in workflow store for key: "
                            + instruction.getInputValue()
            );
        }

        element.clear();

        element.sendKeys(
                storedValue
        );

        logger.info(
                "Typed stored value [{}] = {}",
                instruction.getInputValue(),
                storedValue
        );

        Thread.sleep(
                instruction.getDynamicSleep()
        );
    }
    /**
     * SWITCH_WINDOW action: Helps in switching the windows for payment gateways.
     */


    private void switchWindow() {

        String currentWindow =
                driver.getWindowHandle();

        for (String windowHandle : driver.getWindowHandles()) {

            if (!windowHandle.equals(currentWindow)) {

                driver.switchTo()
                        .window(windowHandle);

                logger.info(
                        "Switched to new window"
                );

                return;
            }
        }

        throw new RuntimeException(
                "No new window found"
        );
    }

    /**
     * WAIT_FOR_TEXT: Helps in visibility of text if it appears late
     */

    private void waitForText(
            TestInstruction instruction) {

        By locator =
                locatorResolver.resolveLocator(
                        instruction
                );

        wait.until(
                ExpectedConditions.textToBePresentInElementLocated(
                        locator,
                        instruction.getInputValue()
                )
        );

        logger.info(
                "Text found: {}",
                instruction.getInputValue()
        );
    }

    /**
     * SET_DATE_TEXT: To fill the date through text
     */

    private void executeSetDateText(TestInstruction instruction) {

        By locator = locatorResolver.resolveLocator(instruction);

        List<WebElement> inputs = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(locator)
        );

        String[] parts = instruction.getInputValue().split(":");

        int index = Integer.parseInt(parts[0]);
        String dateValue = parts[1];

        WebElement input = inputs.get(index);

        input.click();

        input.sendKeys(Keys.COMMAND + "a");
        input.sendKeys(Keys.BACK_SPACE);

        input.sendKeys(dateValue);

        input.sendKeys(Keys.TAB);

        logger.debug("Date field {} set to {}", index, dateValue);
    }

    /**
     * MULTI_SELECT_CHECKBOX: To select multiple options from the dropdown
     */

    private void executeMultiSelectCheckbox(TestInstruction instruction)
            throws InterruptedException {

        String[] values = instruction.getInputValue().split(",");

        for (String value : values) {

            String xpath =
                    "//div[contains(@class,'option-item')][.//p[contains(normalize-space(.),'"
                            + value.trim() + "')]]//div[contains(@class,'custom-checkbox')]";

            WebElement option = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath(xpath))
            );

            js.executeScript(
                    "arguments[0].dispatchEvent(new MouseEvent('mousedown', {bubbles:true}));" +
                            "arguments[0].dispatchEvent(new MouseEvent('mouseup', {bubbles:true}));" +
                            "arguments[0].click();" +
                            "arguments[0].dispatchEvent(new Event('change', {bubbles:true}));",
                    option
            );

            Thread.sleep(500);

            js.executeScript("arguments[0].click();", option);

            logger.info("Selected checkbox option: {}", value.trim());

            Thread.sleep(1000);
        }
    }

    /**
     * OPEN_URL: To open url when we log out from one mobile number and
     * login again through another mobile number
     */


    private void openUrl(TestInstruction instruction)
            throws InterruptedException {

        driver.get(instruction.getLocatorValue());

        logger.info(
                "Opened URL: {}",
                instruction.getLocatorValue()
        );

        Thread.sleep(
                instruction.getDynamicSleep()
        );
    }

    /**
     * SET_CURRENT_TIME: This helps in setting the current time
     */

    private void executeSetCurrentTime(TestInstruction instruction) {

        By locator = locatorResolver.resolveLocator(instruction);

        WebElement element = wait.until(
                ExpectedConditions.elementToBeClickable(locator)
        );

        String currentTime = LocalTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm"));

        js.executeScript(
                "arguments[0].value=arguments[1]; arguments[0].dispatchEvent(new Event('change'));",
                element,
                currentTime
        );

        logger.debug("Set current time: {}", currentTime);
    }

    /**
     * SET_CUSTOM_TIME: This helps in setting the custom time
     */

    private void executeSetCustomTime(TestInstruction instruction) {

        By locator = locatorResolver.resolveLocator(instruction);

        WebElement element = wait.until(
                ExpectedConditions.elementToBeClickable(locator)
        );

        String inputTime = instruction.getInputValue()
                .replaceAll("\\s+", " ")
                .trim()
                .toUpperCase();

        String[] parts = inputTime.split(" ");

        String timePart = parts[0];
        String ampm = parts[1];

        String[] timeArray = timePart.split(":");

        int hour = Integer.parseInt(timeArray[0]);
        String minute = timeArray[1];

        if (ampm.equals("PM") && hour != 12) {
            hour += 12;
        }

        if (ampm.equals("AM") && hour == 12) {
            hour = 0;
        }

        String formattedTime =
                String.format("%02d:%s", hour, minute);

        logger.info("Setting time: {}", formattedTime);

        js.executeScript(

                "const input = arguments[0];" +
                        "const value = arguments[1];" +

                        "const nativeInputValueSetter = " +
                        "Object.getOwnPropertyDescriptor(" +
                        "window.HTMLInputElement.prototype," +
                        "'value').set;" +

                        "nativeInputValueSetter.call(input, value);" +

                        "input.dispatchEvent(new Event('input', { bubbles: true }));" +
                        "input.dispatchEvent(new Event('change', { bubbles: true }));" +
                        "input.dispatchEvent(new Event('blur', { bubbles: true }));",

                element,
                formattedTime
        );

        logger.info("Time set successfully");
    }

    /**
     * SET_DATE_JS: This helps in setting the date through js
     */


    private void executeSetDateJs(TestInstruction instruction) {

        By locator = locatorResolver.resolveLocator(instruction);

        WebElement element = wait.until(
                ExpectedConditions.visibilityOfElementLocated(locator)
        );

        String dateValue = instruction.getInputValue();

        js.executeScript(

                "const input = arguments[0];" +
                        "const value = arguments[1];" +

                        // React native setter
                        "const nativeInputValueSetter = " +
                        "Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value').set;" +

                        "nativeInputValueSetter.call(input, value);" +

                        // React events
                        "input.dispatchEvent(new Event('input', { bubbles: true }));" +
                        "input.dispatchEvent(new Event('change', { bubbles: true }));" +
                        "input.dispatchEvent(new Event('blur', { bubbles: true }));",

                element,
                dateValue
        );

        logger.info("React date set successfully: {}", dateValue);
    }

    /**
     * TYPE_BY_LABEL action: Finds an input element associated with a label text and types the input value into it.
     *
     * @param instruction the test instruction containing the label text locator and input value
     */
    private void executeTypeByLabel(TestInstruction instruction) {

        WebElement input = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(normalize-space(.),'" +
                                instruction.getLocatorValue() +
                                "')]/following::input[1]")
                )
        );

        js.executeScript("arguments[0].scrollIntoView(true);", input);

        input.clear();
        input.sendKeys(instruction.getInputValue());

        logger.debug("Typed '{}' into label '{}'",
                instruction.getInputValue(),
                instruction.getLocatorValue());
    }

    /**
     * SELECT_BY_VALUE action: Selects an option from a native HTML select dropdown by its value attribute.
     *
     * @param instruction the test instruction containing locator and select value
     */
    private void executeSelectByValue(TestInstruction instruction) {

        By locator = locatorResolver.resolveLocator(instruction);

        WebElement element =
                wait.until(
                        ExpectedConditions.elementToBeClickable(locator)
                );

        // Scroll into view
        js.executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                element
        );

        Select select =
                new Select(element);

        select.selectByValue(
                instruction.getInputValue()
        );

        logger.debug(
                "Selected value '{}' from dropdown",
                instruction.getInputValue()
        );
    }

    /**
     * SCROLL_TO_ELEMENT action: Scrolls the browser viewport until the specified element is centered.
     *
     * @param instruction the test instruction containing locator
     */
    private void executeScrollToElement(TestInstruction instruction) {

        By locator = locatorResolver.resolveLocator(instruction);

        WebElement element = wait.until(
                ExpectedConditions.visibilityOfElementLocated(locator)
        );

        js.executeScript(
                "arguments[0].scrollIntoView({block:'center', inline:'nearest'});",
                element
        );

        logger.info("Scrolled to element");
    }

    /**
     * WAIT_VISIBLE action: Waits until the target element is visible in the DOM.
     *
     * @param instruction the test instruction containing locator and optional sleep
     */
    private void executeWaitVisible(TestInstruction instruction) {

        By locator = locatorResolver.resolveLocator(instruction);

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(locator)
        );

        logger.info(
                "Element is visible: {}",
                instruction.getLocatorValue()
        );

        if (instruction.getDynamicSleep() > 0) {
            try {
                Thread.sleep(instruction.getDynamicSleep());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Clicks the date range calendar icon trigger on the page.
     */
    private void clickCalendar() {

        WebElement calendar =
                wait.until(ExpectedConditions.elementToBeClickable(
                        By.cssSelector("svg.date-range-calendar-icon")));

        calendar.click();
    }

    /**
     * Clicks the continuous selection tab/item within the date range picker.
     */
    private void clickContinuous() {

        WebElement continuous =
                wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//span[contains(@class,'rdrDateDisplayItem')][.//input[@placeholder='Continuous']]")));

        continuous.click();
    }

    /**
     * Clicks a specific date cell in the date range picker calendar.
     *
     * @param date the {@link LocalDate} to select
     */
    private void clickDate(LocalDate date) {

        String day = String.valueOf(date.getDayOfMonth());

        By locator = By.xpath(
                "//button[contains(@class,'rdrDay')]" +
                        "[not(contains(@class,'rdrDayPassive'))]" +
                        "[.//span[@class='rdrDayNumber']/span[text()='" + day + "']]"
        );

        WebElement dateElement = wait.until(
                ExpectedConditions.elementToBeClickable(locator)
        );

        actions.moveToElement(dateElement)
                .click()
                .perform();

        logger.info("Clicked Date = {}", date);
    }

    /**
     * SELECT_DATE_RANGE action: Selects a continuous date range (from tomorrow to +3 days).
     *
     * @throws InterruptedException if thread sleep is interrupted
     */
    private void executeSelectDateRange() throws InterruptedException {

        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = LocalDate.now().plusDays(3);

        // Calendar already open from JSON step "Open Calendar"

        // First date
        clickDate(start);

        logger.info("Start Date Selected");

        Thread.sleep(1000);

        // Calendar closed automatically

        // Open calendar again
        clickCalendar();

        Thread.sleep(800);

        // Click Continuous box
        clickContinuous();

        logger.info("Clicked Continuous Box");

        Thread.sleep(500);

        // End date
        clickDate(end);

        logger.info("End Date Selected");

        logger.info("Date Range Selected : {} -> {}", start, end);
    }

    /**
     * Extracts and returns the display value to be logged in the Extent/HTML test report for a step.
     *
     * @param instruction the executed test instruction
     * @param action the action type string
     * @return the resolved string value suitable for reporting, or null if not applicable
     */
    private String getReportValue(
            TestInstruction instruction,
            String action) {

        try {

            switch (action.toUpperCase()) {

                case AutomationConstants.ACTION_TYPE:
                case AutomationConstants.ACTION_TYPE_BY_LABEL:
                case AutomationConstants.ACTION_CLEAR_AND_TYPE:
                case AutomationConstants.ACTION_TYPE_FROM_STORE:

                    return instruction.getInputValue();

                case AutomationConstants.ACTION_SELECT_RADIO_BY_TEXT:
                case AutomationConstants.ACTION_MULTI_SELECT_CHECKBOX:

                    return instruction.getInputValue();

                case AutomationConstants.ACTION_SELECT_DROPDOWN_BY_INDEX:

                    return WorkflowDataStore.get(AutomationConstants.KEY_SELECTED_VALUE);

                case AutomationConstants.ACTION_UPLOAD_FILE:

                    String filePath = instruction.getInputValue();

                    if (filePath != null) {
                        return new File(filePath).getName();
                    }

                    return null;

                case AutomationConstants.ACTION_CAPTURE_TEXT:

                    String key = instruction.getInputValue();

                    return WorkflowDataStore.get(key);

                case AutomationConstants.ACTION_SET_DATE_TODAY:
                case AutomationConstants.ACTION_SET_DATE_PLUS_DAYS:
                case AutomationConstants.ACTION_SET_DATE_TEXT:
                case AutomationConstants.ACTION_SET_DATE_JS:
                case AutomationConstants.ACTION_SET_CURRENT_TIME:
                case AutomationConstants.ACTION_SET_CUSTOM_TIME:

                    return instruction.getInputValue();

                default:

                    return null;
            }

        } catch (Exception e) {

            logger.debug(
                    "Could not determine report value for step '{}': {}",
                    instruction.getStepName(),
                    e.getMessage()
            );

            return null;
        }
    }

    /**
     * Determines whether an instruction represents a screen transition/submission step
     * (e.g. clicking Next, Submit, Continue, Proceed, Forward, Pay, Apply, Approve, Verify).
     *
     * @param instruction the test step instruction
     * @return true if the step transitions away from a filled screen
     */
    private boolean isScreenTransitionStep(TestInstruction instruction) {
        if (instruction == null) {
            return false;
        }

        String action = instruction.getAction();
        if (action == null) {
            return false;
        }

        String upperAction = action.toUpperCase();

        if (AutomationConstants.ACTION_CAPTURE_SCREENSHOT.equals(upperAction)
                || AutomationConstants.ACTION_SCREENSHOT.equals(upperAction)) {
            return false; // Handled directly by executeCaptureScreenshot
        }

        if (AutomationConstants.ACTION_CLICK.equals(upperAction)
                || AutomationConstants.ACTION_CLICK_JS.equals(upperAction)
                || AutomationConstants.ACTION_OPTIONAL_CLICK_JS.equals(upperAction)) {

            String stepName = instruction.getStepName() != null ? instruction.getStepName().toLowerCase() : "";
            String locator = instruction.getLocatorValue() != null ? instruction.getLocatorValue().toLowerCase() : "";

            return stepName.contains("next")
                    || stepName.contains("submit")
                    || stepName.contains("continue")
                    || stepName.contains("proceed")
                    || stepName.contains("forward")
                    || stepName.contains("save")
                    || stepName.contains("apply")
                    || stepName.contains("pay")
                    || stepName.contains("approve")
                    || stepName.contains("verify")
                    || stepName.contains("send otp")
                    || stepName.contains("take action")
                    || locator.contains("next")
                    || locator.contains("submit")
                    || locator.contains("continue");
        }

        return false;
    }

    /**
     * Determines whether an instruction represents a final submission, payment, or approval action.
     *
     * @param instruction the test step instruction
     * @return true if the step triggers submission, payment, or approval
     */
    private boolean isSubmissionOrPaymentStep(TestInstruction instruction) {
        if (instruction == null) {
            return false;
        }

        String action = instruction.getAction();
        if (action == null) {
            return false;
        }

        String upperAction = action.toUpperCase();
        if (AutomationConstants.ACTION_CLICK.equals(upperAction)
                || AutomationConstants.ACTION_CLICK_JS.equals(upperAction)
                || AutomationConstants.ACTION_OPTIONAL_CLICK_JS.equals(upperAction)) {

            String stepName = instruction.getStepName() != null ? instruction.getStepName().toLowerCase() : "";
            String locator = instruction.getLocatorValue() != null ? instruction.getLocatorValue().toLowerCase() : "";

            return stepName.contains("submit")
                    || stepName.contains("pay")
                    || stepName.contains("collect payment")
                    || stepName.contains("approve")
                    || stepName.contains("forward")
                    || stepName.contains("verify popup")
                    || stepName.contains("approve popup")
                    || stepName.contains("take action")
                    || locator.contains("submit")
                    || locator.contains("collect payment")
                    || locator.contains("pay");
        }

        return false;
    }

    /**
     * Checks whether the browser is currently viewing an acknowledgement, response, or payment success page.
     *
     * @return true if the active page matches known UPYOG acknowledgement / response signatures
     */
    public boolean isAcknowledgementScreen() {
        try {
            String currentUrl = driver.getCurrentUrl();
            if (currentUrl != null) {
                String lowerUrl = currentUrl.toLowerCase();
                if (lowerUrl.contains("/response")
                        || lowerUrl.contains("/success")
                        || lowerUrl.contains("/acknowledgement")
                        || lowerUrl.contains("/acknowledgment")
                        || lowerUrl.contains("/receipt")
                        || lowerUrl.contains("/collect-receipt")) {
                    return true;
                }
            }

            String pageSource = driver.getPageSource();
            if (pageSource != null) {
                String lowerSource = pageSource.toLowerCase();
                if (lowerSource.contains("payment collected")
                        || lowerSource.contains("payment successful")
                        || lowerSource.contains("payment completed")
                        || lowerSource.contains("application submitted")
                        || lowerSource.contains("application created")
                        || lowerSource.contains("booking successful")
                        || lowerSource.contains("challan created")
                        || lowerSource.contains("trade license created")
                        || lowerSource.contains("permit generated")) {
                    return true;
                }
            }
        } catch (Exception e) {
            logger.debug("Could not determine if current screen is acknowledgement: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Captures a screenshot of the currently displayed screen, saves it to disk, and logs it to the active report.
     *
     * @param screenName descriptive label for the screenshot
     * @param force if true, bypasses duplicate fingerprint checking
     */
    public void captureScreen(String screenName, boolean force) {
        try {
            String currentUrl = "";
            try {
                currentUrl = driver.getCurrentUrl();
            } catch (Exception ignored) {}

            long now = System.currentTimeMillis();
            String currentHeading = "";
            try {
                List<WebElement> headers = driver.findElements(By.cssSelector("h1, h2, h3, header, .card-label, .heading"));
                if (!headers.isEmpty()) {
                    currentHeading = headers.get(0).getText().trim();
                }
            } catch (Exception ignored) {}

            String fingerprint = (currentUrl != null ? currentUrl : "") + "|" + currentHeading;

            // If not forced, avoid duplicate captures of the exact same screen within 2.5 seconds
            if (!force && fingerprint.equals(lastCapturedFingerprint) && (now - lastCapturedTime < 2500)) {
                logger.debug("Skipping duplicate screen capture for '{}' (same page state)", screenName);
                return;
            }
            lastCapturedFingerprint = fingerprint;
            lastCapturedTime = now;

            String selectedMod = WorkflowDataStore.get(AutomationConstants.KEY_SELECTED_MODULE);
            String currentMod = WorkflowDataStore.get(AutomationConstants.KEY_CURRENT_MODULE);
            String moduleName = (selectedMod != null && !selectedMod.isBlank()) ? selectedMod : currentMod;
            if (moduleName == null || moduleName.isBlank() || moduleName.equalsIgnoreCase("TC") || moduleName.startsWith("TC_")) {
                moduleName = (currentMod != null && !currentMod.isBlank()) ? currentMod : "UPYOG";
            }

            String testCase = WorkflowDataStore.get(AutomationConstants.KEY_CURRENT_TEST_CASE);
            if (testCase == null || testCase.isBlank()) {
                testCase = "WORKFLOW";
            }

            // Save to disk for local file archives and User Manual
            ScreenshotManager.captureScreenScreenshot(
                    driver,
                    moduleName,
                    testCase,
                    screenName
            );

            // Capture base64 representation to embed directly inside the HTML report
            String base64Screenshot = ScreenshotManager.captureBase64(driver);

            if (base64Screenshot != null && !base64Screenshot.isEmpty()) {
                ReportManager.logScreen("SCREEN CAPTURE : " + screenName, base64Screenshot);
            }
        } catch (Exception e) {
            logger.warn("Could not capture screen screenshot for step '{}': {}", screenName, e.getMessage());
        }
    }

    /**
     * Captures a screenshot of the filled form or intermediate screen.
     *
     * @param screenName descriptive label for the screenshot
     */
    public void captureFilledScreen(String screenName) {
        captureScreen(screenName, false);
    }

    /**
     * Explicitly captures the acknowledgement/response screen.
     *
     * @param screenName descriptive label for the screenshot
     */
    public void captureAcknowledgementScreen(String screenName) {
        captureScreen(screenName != null && !screenName.isBlank() ? screenName : "Acknowledgement", true);
    }

    /**
     * Executes an explicit CAPTURE_SCREENSHOT instruction.
     *
     * @param instruction the test instruction
     */
    private void executeCaptureScreenshot(TestInstruction instruction) {
        String screenName = instruction.getStepName() != null ? instruction.getStepName() : "Screen";
        captureScreen(screenName, true);
    }
}