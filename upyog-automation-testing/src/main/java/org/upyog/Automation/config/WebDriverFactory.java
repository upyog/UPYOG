package org.upyog.Automation.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class WebDriverFactory {

    private static final Logger logger =
            LoggerFactory.getLogger(WebDriverFactory.class);

    @Value("${selenium.grid.url:http://selenium-chrome:4444}")
    private String gridUrl;

    @Value("${selenium.grid.enabled:false}")
    private boolean gridEnabled;

    private static final List<WebDriver> activeDrivers =
            new ArrayList<>();

    public WebDriver createDriver() {

        try {

            closeAllDrivers();

            ChromeOptions options =
                    new ChromeOptions();

            options.addArguments("--remote-allow-origins=*");
            options.addArguments("--disable-blink-features=AutomationControlled");
            options.addArguments("--start-maximized");
            options.addArguments("--incognito");

            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");

            WebDriver driver;

            boolean isGrid =
                    Boolean.parseBoolean(
                            System.getProperty(
                                    "selenium.grid.enabled",
                                    String.valueOf(gridEnabled)
                            )
                    );

            String url =
                    System.getProperty(
                            "selenium.grid.url",
                            gridUrl
                    );

            if (isGrid) {

                logger.info(
                        "Running on GRID: " + url
                );

                driver =
                        new RemoteWebDriver(
                                new URL(url),
                                options
                        );

                ((RemoteWebDriver) driver)
                        .setFileDetector(
                                new org.openqa.selenium.remote.LocalFileDetector()
                        );

            } else {

                logger.info(
                        "Running on LOCAL Chrome"
                );

                // Selenium Manager automatically handles driver
                driver =
                        new ChromeDriver(options);
            }

            activeDrivers.add(driver);

            return driver;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to create WebDriver: "
                            + e.getMessage(),
                    e
            );
        }
    }

    public static void closeAllDrivers() {

        for (WebDriver driver : activeDrivers) {

            try {

                if (driver != null) {
                    driver.quit();
                }

            } catch (Exception ignored) {

            }
        }

        activeDrivers.clear();
    }
}