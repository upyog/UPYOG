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

        String loginMobile = mobile;

        String env = WorkflowDataStore.get("selected.env");

        if ("ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM"
                .equalsIgnoreCase(moduleName)) {

            if ("NIUATT".equalsIgnoreCase(env)) {
                loginMobile =
                        ConfigReader.get("niuatt.architect.mobile");
            }
            else {
                loginMobile =
                        ConfigReader.get("upyog.architect.mobile");
            }
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
        logger.info("selecting cityyyyy");


        // ==========================
        // CITY
        // ==========================

        CommonActions.selectCity(driver,
                wait,
                js,
                city);


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