package org.upyog.Automation.Modules.PropertyTax;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;
import org.upyog.Automation.engine.TestEngine;

@Component
public class PropertyTaxEmp {

    public void propertyInboxEmp(
            WebDriver driver,
            WebDriverWait wait,
            JavascriptExecutor js){

        try{

            TestEngine engine =
                    new TestEngine(
                            driver,
                            "config/dev.properties"
                    );

            engine.executeModule(
                    "test-config/propertyTax/property_tax_employee_module.json"
            );

        }catch(Exception e){

            throw new RuntimeException(
                    "Property Tax Employee Failed",
                    e
            );
        }
    }
}

//package org.upyog.Automation.Modules.PropertyTax;
//
//import org.openqa.selenium.By;
//import org.openqa.selenium.JavascriptExecutor;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.interactions.Actions;
//import org.openqa.selenium.support.ui.ExpectedConditions;
//import org.openqa.selenium.support.ui.WebDriverWait;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.upyog.Automation.Utils.ConfigReader;
//import org.upyog.Automation.Utils.DriverFactory;
//import org.upyog.Automation.config.WebDriverFactory;
//
//import java.util.List;
//
//@Component
//public class PropertyTaxEmp {
//
//    private static final Logger logger = LoggerFactory.getLogger(PropertyTaxEmp.class);
//
//    @Autowired
//    private WebDriverFactory webDriverFactory;
//
//    //@PostConstruct
//    public void propertyInbox() {
//        propertyInboxEmp(ConfigReader.get("employee.base.url"),
//                ConfigReader.get("property.login.username"),
//                ConfigReader.get("property.login.password"),
//                ConfigReader.get("propertyTax.application.number"));
//    }
//
//    public void propertyInboxEmp(String baseUrl, String username, String password, String applicationNumber) {
//        logger.info("Property Tax Application Employee Workflow");
//
//        // Initialize WebDriver using DriverFactory
//        WebDriver driver = webDriverFactory.createDriver();
//        WebDriverWait wait = DriverFactory.createWebDriverWait(driver);
//        JavascriptExecutor js = (JavascriptExecutor) driver;
//        Actions actions = new Actions(driver);
//
//        try {
//            // STEP 1: Employee Login
//            performEmployeeLogin(driver, wait, js, actions, baseUrl, username, password);
//
//            // STEP 2: Navigate to Search Application
//            navigateToSearchApplication(driver, wait, js);
//
//            // STEP 3: Search Property by Application Number
//            searchByPropertyID(driver, wait, js, applicationNumber);
//
//            // STEP 4: Take Action Verify
//            takeActionAndVerify(driver, wait, js);
//
//            // STEP 5: Pop Up Verify
//            handleVerifyPopup(driver, wait, js);
//
//            // STEP 6: Take Action Forward
//            takeActionAndForward(driver, wait, js);
//
//            // STEP 7: Pop Up Forward
//            handleForwardPopup(driver, wait, js);
//
//            // STEP 8: Take Action Approve
//            takeActionAndApprove(driver, wait, js);
//
//            // STEP 9: Pop Up Approve
//            handleApprovePopup(driver, wait, js);
//
//            // STEP 10: View Details
//            clickViewDetailsButton(driver, wait, js);
//
//            // STEP 11: Take Action Assess Property
//            takeActionAndAssessProperty(driver, wait, js);
//
//            // STEP 12: Click Assess Property
//            handleAssessPropertyButton(driver, wait);
//
//            // STEP 13: Click Collect Payment
//            fillPaymentAndCollect(driver, wait, js);
//
//
//            logger.info("Property Tax Application Employee Workflow completed successfully!");
//            Thread.sleep(50000); // Keep browser open for observation
//
//        } catch (Exception e) {
//            logger.info("Exception in Property Tax Application Employee Workflow: " + e.getMessage());
//
//        }finally {
//            if (driver != null) {
//                driver.quit();
//            }}
//    }
//
//    // =====================================================================
//    // STEP 1: EMPLOYEE LOGIN
//    // =====================================================================
//
//
//private void performEmployeeLogin(WebDriver driver, WebDriverWait wait, JavascriptExecutor js, Actions actions, String baseUrl, String username, String password) throws InterruptedException {
//    driver.get(baseUrl);
//    driver.manage().window().maximize();
//    logger.info("Open the Employee Login Portal");
//
//    // Enter credentials from configuration
//    fillInput(wait, "username", username);
//    fillInput(wait, "password", password);
//    logger.info("Filled username and password");
//
//    // Select city dropdown
//    selectCityDropdown(driver, wait, actions);
//
//    // Click Continue button
//    clickButton(wait, js, "//button[contains(@class, 'submit-bar') and .//header[text()='Continue']]");
//
//}
//    // =====================================================================
//    // STEP 2: SEARCH APPLICATION
//    // =====================================================================
//
//
//    private void navigateToSearchApplication(WebDriver driver, WebDriverWait wait, JavascriptExecutor js)
//            throws InterruptedException {
//        logger.info("Navigating to Search Property Application");
//
//        // Wait for page to load after login
//        Thread.sleep(2000);
//
//        // Click Search Application link
//        WebElement inboxLink = wait.until(ExpectedConditions.elementToBeClickable(
//                By.xpath("//span[text()='Property Tax']" +
//                        "/ancestor::div[contains(@class,'employeeCustomCard')]" +
//                        "//div[contains(@class,'employee-card-banner')]" +
//                        "//*[normalize-space()='Inbox']")));
//        js.executeScript("arguments[0].scrollIntoView(true);", inboxLink);
//        inboxLink.click();
//        logger.info("Clicked Inbox link");
//    }
//
//
//    // =====================================================================
//    // STEP 3: SEARCH PROPERTY BY PROPERTY ID
//    // =====================================================================
//
//
//    private void searchByPropertyID(WebDriver driver, WebDriverWait wait,
//                                    JavascriptExecutor js, String applicationNumber)
//            throws InterruptedException {
//
//        logger.info("Searching Property in Inbox");
//
//        wait.until(ExpectedConditions.urlContains("inbox"));
//        Thread.sleep(2000);
//
//        String propertyId = applicationNumber.trim();
//        logger.info("Using Property ID: " + propertyId);
//
//        WebElement propertyInput = null;
//
//        try {
//            propertyInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
//                    By.xpath("(//div[contains(@class,'search-complaint-container')]//input[@type='text'])[2]")
//            ));
//            logger.info("Found using index fallback locator");
//
//
//        } catch (Exception e1) {
//
//            try {
//                propertyInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
//                        By.xpath("//h4[text()='Property ID']/parent::span//input")
//                ));
//                logger.info("Found using label-based locator");
//
//            } catch (Exception e2) {
//
//                propertyInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
//                        By.name("propertyId")
//                ));
//                logger.info("Found using name locator");
//            }
//        }
//
//        js.executeScript("arguments[0].scrollIntoView({block:'center'});", propertyInput);
//        Thread.sleep(500);
//
//        propertyInput.clear();
//        propertyInput.sendKeys(propertyId);
//
//        logger.info("Property ID entered");
//
//        //SEARCH BUTTON
//
//        WebElement searchBtn = wait.until(ExpectedConditions.elementToBeClickable(
//                By.xpath("//button[normalize-space()='Search']")
//        ));
//
//        try {
//            searchBtn.click();
//        } catch (Exception e) {
//            js.executeScript("arguments[0].click();", searchBtn);
//        }
//
//        logger.info("Search button clicked");
//
//        By applicationLinkLocator = By.xpath("//a[contains(text(),'" + propertyId + "')]");
//
//        // CLICK RESULT
//
//        WebElement applicationLink = wait.until(ExpectedConditions.visibilityOfElementLocated(applicationLinkLocator));
//
//        js.executeScript("arguments[0].scrollIntoView({block:'center'});", applicationLink);
//        Thread.sleep(500);
//
//        try {
//            applicationLink.click();
//        } catch (Exception e) {
//            js.executeScript("arguments[0].click();", applicationLink);
//        }
//
//        logger.info("Application clicked: " + propertyId);
//    }
//
//    // =====================================================================
//    // STEP 4: TAKE ACTION VERIFY
//    // =====================================================================
//
//    private void takeActionAndVerify(WebDriver driver, WebDriverWait wait, JavascriptExecutor js)
//            throws InterruptedException {
//
//        logger.info("Starting Take Action → Verify");
//
//
//        // TAKE ACTION
//
//        clickTakeActionButton(driver, wait);
//        Thread.sleep(500);
//
//        //  WAIT FOR DROPDOWN
//
//        By dropdownLocator = By.xpath("//div[contains(@class,'menu-wrap')]");
//        wait.until(ExpectedConditions.visibilityOfElementLocated(dropdownLocator));
//
//        Thread.sleep(500);
//
//
//        // VERIFY CLICK
//
//        By verifyLocator = By.xpath("//p[normalize-space()='VERIFY']");
//
//        WebElement verifyBtn = wait.until(ExpectedConditions.elementToBeClickable(verifyLocator));
//
//        js.executeScript("arguments[0].scrollIntoView({block:'center'});", verifyBtn);
//        Thread.sleep(500);
//
//        try {
//            verifyBtn.click();
//        } catch (Exception e) {
//            js.executeScript("arguments[0].click();", verifyBtn);
//        }
//
//        logger.info("Verify clicked");
//    }
//
//    // =====================================================================
//    // STEP 5: POP UP VERIFY
//    // =====================================================================
//
//    private void handleVerifyPopup(WebDriver driver, WebDriverWait wait, JavascriptExecutor js)
//            throws InterruptedException {
//
//        logger.info("Handling Verify Popup");
//
//        // =========================
//        // STEP 1: WAIT FOR POPUP
//        // =========================
//        By popupLocator = By.xpath("//div[contains(@class,'popup-wrap')]");
//        wait.until(ExpectedConditions.visibilityOfElementLocated(popupLocator));
//
//        Thread.sleep(1000);
//
//        // =========================
//        // STEP 2: ENTER COMMENT
//        // =========================
//        By commentLocator = By.xpath("//textarea[@name='comments']");
//
//        WebElement commentBox = wait.until(ExpectedConditions.visibilityOfElementLocated(commentLocator));
//
//        js.executeScript("arguments[0].scrollIntoView({block:'center'});", commentBox);
//        Thread.sleep(500);
//
//        commentBox.clear();
//        commentBox.sendKeys("verified");
//
//        logger.info("Comment entered");
//
//        // =========================
//        // STEP 3: CLICK VERIFY BUTTON
//        // =========================
//        By verifyBtnLocator = By.xpath("//button[normalize-space()='Verify']");
//
//        WebElement verifyBtn = wait.until(ExpectedConditions.elementToBeClickable(verifyBtnLocator));
//
//        js.executeScript("arguments[0].scrollIntoView({block:'center'});", verifyBtn);
//        Thread.sleep(500);
//
//        try {
//            verifyBtn.click();
//        } catch (Exception e) {
//            js.executeScript("arguments[0].click();", verifyBtn);
//        }
//
//        logger.info("Final Verify clicked");
//    }
//
//    // =====================================================================
//    // STEP 6: TAKE ACTION FORWARD
//    // =====================================================================
//
//    private void takeActionAndForward(WebDriver driver, WebDriverWait wait, JavascriptExecutor js)
//            throws InterruptedException {
//
//        logger.info("Starting Take Action → Forward");
//
//
//        // TAKE ACTION
//
//        clickTakeActionButton(driver, wait);
//        Thread.sleep(500);
//
//
//        //  WAIT FOR DROPDOWN
//
//        By dropdownLocator = By.xpath("//div[contains(@class,'menu-wrap')]");
//        wait.until(ExpectedConditions.visibilityOfElementLocated(dropdownLocator));
//
//        Thread.sleep(500);
//
//
//        // FORWARD CLICK
//
//        By forwardLocator = By.xpath("//p[normalize-space()='FORWARD']");
//
//        WebElement forwardBtn = wait.until(ExpectedConditions.elementToBeClickable(forwardLocator));
//
//        js.executeScript("arguments[0].scrollIntoView({block:'center'});", forwardBtn);
//        Thread.sleep(500);
//
//        try {
//            forwardBtn.click();
//        } catch (Exception e) {
//            js.executeScript("arguments[0].click();", forwardBtn);
//        }
//
//        logger.info("Forward clicked");
//    }
//
//    // =====================================================================
//    // STEP 7: POP UP FORWARD
//    // =====================================================================
//
//    private void handleForwardPopup(WebDriver driver, WebDriverWait wait, JavascriptExecutor js)
//            throws InterruptedException {
//
//        logger.info("Handling Forward Popup");
//
//        // =========================
//        // STEP 1: WAIT FOR POPUP
//        // =========================
//        By popupLocator = By.xpath("//div[contains(@class,'popup-wrap')]");
//        wait.until(ExpectedConditions.visibilityOfElementLocated(popupLocator));
//
//        Thread.sleep(1000);
//
//        // =========================
//        // STEP 2: ENTER COMMENT
//        // =========================
//        By commentLocator = By.xpath("//textarea[@name='comments']");
//
//        WebElement commentBox = wait.until(ExpectedConditions.visibilityOfElementLocated(commentLocator));
//
//        js.executeScript("arguments[0].scrollIntoView({block:'center'});", commentBox);
//        Thread.sleep(500);
//
//        commentBox.clear();
//        commentBox.sendKeys("Forwarded");
//
//        logger.info("Comment entered");
//
//        // =========================
//        // STEP 3: CLICK FORWARD BUTTON
//        // =========================
//        By forwardBtnLocator = By.xpath("//button[normalize-space()='Forward']");
//
//        WebElement forwardBtn = wait.until(ExpectedConditions.elementToBeClickable(forwardBtnLocator));
//
//        js.executeScript("arguments[0].scrollIntoView({block:'center'});", forwardBtn);
//        Thread.sleep(500);
//
//        try {
//            forwardBtn.click();
//        } catch (Exception e) {
//            js.executeScript("arguments[0].click();", forwardBtn);
//        }
//
//        logger.info("Final Forward clicked");
//    }
//
//    // =====================================================================
//    // STEP 8: TAKE ACTION APPROVE
//    // =====================================================================
//
//    private void takeActionAndApprove(WebDriver driver, WebDriverWait wait, JavascriptExecutor js)
//            throws InterruptedException {
//
//        logger.info("Starting Take Action → Approve");
//
//
//        // TAKE ACTION
//
//        clickTakeActionButton(driver, wait);
//        Thread.sleep(500);
//
//
//        //  WAIT FOR DROPDOWN
//
//        By dropdownLocator = By.xpath("//div[contains(@class,'menu-wrap')]");
//        wait.until(ExpectedConditions.visibilityOfElementLocated(dropdownLocator));
//
//        Thread.sleep(500);
//
//
//        // FORWARD CLICK
//
//        By approveLocator = By.xpath("//p[normalize-space()='APPROVE']");
//
//        WebElement approveBtn = wait.until(ExpectedConditions.elementToBeClickable(approveLocator));
//
//        js.executeScript("arguments[0].scrollIntoView({block:'center'});", approveBtn);
//        Thread.sleep(500);
//
//        try {
//            approveBtn.click();
//        } catch (Exception e) {
//            js.executeScript("arguments[0].click();", approveBtn);
//        }
//
//        logger.info("Approve clicked");
//    }
//
//    // =====================================================================
//    // STEP 9: POP UP APPROVE
//    // =====================================================================
//
//    private void handleApprovePopup(WebDriver driver, WebDriverWait wait, JavascriptExecutor js)
//            throws InterruptedException {
//
//        logger.info("Handling Approve Popup");
//
//        // =========================
//        // STEP 1: WAIT FOR POPUP
//        // =========================
//        By popupLocator = By.xpath("//div[contains(@class,'popup-wrap')]");
//        wait.until(ExpectedConditions.visibilityOfElementLocated(popupLocator));
//
//        Thread.sleep(1000);
//
//        // =========================
//        // STEP 2: ENTER COMMENT
//        // =========================
//        By commentLocator = By.xpath("//textarea[@name='comments']");
//
//        WebElement commentBox = wait.until(ExpectedConditions.visibilityOfElementLocated(commentLocator));
//
//        js.executeScript("arguments[0].scrollIntoView({block:'center'});", commentBox);
//        Thread.sleep(500);
//
//        commentBox.clear();
//        commentBox.sendKeys("Approved");
//
//        logger.info("Comment entered");
//
//        // =========================
//        // STEP 3: CLICK APPROVE BUTTON
//        // =========================
//        By approveBtnLocator = By.xpath("//button[normalize-space()='Approve']");
//
//        WebElement approveBtn = wait.until(ExpectedConditions.elementToBeClickable(approveBtnLocator));
//
//        js.executeScript("arguments[0].scrollIntoView({block:'center'});", approveBtn);
//        Thread.sleep(500);
//
//        try {
//            approveBtn.click();
//        } catch (Exception e) {
//            js.executeScript("arguments[0].click();", approveBtn);
//        }
//
//        logger.info("Final Forward clicked");
//        Thread.sleep(2000);
//    }
//
//    // =====================================================================
//    // STEP 10: VIEW DETAILS
//    // =====================================================================
//
//    private void clickViewDetailsButton(WebDriver driver, WebDriverWait wait, JavascriptExecutor js)
//            throws InterruptedException {
//
//        WebElement viewDetailsButton = wait.until(ExpectedConditions.elementToBeClickable(
//                By.xpath("//button[normalize-space()='VIEW DETAILS']")
//        ));
//
//        js.executeScript("arguments[0].scrollIntoView({block:'center'});", viewDetailsButton);
//        Thread.sleep(300);
//
//        try {
//            viewDetailsButton.click();
//        } catch (Exception e) {
//            js.executeScript("arguments[0].click();", viewDetailsButton);
//        }
//
//        logger.info("Clicked VIEW DETAILS button");
//    }
//
//    // =====================================================================
//    // STEP 11: TAKE ACTION ASSESS PROPERTY
//    // =====================================================================
//
//    private void takeActionAndAssessProperty(WebDriver driver, WebDriverWait wait, JavascriptExecutor js)
//            throws InterruptedException {
//
//        logger.info("Starting Take Action → Assess Property");
//
//
//        // TAKE ACTION
//
//        clickTakeActionButton(driver, wait);
//        Thread.sleep(500);
//
//
//        //  WAIT FOR DROPDOWN
//
//        By dropdownLocator = By.xpath("//div[contains(@class,'menu-wrap')]");
//        wait.until(ExpectedConditions.visibilityOfElementLocated(dropdownLocator));
//
//        Thread.sleep(500);
//
//
//        // ASSESS PROPERTY CLICK
//
//        By assessPropertyLocator = By.xpath("//p[normalize-space()='Assess Property']");
//
//        WebElement assessPropertyBtn = wait.until(ExpectedConditions.elementToBeClickable(assessPropertyLocator));
//
//        js.executeScript("arguments[0].scrollIntoView({block:'center'});", assessPropertyBtn);
//        Thread.sleep(500);
//
//        try {
//            assessPropertyBtn.click();
//        } catch (Exception e) {
//            js.executeScript("arguments[0].click();", assessPropertyBtn);
//        }
//
//        logger.info("Assess Property clicked");
//
//
//        logger.info("Selecting Financial Year");
//
//        // WAIT FOR POPUP
//        wait.until(ExpectedConditions.visibilityOfElementLocated(
//                By.xpath("//div[contains(@class,'popup-wrap')]")
//        ));
//
//        Thread.sleep(1000);
//
//        // =========================
//        // SELECT YEAR (TEXT BASED)
//        // =========================
//
//        selectRadioButtonByLabel(driver, "2025-26");
//        Thread.sleep(1000);
//
//        // =========================
//        // CLICK ASSESS BUTTON
//        // =========================
//        By assessBtnLocator = By.xpath("//button[normalize-space()='Assess Property']");
//
//        WebElement assessBtn = wait.until(ExpectedConditions.elementToBeClickable(assessBtnLocator));
//
//        js.executeScript("arguments[0].scrollIntoView({block:'center'});", assessBtn);
//        Thread.sleep(500);
//
//        js.executeScript("arguments[0].click();", assessBtn);
//
//        logger.info("Assess Property clicked");
//    }
//
//    // =====================================================================
//    // STEP 12: ASSESS PROPERTY CLICK
//    // =====================================================================
//
//    private void handleAssessPropertyButton(WebDriver driver, WebDriverWait wait)
//        throws InterruptedException{
//        logger.info("Clicking Assess Property");
//
//        clickAssessPropertyButton(driver, wait);
//        Thread.sleep(2000);
//    }
//
//    // =====================================================================
//    // STEP 13: COLLECT PAYMENT
//    // =====================================================================
//
//    private void fillPaymentAndCollect(WebDriver driver, WebDriverWait wait, JavascriptExecutor js)
//            throws InterruptedException {
//
//        logger.info("Filling Payment Details");
//
//
//        // PAYER MOBILE
//
//        By mobileLocator = By.name("payerMobile");
//
//        WebElement mobileInput = wait.until(ExpectedConditions.visibilityOfElementLocated(mobileLocator));
//
//        js.executeScript("arguments[0].scrollIntoView({block:'center'});", mobileInput);
//        Thread.sleep(300);
//
//        mobileInput.clear();
//        mobileInput.sendKeys("9999999999");
//
//        logger.info("Payer Mobile entered");
//
//
//
//        // RECEIPT NUMBER
//
//        By receiptNoLocator = By.name("instrumentNumber");
//
//        WebElement receiptInput = wait.until(ExpectedConditions.visibilityOfElementLocated(receiptNoLocator));
//
//        js.executeScript("arguments[0].scrollIntoView({block:'center'});", receiptInput);
//        Thread.sleep(300);
//
//        receiptInput.clear();
//        receiptInput.sendKeys("GEN123456");
//
//        logger.info("Receipt Number entered");
//
//
//
//        // RECEIPT DATE
//
//        String currentDate = java.time.LocalDate.now().toString();
//
//        By dateLocator = By.xpath("//input[@type='date']");
//
//        WebElement dateInput = wait.until(ExpectedConditions.visibilityOfElementLocated(dateLocator));
//
//        js.executeScript("arguments[0].scrollIntoView({block:'center'});", dateInput);
//        Thread.sleep(300);
//
//        // Set current date
//        js.executeScript(
//                "arguments[0].value='" + currentDate + "';" +
//                        "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
//                dateInput
//        );
//
//        logger.info("Date entered: " + currentDate);
//
//
//
//        // CLICK COLLECT PAYMENT
//
//        By collectBtnLocator = By.xpath("//button[.//header[normalize-space()='Collect Payment']]");
//
//        WebElement collectBtn = wait.until(ExpectedConditions.elementToBeClickable(collectBtnLocator));
//
//        js.executeScript("arguments[0].scrollIntoView({block:'center'});", collectBtn);
//        Thread.sleep(500);
//
//        try {
//            collectBtn.click();
//        } catch (Exception e) {
//            js.executeScript("arguments[0].click();", collectBtn);
//        }
//
//        logger.info("Collect Payment clicked");
//    }
//
//    // =====================================================================
//    // UTILITY METHODS
//    // =====================================================================
//
//    private void fillInput(WebDriverWait wait, String fieldName, String value) {
//        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(By.name(fieldName)));
//        input.clear();
//        input.sendKeys(value);
//    }
//
//    /**
//     * Utility method to click buttons with XPath
//     */
//    private void clickButton(WebDriverWait wait, JavascriptExecutor js, String xpath) throws InterruptedException {
//        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
//        js.executeScript("arguments[0].scrollIntoView(true);", button);
//        Thread.sleep(300);
//        button.click();
//    }
//
//    /**
//     * Selects city dropdown during login
//     */
//    private void selectCityDropdown(WebDriver driver, WebDriverWait wait, Actions actions) throws InterruptedException {
//        WebElement cityDropdownContainer = driver.findElement(By.cssSelector("div.select"));
//        WebElement cityDropdownArrow = cityDropdownContainer.findElement(By.tagName("svg"));
//        actions.moveToElement(cityDropdownArrow).click().perform();
//
//        WebElement dropdownOptions = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.options-card")));
//        WebElement firstCityOption = dropdownOptions.findElement(By.cssSelector(".profile-dropdown--item:first-child"));
//        actions.moveToElement(firstCityOption).click().perform();
//    }
//
//    /**
//     * Clicks the TAKE ACTION button
//     */
//    private void clickTakeActionButton(WebDriver driver, WebDriverWait wait) throws InterruptedException {
//        WebElement takeActionButton = wait.until(ExpectedConditions.elementToBeClickable(
//                By.xpath("//button[contains(@class, 'submit-bar') and .//header[normalize-space()='TAKE ACTION']]")));
//        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", takeActionButton);
//        Thread.sleep(300);
//        takeActionButton.click();
//        logger.info("Clicked TAKE ACTION button");
//    }
//
//    /**
//     * Clicks the ASSESS PROPERTY button
//     */
//
//    private void clickAssessPropertyButton(WebDriver driver, WebDriverWait wait) throws InterruptedException {
//        WebElement assessPropertyButton = wait.until(ExpectedConditions.elementToBeClickable(
//                By.xpath("//button[contains(@class, 'submit-bar') and .//header[normalize-space()='Assess Property']]")));
//        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", assessPropertyButton);
//        Thread.sleep(300);
//        assessPropertyButton.click();
//        logger.info("Clicked ASSESS PROPERTY button");
//    }
//
//
//    /**
//     * Handles the take action menu and selects appropriate action
//     */
//    private void handleTakeActionMenu(WebDriver driver, WebDriverWait wait) throws InterruptedException {
//        try {
//            WebElement menuWrap = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.menu-wrap")));
//            List<WebElement> actionOptions = menuWrap.findElements(By.tagName("p"));
//
//            for (WebElement option : actionOptions) {
//                String text = option.getText().trim().toUpperCase();
//                if (text.equals("VERIFY")) {
//                    option.click();
//                    logger.info("Clicked VERIFY");
//                    handlePopupAndSubmit(driver, wait, "Automated verification comment.",
//                            ConfigReader.get("document.identity.proof"));
//                    break;
//                } else if (text.equals("FORWARD")) {
//                    option.click();
//                    logger.info("Clicked FORWARD");
//                    handlePopupAndSubmit(driver, wait, "Automated forward comment.",
//                            ConfigReader.get("document.identity.proof"));
//                    break;
//                } else if (text.equals("APPROVE")) {
//                    option.click();
//                    logger.info("Clicked APPROVE");
//                    handlePopupAndSubmit(driver, wait, "Automated approval comment.",
//                            ConfigReader.get("document.identity.proof"));
//                    break;
//                } else if (text.equals("PAY")) {
//                    option.click();
//                    logger.info("Clicked PAY");
//                    break;
//                } else if (text.equals("REJECT")) {
//                    logger.info("Application Rejected");
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            logger.info("Take Action Menu not found or no valid option present: " + e.getMessage());
//        }
//    }
//
//    /**
//     * Handles popup submission with comment and document upload
//     */
//    private void handlePopupAndSubmit(WebDriver driver, WebDriverWait wait, String comment, String filePath) throws InterruptedException {
//        // Enter comment
//        WebElement commentField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("comments")));
//        commentField.clear();
//        commentField.sendKeys(comment);
//
//        // Upload document
//        WebElement fileInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("workflow-doc")));
//        fileInput.sendKeys(filePath);
//        logger.info("Document uploaded");
//
//        // Click Verify or Approve button
//        List<WebElement> verifyButtons = driver.findElements(By.xpath("//button[contains(@class, 'selector-button-primary') and .//h2[normalize-space()='Verify']]"));
//        List<WebElement> approveButtons = driver.findElements(By.xpath("//button[contains(@class, 'selector-button-primary') and .//h2[normalize-space()='Approve']]"));
//
//        WebElement actionButton = null;
//        if (!verifyButtons.isEmpty()) {
//            actionButton = verifyButtons.get(0);
//            logger.info("Clicking Verify button");
//        } else if (!approveButtons.isEmpty()) {
//            actionButton = approveButtons.get(0);
//            logger.info("Clicking Approve button");
//        } else {
//            throw new RuntimeException("Neither Verify nor Approve button found!");
//        }
//
//        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", actionButton);
//        Thread.sleep(300);
//        actionButton.click();
//    }
//
//    private void selectRadioButtonByLabel(WebDriver driver, String labelText) {
//        try {
//            WebElement radio = null;
//
//            try {
//                radio = driver.findElement(By.xpath("//label[text()='" + labelText + "']/preceding-sibling::span/input"));
//            } catch (Exception e1) {
//                try {
//                    radio = driver.findElement(By.xpath("//label[contains(text(),'" + labelText + "')]/preceding-sibling::input"));
//                } catch (Exception e2) {
//                    try {
//                        radio = driver.findElement(By.xpath("//label[text()='" + labelText + "']/..//input[@type='radio']"));
//                    } catch (Exception e3) {
//                        try {
//                            radio = driver.findElement(By.xpath("//label[text()='" + labelText + "']/following-sibling::input[@type='radio']"));
//                        } catch (Exception e4) {
//                            radio = driver.findElement(By.xpath("//input[@type='radio'][@value='" + labelText + "']"));
//                        }
//                    }
//                }
//            }
//
//            if (radio != null && !radio.isSelected()) {
//                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", radio);
//                Thread.sleep(200);
//                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", radio);
//                logger.info("Selected radio button: " + labelText);
//            }
//        } catch (Exception e) {
//            logger.info("Error selecting radio button '" + labelText + "': " + e.getMessage());
//            throw new RuntimeException("Failed to select radio button: " + labelText, e);
//        }
//    }
//}
