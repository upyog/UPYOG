package org.upyog.Automation.Utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.upyog.Automation.Reports.ReportManager;

import java.util.List;

public class CommonActions {

    public static void fillInput(WebDriverWait wait, String fieldName, String value) {
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(By.name(fieldName)));
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

        WebElement radio = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath(
                                "//*[normalize-space()='" + labelText + "']" +
                                        "/ancestor::*[contains(@class,'radio-wrap')]" +
                                        "//input[@type='radio']"
                        )
                )
        );

        js.executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                radio
        );

        js.executeScript(
                "arguments[0].click();",
                radio
        );
    }

    public static void clickButtonByText(WebDriver driver, WebDriverWait wait, JavascriptExecutor js, String text) {

        By locator = By.xpath("//button[.//header[text()='" + text + "']]");

        WebElement button = wait.until(ExpectedConditions.presenceOfElementLocated(locator));

        js.executeScript("arguments[0].scrollIntoView({block:'center'});", button);

        try {
            wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", button);
        }
    }

    public static void selectCity(WebDriver driver,
                                  WebDriverWait wait,
                                  JavascriptExecutor js,
                                  String cityName) {

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("div.radio-wrap.reverse-radio-selection-wrapper")));

        List<WebElement> cityOptions = driver.findElements(
                By.cssSelector("div.radio-wrap.reverse-radio-selection-wrapper div"));

        for (WebElement option : cityOptions) {

            WebElement label = option.findElement(By.tagName("label"));

            if (label.getText().trim().equals(cityName)) {

                WebElement radioInput = option.findElement(
                        By.cssSelector("input[type='radio']")
                );

                if (!radioInput.isSelected()) {

                    js.executeScript("arguments[0].click();", radioInput);

                    wait.until(driver1 -> radioInput.isSelected());
                }

                return;
            }
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