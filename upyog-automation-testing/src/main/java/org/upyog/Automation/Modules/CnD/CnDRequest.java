package org.upyog.Automation.Modules.CnD;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.springframework.stereotype.Component;

import org.upyog.Automation.engine.TestEngine;

@Component
public class CnDRequest {

    public void cndReg(
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
                    "test-config/cnd/cnd_citizen_module.json"
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "CnD Citizen Flow Failed",
                    e
            );
        }
    }
}
