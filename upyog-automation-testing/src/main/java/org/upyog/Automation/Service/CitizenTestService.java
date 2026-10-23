package org.upyog.Automation.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.Automation.Common.CommonCitizenTest;
import org.upyog.Automation.Reports.ReportManager;
import org.upyog.Automation.Utils.AutomationConstants;
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

        WorkflowDataStore.put(AutomationConstants.KEY_SELECTED_MOBILE, mobileNumber);
        WorkflowDataStore.put(AutomationConstants.KEY_SELECTED_OTP, otp);
        WorkflowDataStore.put(AutomationConstants.KEY_SELECTED_CITY, cityName);
        WorkflowDataStore.put(AutomationConstants.KEY_SELECTED_PERMIT_NO, permitNumber);

        logger.info("Permit stored: {}", permitNumber);

        WorkflowDataStore.put(AutomationConstants.KEY_SELECTED_URL, baseUrl);
        WorkflowDataStore.put(AutomationConstants.KEY_SELECTED_MODULE, moduleName);

        String env;
        if (baseUrl.toLowerCase().contains("sandbox")) {
            env = AutomationConstants.ENV_SANDBOX;
        } else if (baseUrl.toLowerCase().contains(AutomationConstants.ENV_NIUATT.toLowerCase())) {
            env = AutomationConstants.ENV_NIUATT;
        } else {
            env = AutomationConstants.ENV_UPYOG;
        }

        WorkflowDataStore.put(AutomationConstants.KEY_SELECTED_ENV, env);

        // Selecting City based on Url if not explicitly provided or default
        String prefix = env.toLowerCase();
        String envCity = ConfigReader.get(prefix + ".city");
        if (cityName == null || cityName.isBlank() || "Select City".equalsIgnoreCase(cityName)) {
            cityName = envCity != null ? envCity : "City A";
        }

        WorkflowDataStore.put(AutomationConstants.KEY_SELECTED_CITY, cityName);

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