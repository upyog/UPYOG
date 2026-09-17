package org.upyog.Automation.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.Automation.Reports.ExtentManager;
import org.upyog.Automation.Reports.ReportManager;
import org.upyog.Automation.Utils.WorkflowDataStore;
import org.upyog.Automation.config.*;
import org.upyog.Automation.model.WorkflowData;
import org.upyog.Automation.model.WorkflowStep;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class WorkflowExecutor {

    private static final Logger logger =
            LoggerFactory.getLogger(WorkflowExecutor.class);

    @Autowired
    private CitizenTestService citizenTestService;

    @Autowired
    private EmployeeTestService employeeTestService;

    @Autowired
    private VendorTestService vendorTestService;

    public void executeWorkflow(
            String workflowPath,
            String stakeholderPath,
            String citizenUrl
    ) {
        String employeeUrl = citizenUrl;

        if (citizenUrl.contains("/citizen/login")) {
            employeeUrl = citizenUrl.replace(
                    "/citizen/login",
                    "/employee/login"
            );
        }
        logger.info("ENTERED executeWorkflow()");
        logger.info("Workflow Path : {}", workflowPath);
        logger.info("Stakeholder Path : {}", stakeholderPath);
        logger.info("Citizen URL : {}", citizenUrl);


        WorkflowData workflow =
                WorkflowConfigLoader.load(workflowPath);
        logger.info("Workflow Loaded : {}", workflow.getModuleName());
        WorkflowDataStore.remove("APPLICATION_NO");
        WorkflowDataStore.remove("WATER_APPLICATION_NO");
        WorkflowDataStore.remove("SEWERAGE_APPLICATION_NO");

        logger.info(
                "APPLICATION_NO RESET"
        );

        ModuleData stakeholder =
                StakeholderConfigLoader.load(
                        stakeholderPath
                );
        logger.info(
                "NEW REPORT CREATED FOR = "
                        + workflow.getModuleName()
        );
        ExtentManager.reset();
        ReportManager.clearTest();

        logger.info(
                "AFTER RESET TEST = "
                        + ReportManager.getTest()
        );

        ReportManager.startTest(
                workflow.getModuleName(),
                workflow.getModuleName()
        );
        try {
            for (WorkflowStep step : workflow.getSteps()) {
                try {
                    logger.info(
                            "STEP TYPE = " + step.getType()
                    );

                    logger.info(
                            "STEP MODULE = " + step.getModule()
                    );

                    logger.info(
                            "STEP ROLE = " + step.getRole()
                    );

                    ReportManager.logFlow(
                            step.getName()
                    );

                    ReportManager.logStep(
                            "Executing : " + step.getModule()
                    );

                    logger.info(
                            "Executing : "
                                    + step.getModule()
                    );

                    if ("CITIZEN".equalsIgnoreCase(step.getType())) {
                        logger.info(
                                "CALLING CITIZEN SERVICE"
                        );

                        citizenTestService.runCitizenSideTest(
                                citizenUrl,
                                step.getModule(),
                                stakeholder.getCitizen().getMobile(),
                                stakeholder.getCitizen().getOtp(),
                                stakeholder.getCitizen().getCity(),
                                null
                        );
                    } else if ("EMPLOYEE".equalsIgnoreCase(step.getType())) {

                        EmployeeData employee =
                                stakeholder.getEmployeeByRole(
                                        step.getRole()
                                );

                        if (employee == null) {

                            throw new RuntimeException(
                                    "Role not found in stakeholder file : "
                                            + step.getRole()
                            );
                        }
                        logger.info(
                                "APPLICATION_NO = "
                                        + WorkflowDataStore.get("APPLICATION_NO")
                        );

                        String applicationNo;

                        switch (step.getModule().toUpperCase()) {

                            case "WATER":
                            case "WATER_EMP":
                                applicationNo =
                                        WorkflowDataStore.get("WATER_APPLICATION_NO");
                                break;

                            case "SEWERAGE":
                            case "SEWERAGE_EMP":
                                applicationNo =
                                        WorkflowDataStore.get("SEWERAGE_APPLICATION_NO");
                                break;

                            default:
                                applicationNo =
                                        WorkflowDataStore.get("APPLICATION_NO");
                                break;
                        }

                        logger.info("Using Application No = {}", applicationNo);

                        if (applicationNo == null || applicationNo.isBlank()) {
                            throw new RuntimeException(
                                    "Application number not found."
                            );
                        }

                        if (!"INITIATOR".equalsIgnoreCase(step.getRole())) {

                            if (applicationNo == null || applicationNo.isBlank()) {
                                throw new RuntimeException(
                                        "APPLICATION_NO not found in WorkflowDataStore"
                                );
                            }
                        }

                        employeeTestService.runEmployeeTest(
                                employeeUrl,
                                step.getModule(),
                                employee.getUsername(),
                                employee.getPassword(),
                                applicationNo
                        );
                    }
                    else if ("VENDOR".equalsIgnoreCase(step.getType())) {

                        VendorData vendor = stakeholder.getVendor();

                        if (vendor == null) {
                            throw new RuntimeException("Vendor details not found");
                        }

                        String applicationNo =
                                WorkflowDataStore.get("APPLICATION_NO");

                        if (applicationNo == null || applicationNo.isBlank()) {
                            throw new RuntimeException(
                                    "APPLICATION_NO not found in WorkflowDataStore"
                            );
                        }

                        vendorTestService.runVendorTest(
                                citizenUrl,
                                step.getModule(),
                                vendor.getMobile(),
                                vendor.getOtp(),
                                vendor.getCity(),
                                applicationNo
                        );
                    }
                    ReportManager.logStep(
                            "PASSED : "
                                    + step.getModule()
                    );

                } catch (Exception e) {

                    ReportManager.logFailure(
                            "FAILED : "
                                    + step.getModule()
                                    + " | "
                                    + e.getMessage()
                    );

                    throw new RuntimeException(
                            "Workflow Failed at : "
                                    + step.getModule(),
                            e
                    );
                }
            }
        } finally {

            logger.info(
                    "ABOUT TO FLUSH = "
                            + workflow.getModuleName()
            );

            ReportManager.flush();

            logger.info(
                    "REPORT SAVED = "
                            + workflow.getModuleName()
            );

            ReportManager.clearTest();

            ExtentManager.reset();
        }
    }
}