package org.upyog.Automation.Common;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.upyog.Automation.Base.BaseTest;
import org.upyog.Automation.Modules.CnD.CndVendor;
import org.upyog.Automation.Modules.RequestService.MobileToiletVendor;
import org.upyog.Automation.Modules.RequestService.WaterTankerVendor;
import org.upyog.Automation.Utils.DriverFactory;
import org.upyog.Automation.Utils.ModuleWrapper;

import java.time.Duration;

@Component
public class CommonVendorTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(CommonVendorTest.class);

    @Autowired
    private WaterTankerVendor waterTankerVendor;

    @Autowired
    private MobileToiletVendor mobileToiletVendor;

    @Autowired
    private CndVendor cndVendor;


    public void runVendorTest(
            String baseUrl,
            String moduleName,
            String mobileNumber,
            String otp,
            String city,
            String applicationNumber)
            throws InterruptedException {
        setUp();
        {
            logger.info("Driver after setup = " + driver);

            logger.info("Starting {} vendor test", moduleName);

            try {
                logger.info("MODULE RECEIVED = " + moduleName);
                switch (moduleName.toUpperCase()) {


                    case "WATER_TANKER":

                        ModuleWrapper.execute(
                                "WATER_TANKER",
                                () -> waterTankerVendor.waterTankerVCreate(
                                        driver,
                                        wait,
                                        js
                                )
                        );

                        break;


                    case "MOBILE_TOILET":

                        ModuleWrapper.execute(
                                "MOBILE_TOILET",
                                () -> mobileToiletVendor.mobileToiletVCreate(
                                        driver,
                                        wait,
                                        js
                                )
                        );

                        break;


                    case "CONSTRUCTION_AND_DEMOLITION":

                        ModuleWrapper.execute(
                                "CONSTRUCTION_AND_DEMOLITION",
                                () -> cndVendor.cndVendorFlow(
                                        driver,
                                        wait,
                                        js
                                )
                        );

                        break;

                    default:
                        logger.error("Unknown vendor module: {}", moduleName);
                        throw new RuntimeException("Unknown vendor module: " + moduleName);
                }

                logger.info("{} vendor test completed", moduleName);

            } catch (Exception e) {
                logger.error("Error in {} vendor test: {}", moduleName, e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }
}
