package org.upyog.Automation.Modules.Adv;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.springframework.stereotype.Component;

import org.upyog.Automation.engine.TestEngine;

@Component
public class AdvEmp {

    public void advApproval(
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
                    "test-config/advertisement/adv_employee_module.json"
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Advertisement Employee Flow Failed",
                    e
            );
        }
    }
}