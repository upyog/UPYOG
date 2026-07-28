package org.upyog.Automation.Modules.Pet;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.upyog.Automation.Utils.ConfigReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.upyog.Automation.config.WebDriverFactory;

/**
 * Automated test class for UPYOG Pet Application Search
 * This class handles the employee-side application search workflow including:
 * - Employee login and city selection
 * - Navigation to Search Application module
 * - Mobile number search functionality
 */
@Component
public class SearchApplication {

    private static final Logger logger = LoggerFactory.getLogger(SearchApplication.class);

    @Autowired
    private WebDriverFactory webDriverFactory;

    /**
     * Main test method for pet application search workflow
     * Runs automatically when Spring context is initialized
     */
    //@PostConstruct
    public void searchApplication() {
        logger.info("Pet Application Search by Employee");
        
        // Initialize WebDriver using DriverFactory
        WebDriver driver = webDriverFactory.createDriver();
        WebDriverWait wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(30));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Actions actions = new Actions(driver);

        try {
            // STEP 1: Employee Login
            performEmployeeLogin(driver, wait, js, actions);
            
            // STEP 2: Navigate to Search Application
            navigateToSearchApplication(driver, wait, js);
            
            // STEP 3: Search by Mobile Number
            searchByMobileNumber(driver, wait, js);
            
            logger.info("Pet Application Search completed successfully!");
            Thread.sleep(5000); // Keep browser open for observation
            
        } catch (Exception e) {
            logger.info("Exception in Pet Application Search: " + e.getMessage());

        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    /**
     * Handles employee login process
     */
    private void performEmployeeLogin(WebDriver driver, WebDriverWait wait, JavascriptExecutor js, Actions actions) throws InterruptedException {
        driver.get(ConfigReader.get("employee.base.url"));
        driver.manage().window().maximize();
        logger.info("Open the Employee Login Portal");

        // Enter credentials from configuration
        fillInput(wait, "username", ConfigReader.get("app.login.username"));
        fillInput(wait, "password", ConfigReader.get("app.login.password"));
        logger.info("Filled username and password");

        // Select city dropdown
        selectCityDropdown(driver, wait, actions);
        
        // Click Continue button
        clickButton(wait, js, "//button[contains(@class, 'submit-bar') and .//header[text()='Continue']]");
    }

    /**
     * Navigates to Search Application module
     */
    private void navigateToSearchApplication(WebDriver driver, WebDriverWait wait, JavascriptExecutor js) throws InterruptedException {
        logger.info("Navigating to Search Application");
        
        // Wait for page to load after login
        Thread.sleep(2000);
        
        // Click Search Application link
        WebElement searchAppLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[@href='/upyog-ui/employee/ptr/petservice/my-applications' and contains(normalize-space(),'Search Application')]")));
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", searchAppLink);
        Thread.sleep(300);
        searchAppLink.click();
        logger.info("Clicked Search Application link");
    }

    /**
     * Performs mobile number search
     */
    private void searchByMobileNumber(WebDriver driver, WebDriverWait wait, JavascriptExecutor js) throws InterruptedException {
        logger.info("Searching by mobile number");
        
        // Fill mobile number with retry logic
        String mobileNumber = ConfigReader.get("citizen.mobile.number");
        fillMobileNumberWithRetry(wait, mobileNumber);
        
        // Click Search button
        WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(@class, 'submit-bar') and @type='submit' and .//header[normalize-space()='Search']]")));
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", searchButton);
        Thread.sleep(300);
        searchButton.click();
        logger.info("Search completed");
    }

    // UTILITY METHODS

    /**
     * Utility method to fill input fields
     */
    private void fillInput(WebDriverWait wait, String fieldName, String value) {
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(By.name(fieldName)));
        input.clear();
        input.sendKeys(value);
    }

    /**
     * Utility method to click buttons with XPath
     */
    private void clickButton(WebDriverWait wait, JavascriptExecutor js, String xpath) throws InterruptedException {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
        js.executeScript("arguments[0].scrollIntoView(true);", button);
        Thread.sleep(300);
        button.click();
    }

    /**
     * Selects city dropdown during login
     */
    private void selectCityDropdown(WebDriver driver, WebDriverWait wait, Actions actions) throws InterruptedException {
        WebElement cityDropdownContainer = driver.findElement(By.cssSelector("div.select"));
        WebElement cityDropdownArrow = cityDropdownContainer.findElement(By.tagName("svg"));
        actions.moveToElement(cityDropdownArrow).click().perform();

        WebElement dropdownOptions = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.options-card")));
        WebElement firstCityOption = dropdownOptions.findElement(By.cssSelector(".profile-dropdown--item:first-child"));
        actions.moveToElement(firstCityOption).click().perform();
    }

    /**
     * Fills mobile number input with retry logic for stale element handling
     */
    private void fillMobileNumberWithRetry(WebDriverWait wait, String mobileNumber) throws InterruptedException {
        int maxAttempts = 3;
        int attempts = 0;
        
        while (attempts < maxAttempts) {
            try {
                logger.info("Attempt " + (attempts + 1) + ": Entering mobile number...");
                WebElement mobileInput = wait.until(ExpectedConditions.elementToBeClickable(
                        By.cssSelector("input.employee-card-input[name='mobileNumber']")));
                
                mobileInput.clear();
                
                // Enter mobile number digit by digit for better reliability
                for (char digit : mobileNumber.toCharArray()) {
                    mobileInput.sendKeys(String.valueOf(digit));
                    Thread.sleep(100);
                }
                
                logger.info("Mobile number entered successfully: " + mobileNumber);
                break; // Success, exit loop
                
            } catch (org.openqa.selenium.StaleElementReferenceException e) {
                attempts++;
                logger.info("StaleElementReferenceException on attempt " + attempts + ". Retrying...");
                if (attempts == maxAttempts) {
                    logger.info("Failed after " + maxAttempts + " attempts. Throwing exception.");
                    throw e;
                }
                Thread.sleep(500);
            }
        }
    }
}