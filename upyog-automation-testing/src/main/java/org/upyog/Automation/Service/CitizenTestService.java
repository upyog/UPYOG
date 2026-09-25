package org.upyog.Automation.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.Automation.Common.CommonCitizenTest;
import org.upyog.Automation.Reports.ReportManager;
import org.upyog.Automation.Utils.ConfigReader;
import org.upyog.Automation.Utils.WorkflowDataStore;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CitizenTestService {

    private static final Logger logger =
            LoggerFactory.getLogger(CitizenTestService.class);

    @Autowired
    private CommonCitizenTest commonCitizenTest;

    public String runCitizenSideTest(String baseUrl,
                                     String moduleName,
                                     String mobileNumber,
                                     String otp,
                                     String cityName,
                                     String permitNumber) {

        boolean standaloneRun = false;

        if (!ReportManager.hasActiveTest()) {

            ReportManager.startTest(
                    moduleName,
                    moduleName
            );

            standaloneRun = true;
        }

        logger.info("Starting {} citizen test", moduleName);

        WorkflowDataStore.put("citizen.mobile.number", mobileNumber);
        WorkflowDataStore.put("selected.mobile", mobileNumber);

        WorkflowDataStore.put("test.otp", otp);
        WorkflowDataStore.put("selected.otp", otp);

        WorkflowDataStore.put("test.city.name", cityName);
        WorkflowDataStore.put("selected.city", cityName);

        WorkflowDataStore.put("selected.permitNo", permitNumber);

        logger.info("Permit stored: {}", permitNumber);

        WorkflowDataStore.put("base.url", baseUrl);
        WorkflowDataStore.put("selected.url", baseUrl);
        WorkflowDataStore.put("selected.module", moduleName);

        String env = baseUrl.contains("niuatt")
                ? "NIUATT"
                : "UPYOG";

        WorkflowDataStore.put("selected.env", env);

        // Selecting City based on Url
        if (cityName == null || cityName.isBlank()) {

            String prefix = env.toLowerCase();

            cityName = ConfigReader.get(prefix + ".city");
        }

        WorkflowDataStore.put("selected.city", cityName);

        try {

            // Multiple modules selected
            if (moduleName.contains(",")) {

                List<String> selectedModules =
                        Arrays.stream(moduleName.split(","))
                                .map(String::trim)
                                .collect(Collectors.toList());

                commonCitizenTest.runMultipleModules(
                        baseUrl,
                        selectedModules,
                        mobileNumber,
                        otp,
                        cityName,
                        permitNumber
                );

                return "Multiple modules executed successfully.";
            }

            // Single module selected
            else {

                commonCitizenTest.runCitizenTest(
                        baseUrl,
                        moduleName,
                        mobileNumber,
                        otp,
                        cityName,
                        permitNumber
                );

                return moduleName + " executed successfully.";
            }

        } catch (Exception e) {

            logger.error("Error in citizen test", e);

            throw new RuntimeException(e);
        }
        finally {

            if (standaloneRun) {
                ReportManager.flush();
            }
        }
    }
}