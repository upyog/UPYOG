package org.upyog.Automation.Modules.EWaste;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.springframework.stereotype.Component;

import org.upyog.Automation.engine.TestEngine;

@Component
public class EWasteCreate {

    public void eWasteReg(
            WebDriver driver,
            WebDriverWait wait,
            JavascriptExecutor js) {

        try {

            TestEngine engine =
                    new TestEngine(
                            driver,
                            "config/dev.properties"
                    );

            engine.executeModule(
                    "test-config/ewaste/ewaste_citizen_module.json"
            );

        }
        catch(Exception e) {

            throw new RuntimeException(
                    "EWaste Citizen Flow Failed",
                    e
            );
        }
    }
}
