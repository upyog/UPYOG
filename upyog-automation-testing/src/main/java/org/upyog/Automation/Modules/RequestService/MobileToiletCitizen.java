package org.upyog.Automation.Modules.RequestService;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;
import org.upyog.Automation.engine.TestEngine;

@Component
public class MobileToiletCitizen {

    public void mobileToiletCreate(
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
                    "test-config/requestService/mobile_toilet_citizen_module.json"
            );

        }catch(Exception e){

            throw new RuntimeException(
                    "Mobile Toilet Citizen Failed",
                    e
            );
        }
    }
}