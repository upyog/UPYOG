package org.upyog.Automation.Modules.TradeLicense;

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

import java.util.List;

/**
 * Automated test class for UPYOG Trade License Employee Inbox Workflow
 * This class handles the complete employee-side trade license processing including:
 * - Employee login and city selection
 * - Navigation to Trade License Inbox
 * - Application search and selection
 * - Complete workflow: Verify and Forward -> Approve -> Pay
 * - Payment collection
 */
@Component
public class InboxEmpTl {

    private static final Logger logger = LoggerFactory.getLogger(InboxEmpTl.class);

    @Autowired
    private WebDriverFactory webDriverFactory;

    /**
     * Main test method for trade license employee workflow
     * Runs automatically when Spring context is initialized
     */
    //@PostConstruct

    public void inboxEmpTl() {
        inboxEmpTl(ConfigReader.get("employee.base.url"),
                ConfigReader.get("app.login.username"),
                ConfigReader.get("app.login.password"),
                ConfigReader.get("tl.application.number"));
    }
    public void inboxEmpTl(String baseUrl, String username, String password, String applicationNumber) {
        logger.info("Trade License Employee Inbox Workflow");
        
        // Initialize WebDriver using DriverFactory
        WebDriver driver = webDriverFactory.createDriver();
        WebDriverWait wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(30));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Actions actions = new Actions(driver);

        try {
            // STEP 1: Employee Login
            performEmployeeLogin(driver, wait, js, actions, baseUrl, username, password);
            
            // STEP 2: Navigate to Trade License Inbox
            navigateToInbox(driver, wait, js);
            
            // STEP 3: Search and Select Application
            searchAndSelectApplication(driver, wait);
            
            // STEP 4: Execute Complete Workflow
            runWorkflow(driver, wait);
            
            // STEP 5: Collect Payment
            collectPayment(driver, wait);
            
            logger.info("Trade License Employee Workflow completed successfully!");
            Thread.sleep(50000); // Keep browser open for observation
            
        } catch (Exception e) {
            logger.info("Exception in Trade License Employee Workflow: " + e.getMessage());

        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    /**
     * Handles employee login process
     */
    private void performEmployeeLogin(WebDriver driver, WebDriverWait wait, JavascriptExecutor js, Actions actions, String baseUrl, String username, String password) throws InterruptedException {
        driver.get(baseUrl);
        driver.manage().window().maximize();
        logger.info("Open the Employee Login Portal");

        // Enter credentials from configuration
        fillInput(wait, "username", username);
        fillInput(wait, "password", password);
        logger.info("Filled username and password");

        // Select city dropdown
        selectCityDropdown(driver, wait, actions);
        
        // Click Continue button
        WebElement continueButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(@class, 'submit-bar') and .//header[text()='Continue']]")));
        js.executeScript("arguments[0].scrollIntoView(true);", continueButton);
        continueButton.click();
    }

    /**
     * Navigates to Trade License Inbox
     */
    private void navigateToInbox(WebDriver driver, WebDriverWait wait, JavascriptExecutor js) throws InterruptedException {
        logger.info("Navigating to Trade License Inbox");
        
        Thread.sleep(2000);
        
        WebElement tlInbox = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//a[contains(@href, '/upyog-ui/employee/tl/inbox') and contains(normalize-space(.), 'Inbox')]")));
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", tlInbox);
        Thread.sleep(300);
        js.executeScript("arguments[0].click();", tlInbox);
        logger.info("Clicked Trade License Inbox");
    }

    /**
     * Searches for and selects application
     */
    private void searchAndSelectApplication(WebDriver driver, WebDriverWait wait) throws InterruptedException {
        logger.info("Searching and selecting application");
        
        Thread.sleep(1000);
        
        // Enter application number
        String appNumber = ConfigReader.get("tl.application.number");
        WebElement appNumberInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.className("employee-card-input")));
        appNumberInput.clear();
        appNumberInput.sendKeys(appNumber);

        // Click search button
        WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.className("submit-bar-search")));
        searchButton.click();

        // Click on application link
        WebElement appLink = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.linkText(appNumber)));
        appLink.click();
        logger.info("Selected application: " + appNumber);
    }

    /**
     * Executes the complete workflow: Verify and Forward -> Approve -> Pay
     */
    private void runWorkflow(WebDriver driver, WebDriverWait wait) throws InterruptedException {
        logger.info("Executing complete workflow");
        
        verifyAndForwardStep(driver, wait); // 1st Verify and Forward
        verifyAndForwardStep(driver, wait); // 2nd Verify and Forward
        approveStep(driver, wait);          // Approve
        payStep(driver, wait);              // Pay
        
        logger.info("Full workflow completed successfully!");
    }

    /**
     * Handles payment collection
     */
    private void collectPayment(WebDriver driver, WebDriverWait wait) {
        logger.info("Collecting payment");
        
        // Enter mobile number for payment
        WebElement mobileInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("payerMobile")));
        mobileInput.clear();
        mobileInput.sendKeys("9847584944");

        // Click Collect Payment button
        WebElement collectPaymentButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(@class, 'submit-bar') and .//header[normalize-space()='Collect Payment']]")));
        collectPaymentButton.click();
        logger.info("Payment collected");
    }

    // WORKFLOW STEP METHODS

    /**
     * Executes Verify and Forward step
     */
    private void verifyAndForwardStep(WebDriver driver, WebDriverWait wait) throws InterruptedException {
        clickTakeActionButton(driver, wait);
        selectMenuOption(driver, wait, "VERIFY AND FORWARD");
        fillComments(driver, wait, "verify");
        clickActionButton(driver, wait, "Verify and Forward");
    }

    /**
     * Executes Approve step
     */
    private void approveStep(WebDriver driver, WebDriverWait wait) throws InterruptedException {
        clickTakeActionButton(driver, wait);
        selectMenuOption(driver, wait, "APPROVE");
        fillComments(driver, wait, "approve");
        clickActionButton(driver, wait, "Approve");
    }

    /**
     * Executes Pay step
     */
    private void payStep(WebDriver driver, WebDriverWait wait) throws InterruptedException {
        clickTakeActionButton(driver, wait);
        selectMenuOption(driver, wait, "PAY");
        logger.info("Clicked PAY in workflow");
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
     * Selects city dropdown during login
     */
    private void selectCityDropdown(WebDriver driver, WebDriverWait wait, Actions actions) throws InterruptedException {
        WebElement cityDropdownContainer = driver.findElement(By.cssSelector("div.select"));
        WebElement cityDropdownArrow = cityDropdownContainer.findElement(By.tagName("svg"));
        actions.moveToElement(cityDropdownArrow).click().perform();

        WebElement dropdownOptions = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("div.options-card")));
        WebElement firstCityOption = dropdownOptions.findElement(By.cssSelector(".profile-dropdown--item:first-child"));
        actions.moveToElement(firstCityOption).click().perform();
    }

    /**
     * Clicks the TAKE ACTION button
     */
    private void clickTakeActionButton(WebDriver driver, WebDriverWait wait) throws InterruptedException {
        WebElement takeActionButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(@class, 'submit-bar') and .//header[normalize-space()='TAKE ACTION']]")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", takeActionButton);
        Thread.sleep(300);
        takeActionButton.click();
        logger.info("Clicked TAKE ACTION button");
    }

    /**
     * Selects menu option from take action menu
     */
    private void selectMenuOption(WebDriver driver, WebDriverWait wait, String optionText) throws InterruptedException {
        WebElement menuWrap = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.menu-wrap")));
        List<WebElement> actionOptions = menuWrap.findElements(By.tagName("p"));
        
        boolean found = false;
        for (WebElement option : actionOptions) {
            if (option.getText().trim().equalsIgnoreCase(optionText)) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", option);
                Thread.sleep(200);
                option.click();
                logger.info("Clicked menu option: " + optionText);
                found = true;
                break;
            }
        }
        if (!found) throw new RuntimeException("Menu option '" + optionText + "' not found!");
    }

    /**
     * Fills comments in the workflow popup
     */
    private void fillComments(WebDriver driver, WebDriverWait wait, String comment) {
        WebElement textarea = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("comments")));
        textarea.clear();
        textarea.sendKeys(comment);
        logger.info("Filled comments: " + comment);
    }

    /**
     * Clicks action button in workflow popup
     */
    private void clickActionButton(WebDriver driver, WebDriverWait wait, String btnText) throws InterruptedException {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(@class,'selector-button-primary')][.//h2[normalize-space()='" + btnText + "']]")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", btn);
        Thread.sleep(200);
        
        try {
            btn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
        logger.info("Clicked action button: " + btnText);
    }
}