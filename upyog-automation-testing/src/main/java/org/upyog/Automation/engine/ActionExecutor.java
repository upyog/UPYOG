package org.upyog.Automation.engine;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.upyog.Automation.Reports.ReportManager;
import org.upyog.Automation.Utils.TestDataStore;
import org.upyog.Automation.Utils.WorkflowDataStore;
import org.upyog.Automation.model.TestInstruction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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

    private String resolveByEnv(String value) {
        if (value == null || !value.contains("||")) {
            return value;
        }

        String env = WorkflowDataStore.get("selected.env");
        String[] parts = value.split("\\|\\|");

        return "NIUATT".equalsIgnoreCase(env)
                ? parts[0]
                : parts[1];
    }

    private static final Logger logger = LoggerFactory.getLogger(ActionExecutor.class);

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final JavascriptExecutor js;
    private final Actions actions;
    private final org.upyog.Automation.engine.LocatorResolver locatorResolver;

    public ActionExecutor(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
        this.js = (JavascriptExecutor) driver;
        this.actions = new Actions(driver);
        this.locatorResolver = new org.upyog.Automation.engine.LocatorResolver();
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

        try {
            // Dispatch to appropriate action handler based on action type
            // Using switch expression for clean, exhaustive handling
            switch (action.toUpperCase()) {

                case "TYPE":
                    executeType(instruction);
                    break;

                case "CLICK":
                    executeClick(instruction);
                    break;

                case "CLICK_JS":
                    executeJsClick(instruction);
                    break;

                case "HOVER":
                    executeHover(instruction);
                    break;

                case "UPLOAD_FILE":
                    executeFileUpload(instruction);
                    break;

                case "TYPE_OTP":
                    executeOtpType(instruction);
                    break;

                case "SELECT_RADIO_BY_TEXT":
                    executeRadioSelectionByText(instruction);
                    break;

                case "SELECT_DROPDOWN_BY_INDEX":
                    executeDropdownSelectionByIndex(instruction);
                    break;

                case "CHECK_LAST_CHECKBOX":
                    checkLastCheckbox(instruction);
                    break;

                case "CAPTURE_TEXT":
                    captureText(instruction);
                    break;

                case "TYPE_FROM_STORE":
                    typeFromStore(instruction);
                    break;

                case "SET_DATE_TODAY":
                    executeSetDateToday(instruction);
                    break;

                case "SET_DATE_PLUS_DAYS":
                    executeSetDatePlusDays(instruction);
                    break;

                case "SWITCH_WINDOW":
                    switchWindow();
                    break;

                case "WAIT_FOR_TEXT":
                    waitForText(instruction);
                    break;

                case "SET_DATE_TEXT":
                    executeSetDateText(instruction);
                    break;

                case "MULTI_SELECT_CHECKBOX":
                    executeMultiSelectCheckbox(instruction);
                    break;

                case "OPEN_URL":
                    openUrl(instruction);
                    break;

                case "SET_CURRENT_TIME":
                    executeSetCurrentTime(instruction);
                    break;

                case "SET_CUSTOM_TIME":
                    executeSetCustomTime(instruction);
                    break;

                case "SET_DATE_JS":
                    executeSetDateJs(instruction);
                    break;

                case "TYPE_BY_LABEL":
                    executeTypeByLabel(instruction);
                    break;

                case "OPTIONAL_CLICK_JS":
                    try {
                        executeJsClick(instruction);
                    } catch (Exception e) {
                        logger.info("Optional step skipped");
                    }
                    break;

                case "OPTIONAL_SELECT_DROPDOWN_BY_INDEX":
                    try {
                        executeDropdownSelectionByIndex(instruction);
                        logger.info("Optional dropdown executed");
                    } catch (Exception e) {
                        logger.info("Optional dropdown step skipped");
                    }
                    break;

                case "OPTIONAL_TYPE":
                    try {
                        executeType(instruction);
                        logger.info("Optional type executed");
                    } catch (Exception e) {
                        logger.info("Optional type skipped");
                    }
                    break;

                case "SELECT_BY_VALUE":
                    executeSelectByValue(instruction);
                    break;

                case "SCROLL_TO_ELEMENT":
                    executeScrollToElement(instruction);
                    break;

                case "WAIT_VISIBLE":
                    executeWaitVisible(instruction);
                    break;

                case "SELECT_DATE_RANGE":
                    executeSelectDateRange();
                    break;

                case "ACCEPT_ALERT":

                    Alert alert = wait.until(ExpectedConditions.alertIsPresent());

                    logger.info("Alert Message : {}", alert.getText());

                    alert.accept();

                    break;


                default:
                    throw new IllegalArgumentException(
                            "Unknown action: " + action
                    );
            }

            // Apply dynamic sleep after action (configured per-step in JSON)
            applyDynamicSleep(instruction);

            logger.info("✓ Completed step: {}", stepName);

            ReportManager.logStep(
                    "PASSED : " + stepName
            );

        } catch (NoSuchElementException e) {
            ReportManager.logFailure(
                    "FAILED : " + stepName
            );
            logger.error("✗ Element not found for step '{}': {}", stepName, e.getMessage());
            throw new RuntimeException("Step failed - element not found: " + stepName, e);


        } catch (TimeoutException e) {
            ReportManager.logFailure(
                    "TIMEOUT : " + stepName
            );
            logger.error("✗ Timeout waiting for element in step '{}': {}", stepName, e.getMessage());
            throw new RuntimeException("Step failed - timeout: " + stepName, e);

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

            ReportManager.logFailure(
                    "FAILED : " + stepName
            );

            throw new RuntimeException("Step execution failed", e);
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
            case "XPATH":
                locator = By.xpath(resolvedLocator);
                break;

            case "CSS":
                locator = By.cssSelector(resolvedLocator);
                break;

            case "ID":
                locator = By.id(resolvedLocator);
                break;

            case "NAME":
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

        String filePath = instruction.getInputValue();

        File file = new File(filePath);

        if (!file.exists()) {
            throw new IllegalArgumentException(
                    "Upload file not found: " + filePath
            );
        }

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

        js.executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                option
        );

        Thread.sleep(200);

        js.executeScript("arguments[0].click();", option);

        Thread.sleep(instruction.getDynamicSleep());

        logger.info(
                "Selected dropdown {} option {}",
                dropdownIndex,
                optionIndex
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

        if (!"WATER_APPLICATION_NO".equals(key)
                && !"SEWERAGE_APPLICATION_NO".equals(key)) {

            WorkflowDataStore.put("APPLICATION_NO", capturedValue);
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
                WorkflowDataStore.get("WATER_APPLICATION_NO")
        );

        logger.info(
                "Sewerage App No = {}",
                WorkflowDataStore.get("SEWERAGE_APPLICATION_NO")
        );
        logger.info(
                "Captured Value = {}",
                capturedValue
        );
        logger.info(
                "APPLICATION_NO STORED = {}",
                WorkflowDataStore.get("APPLICATION_NO")
        );
    }

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
                && "APPLICATION_NO".equals(key)) {

            storedValue =
                    WorkflowDataStore.get(
                            "selected.applicationNumber"
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
    private void clickCalendar() {

        WebElement calendar =
                wait.until(ExpectedConditions.elementToBeClickable(
                        By.cssSelector("svg.date-range-calendar-icon")));

        calendar.click();
    }
    private void clickContinuous() {

        WebElement continuous =
                wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//span[contains(@class,'rdrDateDisplayItem')][.//input[@placeholder='Continuous']]")));

        continuous.click();
    }
    private void clickDate(int day) {

        By locator = By.xpath(
                "//button[contains(@class,'rdrDay')][.//span[@class='rdrDayNumber']/span[text()='" + day + "']]"
        );

        WebElement date = wait.until(
                ExpectedConditions.visibilityOfElementLocated(locator)
        );

        actions.moveToElement(date)
                .click()
                .perform();

        logger.info("Clicked Date = {}", day);
    }

    private void executeSelectDateRange() throws InterruptedException {

        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = LocalDate.now().plusDays(3);

        // Calendar already open from JSON step "Open Calendar"

        // First date
        clickDate(start.getDayOfMonth());

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
        clickDate(end.getDayOfMonth());

        logger.info("End Date Selected");

        logger.info("Date Range Selected : {} -> {}", start, end);
    }
}