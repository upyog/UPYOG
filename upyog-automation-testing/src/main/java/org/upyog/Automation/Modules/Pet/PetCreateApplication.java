package org.upyog.Automation.Modules.Pet;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;
import org.upyog.Automation.engine.TestEngine;

@Component
public class PetCreateApplication {

    public void petApptest(
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
                    "test-config/pet/pet_citizen_module.json"
            );

        }catch(Exception e){

            throw new RuntimeException(
                    "Pet Citizen Flow Failed",
                    e
            );
        }
    }
}

