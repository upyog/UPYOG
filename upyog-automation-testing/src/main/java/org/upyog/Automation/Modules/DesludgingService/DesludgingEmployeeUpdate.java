package org.upyog.Automation.Modules.DesludgingService;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;
import org.upyog.Automation.engine.TestEngine;

@Component
public class DesludgingEmployeeUpdate {

    public void desludgingUpdate(
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
                    "test-config/desludging/desludging_employee_module.json"
            );

        }
        catch(Exception e) {

            throw new RuntimeException(
                    "EWaste Employee Flow Failed",
                    e
            );
        }
    }
}
