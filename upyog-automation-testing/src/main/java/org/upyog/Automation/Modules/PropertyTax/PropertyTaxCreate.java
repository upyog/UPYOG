package org.upyog.Automation.Modules.PropertyTax;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;
import org.upyog.Automation.engine.TestEngine;

@Component
public class PropertyTaxCreate {

    public void newPropertyReg(
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
                    "test-config/propertyTax/property_tax_citizen_module.json"
            );

        }catch(Exception e){

            throw new RuntimeException(
                    "Property Tax Citizen Failed",
                    e
            );
        }
    }
}

