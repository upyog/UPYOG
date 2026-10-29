package org.upyog.Automation.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.Automation.Common.CommonEmployeeTest;
import org.upyog.Automation.Reports.ReportManager;
import org.upyog.Automation.Utils.AutomationConstants;
import org.upyog.Automation.Utils.WorkflowDataStore;

@Service
public class EmployeeTestService {

    private static final Logger logger =
            LoggerFactory.getLogger(EmployeeTestService.class);

    @Autowired
    private CommonEmployeeTest commonEmployeeTest;

    public String runEmployeeTest(
            String baseUrl,
            String moduleName,
            String username,
            String password,
            String applicationNumber) {

        logger.info(
                "Report Test Object = {}",
                ReportManager.getTest()
        );

        boolean standaloneRun = false;

        if (!ReportManager.hasActiveTest()) {

            ReportManager.startTest(
                    moduleName,
                    moduleName
            );

            standaloneRun = true;
        }

        WorkflowDataStore.put(AutomationConstants.KEY_SELECTED_URL, baseUrl);
        WorkflowDataStore.put(AutomationConstants.KEY_SELECTED_USERNAME, username);
        WorkflowDataStore.put(AutomationConstants.KEY_SELECTED_PASSWORD, password);
        WorkflowDataStore.put(AutomationConstants.KEY_SELECTED_APPLICATION_NO, applicationNumber);

        String env;
        if (baseUrl.toLowerCase().contains("sandbox")) {
            env = AutomationConstants.ENV_SANDBOX;
        } else if (baseUrl.toLowerCase().contains(AutomationConstants.ENV_NIUATT.toLowerCase())) {
            env = AutomationConstants.ENV_NIUATT;
        } else {
            env = AutomationConstants.ENV_UPYOG;
        }

        WorkflowDataStore.put(AutomationConstants.KEY_SELECTED_ENV, env);

        logger.info("Selected ENV: {}", env);
        logger.info("Starting {} employee test", moduleName);

        try {

            commonEmployeeTest.runEmployeeTest(
                    baseUrl,
                    moduleName,
                    username,
                    password,
                    applicationNumber
            );

        } catch (Exception e) {

            logger.error(
                    "Error in employee test",
                    e
            );

            throw new RuntimeException(
                    "Employee Test Failed : "
                            + moduleName,
                    e
            );

        } finally {

            if (standaloneRun) {
                ReportManager.flush();
            }
        }

        return moduleName
                + " employee test completed successfully.";
    }
}