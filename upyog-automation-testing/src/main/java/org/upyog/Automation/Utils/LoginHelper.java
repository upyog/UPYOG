package org.upyog.Automation.Utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LoginHelper {

    private static final Logger logger =
            LoggerFactory.getLogger(LoginHelper.class);

    public static void login(WebDriver driver,
                             WebDriverWait wait,
                             JavascriptExecutor js,
                             String baseUrl,
                             String mobile,
                             String otp,
                             String city,
                             String moduleName)
            throws InterruptedException {

        driver.get(baseUrl);

        // Handle language selection screen if redirected or displayed
        try {
            if (driver.getCurrentUrl().contains("select-language") ||
                !driver.findElements(By.xpath("//*[contains(text(),'Select Language') or contains(text(),'Choose Language')]")).isEmpty()) {
                logger.info("Language selection screen detected, selecting English and continuing...");
                List<WebElement> englishOptions = driver.findElements(By.xpath("//*[normalize-space()='English' or @value='en_IN']"));
                if (!englishOptions.isEmpty()) {
                    js.executeScript("arguments[0].click();", englishOptions.get(0));
                }
                List<WebElement> continueButtons = driver.findElements(By.xpath("//button[normalize-space()='CONTINUE' or normalize-space()='Continue']"));
                if (!continueButtons.isEmpty()) {
                    js.executeScript("arguments[0].click();", continueButtons.get(0));
                    Thread.sleep(1500);
                }
            }
        } catch (Exception e) {
            logger.warn("Non-fatal check for language selection: {}", e.getMessage());
        }

        String loginMobile = mobile;
        String loginCity = city;

        String env = WorkflowDataStore.get(AutomationConstants.KEY_SELECTED_ENV);

        if (moduleName != null) {
            String upperMod = moduleName.toUpperCase();
            if (upperMod.contains("OBPAS") || upperMod.contains("ONLINE_BUILDING_PLAN")) {
                if (AutomationConstants.ENV_NIUATT.equalsIgnoreCase(env)) {
                    loginMobile = ConfigReader.get("niuatt.architect.mobile");
                } else {
                    loginMobile = ConfigReader.get("upyog.architect.mobile");
                }
            }
        }

        if (loginCity != null && !loginCity.isBlank()) {
            WorkflowDataStore.put(AutomationConstants.KEY_SELECTED_CITY, loginCity);
        }

        // ==========================
        // MOBILE
        // ==========================
        CommonActions.fillInput(
                wait,
                "mobileNumber",
                loginMobile
        );

        // ==========================
        // CHECKBOX
        // ==========================
        WebElement checkbox = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector(
                                "input[type='checkbox'].form-field"
                        )
                )
        );

        if (!checkbox.isSelected()) {

            js.executeScript(
                    "arguments[0].click();",
                    checkbox
            );
        }


        // ==========================
        // NEXT
        // ==========================
        CommonActions.clickButtonByText(
                driver,
                wait,
                js,
                "Next"
        );

        // ==========================
        // OTP
        // ==========================
        List<WebElement> otpInputs = wait.until(
                ExpectedConditions
                        .visibilityOfAllElementsLocatedBy(
                                By.cssSelector(
                                    "input.input-otp"
                                )
                        )
        );

        for (int i = 0;
             i < otp.length() && i < otpInputs.size();
             i++) {

            otpInputs.get(i)
                    .sendKeys(
                            String.valueOf(
                                    otp.charAt(i)
                            )
                    );
        }



        // ==========================
        // OTP NEXT
        // ==========================
        CommonActions.clickButtonByText(
                driver,
                wait,
                js,
                "Next"
        );
        logger.info("selecting city: {}", loginCity);


        // ==========================
        // CITY
        // ==========================

        CommonActions.selectCity(driver,
                wait,
                js,
                loginCity);


        logger.info("City selected properly");

         //==========================
         //CONTINUE
         //==========================
        CommonActions.clickButtonByText(
                driver,
                wait,
                js,
                "Continue"
        );

        logger.info(
                "Citizen Login Completed"
        );

       }
}