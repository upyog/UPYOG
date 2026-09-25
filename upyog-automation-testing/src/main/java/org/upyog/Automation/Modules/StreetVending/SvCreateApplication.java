package org.upyog.Automation.Modules.StreetVending;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;
import org.upyog.Automation.engine.TestEngine;

@Component
public class SvCreateApplication {

    public void svCreateReg(
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
                    "test-config/streetVending/street_vending_citizen_module.json"
            );

        }catch(Exception e){

            throw new RuntimeException(
                    "Street Vending Citizen Failed",
                    e
            );
        }
    }
}

