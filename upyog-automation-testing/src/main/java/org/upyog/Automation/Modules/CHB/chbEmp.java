package org.upyog.Automation.Modules.CHB;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.springframework.stereotype.Component;

import org.upyog.Automation.engine.TestEngine;

@Component
public class chbEmp {

    public void chbInboxEmp(
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
                    "test-config/chb/chb_employee_module.json"
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "CHB Employee Flow Failed",
                    e
            );
        }
    }
}

