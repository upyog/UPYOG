package org.upyog.Automation.Utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.upyog.Automation.Reports.ReportManager;

import java.util.List;

public class CommonActions {

    public static void fillInput(WebDriverWait wait, String fieldName, String value) {
        By locator = By.xpath("//input[@name='" + fieldName + "' or @id='" + fieldName + "'] | //*[@name='" + fieldName + "']");
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(locator));
        input.clear();
        input.sendKeys(value);
    }

    public static void clickButton(WebDriverWait wait,
                                   JavascriptExecutor js,
                                   String xpath) {

        WebElement button = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath(xpath)
                )
        );

        js.executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                button
        );

        wait.until(
                ExpectedConditions.visibilityOf(button)
        );

        js.executeScript(
                "arguments[0].click();",
                button
        );
    }

    public static void selectDropdown(WebDriver driver, WebDriverWait wait, String fieldName, String value) {
        WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(By.name(fieldName)));
        dropdown.click();

        WebElement option = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//li[contains(text(),'" + value + "')]")));
        option.click();
    }

    public static void selectRadioButtonByLabel(WebDriver driver,
                                                WebDriverWait wait,
                                                JavascriptExecutor js,
                                                String labelText) {

        By locator = By.xpath(
                "//label[normalize-space()='" + labelText + "']/preceding-sibling::span//input[@type='radio'] | " +
                "//label[normalize-space()='" + labelText + "']/preceding-sibling::input[@type='radio'] | " +
                "//label[normalize-space()='" + labelText + "']//input[@type='radio'] | " +
                "//label[normalize-space()='" + labelText + "']"
        );

        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", element);
        try {
            wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", element);
        }
    }

    public static void clickButtonByText(WebDriver driver, WebDriverWait wait, JavascriptExecutor js, String text) {
        By locator = By.xpath("//button[.//header[normalize-space()='" + text + "'] or normalize-space()='" + text + "' or .//span[normalize-space()='" + text + "']]");
        WebElement button = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", button);
        try {
            wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", button);
        }
    }

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CommonActions.class);

    public static void selectCity(WebDriver driver,
                                  WebDriverWait wait,
                                  JavascriptExecutor js,
                                  String cityName) {

        String selectedUrl = WorkflowDataStore.get(AutomationConstants.KEY_SELECTED_URL);
        String selectedModule = WorkflowDataStore.get(AutomationConstants.KEY_SELECTED_MODULE);

        if (cityName == null || cityName.isBlank() || "Select City".equalsIgnoreCase(cityName)) {
            if (selectedUrl != null && selectedUrl.toLowerCase().contains("sandbox")) {
                cityName = "City A Muncipal Corporation";
            } else if (selectedUrl != null && selectedUrl.toLowerCase().contains("niuatt")) {
                cityName = "City A";
            } else if (selectedUrl != null && selectedUrl.toLowerCase().contains("upyog.niua.org")) {
                cityName = "Delhi";
            }
        }

        logger.info("Selecting city on location screen: '{}' (Target URL: {}, Module: {})", cityName, selectedUrl, selectedModule);

        // 1. Wait for location screen container or radio elements
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.radio-wrap")),
                    ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[type='radio']")),
                    ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Choose your location') or contains(text(),'Select City') or contains(text(),'City')]"))
            ));
        } catch (Exception e) {
            logger.warn("Location container wait, checking options directly: {}", e.getMessage());
        }

        // 2. Direct radio input match associated with target city label or value
        List<WebElement> targetRadios = driver.findElements(By.xpath(
                "//label[normalize-space()='" + cityName + "' or contains(normalize-space(),'" + cityName + "')]/preceding-sibling::span//input[@type='radio'] | " +
                "//label[normalize-space()='" + cityName + "' or contains(normalize-space(),'" + cityName + "')]/preceding-sibling::input[@type='radio'] | " +
                "//label[normalize-space()='" + cityName + "' or contains(normalize-space(),'" + cityName + "')]/following-sibling::span//input[@type='radio'] | " +
                "//label[normalize-space()='" + cityName + "' or contains(normalize-space(),'" + cityName + "')]/following-sibling::input[@type='radio'] | " +
                "//label[normalize-space()='" + cityName + "' or contains(normalize-space(),'" + cityName + "')]//input[@type='radio'] | " +
                "//input[@type='radio' and (contains(translate(@value, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + cityName.toLowerCase() + "'))]"
        ));

        if (!targetRadios.isEmpty()) {
            WebElement radio = targetRadios.get(0);
            js.executeScript("arguments[0].scrollIntoView({block:'center'});", radio);
            js.executeScript("arguments[0].click();", radio);
            logger.info("Successfully selected city radio input for: {}", cityName);
            return;
        }

        // 3. Direct label match for target city
        List<WebElement> matchingLabels = driver.findElements(By.xpath(
                "//label[normalize-space()='" + cityName + "' or contains(normalize-space(),'" + cityName + "')]"
        ));

        if (!matchingLabels.isEmpty()) {
            WebElement label = matchingLabels.get(0);
            js.executeScript("arguments[0].scrollIntoView({block:'center'});", label);
            js.executeScript("arguments[0].click();", label);
            logger.info("Successfully clicked matching label for city: {}", cityName);
            return;
        }

        // 4. Scan through individual radio option child wrappers (not the top container)
        List<WebElement> cityOptions = driver.findElements(
                By.xpath("//div[contains(@class,'radio-wrap')]/*[self::div or self::span or self::label] | " +
                        "//div[contains(@class,'reverse-radio-selection-wrapper')]/*[self::div or self::span or self::label] | " +
                        "//form//label"));

        for (WebElement option : cityOptions) {
            try {
                String text = option.getText().trim();
                if (text.equalsIgnoreCase(cityName) ||
                    (cityName.equalsIgnoreCase("Mohali") && text.toLowerCase().contains("mohali")) ||
                    (cityName.equalsIgnoreCase("Kurali") && text.toLowerCase().contains("kurali")) ||
                    (cityName.equalsIgnoreCase("City A") && (text.equalsIgnoreCase("City A") || text.equalsIgnoreCase("City A Muncipal Corporation"))) ||
                    (cityName.equalsIgnoreCase("Delhi") && text.equalsIgnoreCase("Delhi"))) {

                    js.executeScript("arguments[0].scrollIntoView({block:'center'});", option);

                    List<WebElement> radioInputs = option.findElements(By.xpath(".//input[@type='radio'] | ./preceding-sibling::span//input[@type='radio']"));
                    if (!radioInputs.isEmpty()) {
                        WebElement radioInput = radioInputs.get(0);
                        js.executeScript("arguments[0].click();", radioInput);
                    } else {
                        js.executeScript("arguments[0].click();", option);
                    }
                    logger.info("Selected city option: '{}' for target: '{}'", text, cityName);
                    return;
                }
            } catch (Exception ignored) {}
        }

        // 5. Fallback to first available radio option only if no specific city was found
        List<WebElement> allRadios = driver.findElements(By.cssSelector("input[type='radio']"));
        if (!allRadios.isEmpty()) {
            WebElement firstRadio = allRadios.get(0);
            js.executeScript("arguments[0].scrollIntoView({block:'center'}); arguments[0].click();", firstRadio);
            logger.warn("Target city '{}' exact match not found, selected first radio option.", cityName);
            return;
        }

        throw new RuntimeException("Failed to select city: " + cityName);
    }

    public static void selectDropdownByIndex(WebDriver driver, WebDriverWait wait, JavascriptExecutor js, int dropdownIndex, int optionIndex)
            throws InterruptedException {

        List<WebElement> dropdowns = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(
                        By.cssSelector("div.select svg.cp")
                )
        );

        WebElement dropdown = dropdowns.get(dropdownIndex);

        js.executeScript("arguments[0].scrollIntoView({block:'center'});", dropdown);
        Thread.sleep(200);

        try {
            dropdown.click();
            Thread.sleep(1000);
        } catch (Exception e) {
            js.executeScript(
                    "var ev = document.createEvent('MouseEvents');" +
                            "ev.initEvent('click', true, true);" +
                            "arguments[0].dispatchEvent(ev);",
                    dropdown
            );
        }

        WebElement optionsContainer = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("div.options-card")
                )
        );

        List<WebElement> options = optionsContainer.findElements(
                By.cssSelector("div.profile-dropdown--item")
        );

        WebElement option = options.get(optionIndex);

        js.executeScript("arguments[0].scrollIntoView({block:'center'});", option);
        js.executeScript("arguments[0].click();", option);

        Thread.sleep(300);

    }
}