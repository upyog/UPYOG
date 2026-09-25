package org.upyog.Automation.Modules.Pet;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;
import org.upyog.Automation.engine.TestEngine;

@Component
public class PetApplicationEmp {

    public void petInboxEmp(
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
                    "test-config/pet/pet_employee_module.json"
            );

        }catch(Exception e){

            throw new RuntimeException(
                    "PET Employee Failed",
                    e
            );
        }
    }
}