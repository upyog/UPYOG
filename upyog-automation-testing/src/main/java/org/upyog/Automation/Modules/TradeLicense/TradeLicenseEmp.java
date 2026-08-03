package org.upyog.Automation.Modules.TradeLicense;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;
import org.upyog.Automation.engine.TestEngine;

@Component
public class TradeLicenseEmp {

    public void tlInboxEmp(
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
                    "test-config/tradeLicense/trade_license_employee_module.json"
            );

        }catch(Exception e){

            throw new RuntimeException(
                    "Trade License Employee Failed",
                    e
            );
        }
    }
}


