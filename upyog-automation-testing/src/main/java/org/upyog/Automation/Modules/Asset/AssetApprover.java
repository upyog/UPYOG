package org.upyog.Automation.Modules.Asset;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.springframework.stereotype.Component;

import org.upyog.Automation.engine.TestEngine;

@Component

public class AssetApprover {
    public void assetEmployeeApprover(
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
                    "test-config/asset/asset_employeeApprover_module.json"
            );

        }
        catch(Exception e) {

            throw new RuntimeException(
                    "Asset Employee Flow Failed",
                    e
            );
        }
    }
}
