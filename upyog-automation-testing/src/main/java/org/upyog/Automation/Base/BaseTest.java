package org.upyog.Automation.Base;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.upyog.Automation.Utils.*;


import java.time.Duration;

public class BaseTest {

    private static final Logger logger =
            LoggerFactory.getLogger(BaseTest.class);

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected JavascriptExecutor js;

    public void setUp() throws InterruptedException {

        driver = DriverFactory.createChromeDriver();
        logger.info("Driver created = " + driver);

        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        logger.info("Wait created");

        js = (JavascriptExecutor) driver;
        logger.info("JS created");

        logger.info("selected.url = " + WorkflowDataStore.get("selected.url"));


        String baseUrl =
                WorkflowDataStore.get("selected.url");

        String mobile =
                WorkflowDataStore.get("selected.mobile");

        String otp =
                WorkflowDataStore.get("selected.otp");

        String city =
                WorkflowDataStore.get("selected.city");

        String moduleName =
                WorkflowDataStore.get("selected.module");

        // Fallback only if HTML didn't send values
        if (baseUrl == null)
            baseUrl = ConfigReader.get("citizen.base.url");

        if (mobile == null)
            mobile = ConfigReader.get("user.mobile");

        if (otp == null)
            otp = ConfigReader.get("user.otp");

        if (city == null)
            city = ConfigReader.get("city.name");

        if (moduleName == null)
            moduleName = ConfigReader.get("obpas.module");

        logger.info("LOGIN URL = {}", baseUrl);
        logger.info("MOBILE = {}", mobile);
        logger.info("CITY = {}", city);

        LoginHelper.login(
                driver,
                wait,
                js,
                baseUrl,
                mobile,
                otp,
                city,
                moduleName
        );
        logger.info("HTML selected.url = {}",
                WorkflowDataStore.get("selected.url"));
    }

    public void tearDown() throws InterruptedException {
        Thread.sleep(15000);
        if (driver != null) {
            driver.quit();
        }
    }

    public WebDriver getDriver() {
        return driver;
    }
}