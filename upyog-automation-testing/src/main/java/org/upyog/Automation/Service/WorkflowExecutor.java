package org.upyog.Automation.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.upyog.Automation.Reports.ExtentManager;
import org.upyog.Automation.Reports.ReportManager;
import org.upyog.Automation.Utils.AutomationConstants;
import org.upyog.Automation.Utils.ConfigReader;
import org.upyog.Automation.Utils.WorkflowDataStore;
import org.upyog.Automation.model.WorkflowData;
import org.upyog.Automation.model.WorkflowStep;
import org.upyog.Automation.config.*;

/**
 * Service responsible for executing configured UPYOG workflows.
 *
 * <p>The executor processes workflow steps based on their stakeholder type,
 * such as Citizen, Employee, or Vendor. It loads workflow and stakeholder
 * configurations from JSON files and delegates the actual execution to
 * the corresponding stakeholder test service.</p>
 *
 * <p>This class provides the common workflow execution mechanism so that
 * individual module classes do not need to duplicate workflow setup,
 * execution, reporting, and cleanup logic.</p>
 */
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

    /**
     * Executes a configured workflow for the selected module.
     *
     * <p>The method performs the following operations:</p>
     * <ul>
     *     <li>Resolves citizen and employee URLs.</li>
     *     <li>Loads the workflow configuration.</li>
     *     <li>Loads stakeholder details.</li>
     *     <li>Initializes the test report.</li>
     *     <li>Executes each workflow step according to its stakeholder type.</li>
     *     <li>Flushes and clears reporting resources after execution.</li>
     * </ul>
     *
     * @param workflowPath path of the workflow JSON configuration file
     * @param stakeholderPath path of the stakeholder JSON configuration file
     * @param citizenUrl base URL used for the workflow execution
     * @throws RuntimeException if the workflow or any workflow step fails
     */
    public void executeWorkflow(
            String workflowPath,
            String stakeholderPath,
            String citizenUrl
    ) {

        String citizenBaseUrl;
        String employeeUrl;

        /*
         * Resolve both citizen and employee URLs from the URL provided
         * by the caller so that the workflow can execute both stakeholder
         * types when required.
         */
        if (citizenUrl.contains(AutomationConstants.EMPLOYEE_LOGIN_PATH)) {

            employeeUrl = citizenUrl;

            citizenBaseUrl =
                    citizenUrl.replace(
                            AutomationConstants.EMPLOYEE_LOGIN_PATH,
                            AutomationConstants.CITIZEN_LOGIN_PATH
                    );

        } else {

            citizenBaseUrl = citizenUrl;

            employeeUrl =
                    citizenUrl.replace(
                            AutomationConstants.CITIZEN_LOGIN_PATH,
                            AutomationConstants.EMPLOYEE_LOGIN_PATH
                    );
        }

        logger.info("Entering workflow execution.");
        logger.info("Workflow Path: {}", workflowPath);
        logger.info("Stakeholder Path: {}", stakeholderPath);
        logger.info("Citizen URL: {}", citizenUrl);

        WorkflowData workflow =
                WorkflowConfigLoader.load(workflowPath);

        logger.info(
                "Workflow loaded successfully: {}",
                workflow.getModuleName()
        );

        /*
         * Clear application numbers from the previous workflow execution
         * to prevent stale runtime data from being reused.
         */
        WorkflowDataStore.remove(
                AutomationConstants.APPLICATION_NO
        );

        WorkflowDataStore.remove(
                AutomationConstants.WATER_APPLICATION_NO
        );

        WorkflowDataStore.remove(
                AutomationConstants.SEWERAGE_APPLICATION_NO
        );

        logger.info("Workflow application numbers reset.");

        ModuleData stakeholder =
                StakeholderConfigLoader.load(
                        stakeholderPath
                );

        logger.info(
                "Creating new report for module: {}",
                workflow.getModuleName()
        );

        /*
         * Reset the existing report state before starting a new workflow
         * so that results from a previous execution are not carried over.
         */
        ExtentManager.reset();
        ReportManager.clearTest();

        logger.info(
                "Report state reset successfully."
        );

        ReportManager.startTest(
                workflow.getModuleName(),
                workflow.getModuleName()
        );

        try {

            for (WorkflowStep step : workflow.getSteps()) {

                try {

                    logger.info(
                            "Step Type: {}",
                            step.getType()
                    );

                    logger.info(
                            "Step Module: {}",
                            step.getModule()
                    );

                    logger.info(
                            "Step Role: {}",
                            step.getRole()
                    );

                    ReportManager.logFlow(
                            step.getName()
                    );

                    ReportManager.logStep(
                            "Executing: " + step.getModule()
                    );

                    logger.info(
                            "Executing module: {}",
                            step.getModule()
                    );

                    /*
                     * Citizen workflow steps are delegated to the common
                     * CitizenTestService using the citizen configuration.
                     */
                    if ("CITIZEN".equalsIgnoreCase(step.getType())) {

                        logger.info(
                                "Executing Citizen workflow step: {}",
                                step.getModule()
                        );

                        citizenTestService.runCitizenSideTest(
                                citizenBaseUrl,
                                step.getModule(),
                                stakeholder.getCitizen().getMobile(),
                                stakeholder.getCitizen().getOtp(),
                                stakeholder.getCitizen().getCity(),
                                null
                        );

                    }

                    /*
                     * Employee workflow steps use the employee details
                     * configured for the role specified in the workflow.
                     */
                    else if ("EMPLOYEE".equalsIgnoreCase(step.getType())) {

                        EmployeeData employee =
                                stakeholder.getEmployeeByRole(
                                        step.getRole()
                                );

                        if (employee == null) {

                            throw new RuntimeException(
                                    "Role not found in stakeholder file: "
                                            + step.getRole()
                                );
                        }

                        logger.info(
                                "APPLICATION_NO: {}",
                                WorkflowDataStore.get(
                                        AutomationConstants.APPLICATION_NO
                                )
                        );

                        String applicationNo;

                        /*
                          * Water and Sewerage workflows maintain separate
                          * application numbers because they generate
                          * different application references.
                          */
                        switch (step.getModule().toUpperCase()) {

                            case AutomationConstants.MODULE_WATER:
                            case AutomationConstants.MODULE_WATER_EMP:

                                applicationNo =
                                        WorkflowDataStore.get(
                                                AutomationConstants.WATER_APPLICATION_NO
                                        );

                                break;

                            case AutomationConstants.MODULE_SEWERAGE:
                            case AutomationConstants.MODULE_SEWERAGE_EMP:

                                applicationNo =
                                        WorkflowDataStore.get(
                                                AutomationConstants.SEWERAGE_APPLICATION_NO
                                        );

                                break;

                            default:

                                applicationNo =
                                        WorkflowDataStore.get(
                                                AutomationConstants.APPLICATION_NO
                                        );

                                break;
                        }

                        logger.info(
                                "Using Application No: {}",
                                applicationNo
                        );

                        /*
                         * Initiator steps may create the application number
                         * themselves, so the application number is not required
                         * before executing those steps.
                         */
                        if (!"INITIATOR".equalsIgnoreCase(step.getRole())) {

                            if (applicationNo == null
                                    || applicationNo.isBlank()) {

                                throw new RuntimeException(
                                        "Application number not found in "
                                                + "WorkflowDataStore for module: "
                                                + step.getModule()
                                );
                            }
                        }

                        String envPrefix = employeeUrl.toLowerCase().contains("sandbox") ? "sandbox"
                                : (employeeUrl.toLowerCase().contains("niuatt") ? "niuatt" : "upyog");

                        String resolvedUsername = resolveEmployeeUsername(envPrefix, step.getModule(), step.getRole(), employee.getUsername());
                        String resolvedPassword = resolveEmployeePassword(envPrefix, step.getModule(), step.getRole(), employee.getPassword());

                        logger.info("Executing Employee step: [{}] role: [{}] on env: [{}] with username: [{}]",
                                step.getModule(), step.getRole(), envPrefix, resolvedUsername);

                        employeeTestService.runEmployeeTest(
                                employeeUrl,
                                step.getModule(),
                                resolvedUsername,
                                resolvedPassword,
                                applicationNo
                        );
                    }

                    /*
                     * Vendor workflow steps use the vendor details from
                     * the stakeholder configuration.
                     */
                    else if ("VENDOR".equalsIgnoreCase(step.getType())) {

                        VendorData vendor =
                                stakeholder.getVendor();

                        if (vendor == null) {

                            throw new RuntimeException(
                                    "Vendor details not found"
                            );
                        }

                        String applicationNo =
                                WorkflowDataStore.get(
                                        AutomationConstants.APPLICATION_NO
                                );

                        if (applicationNo == null
                                || applicationNo.isBlank()) {

                            throw new RuntimeException(
                                    "Application number not found in "
                                            + "WorkflowDataStore"
                            );
                        }

                        vendorTestService.runVendorTest(
                                citizenBaseUrl,
                                step.getModule(),
                                vendor.getMobile(),
                                vendor.getOtp(),
                                vendor.getCity(),
                                applicationNo
                        );
                    }

                    ReportManager.logStep(
                            "PASSED: " + step.getModule()
                    );

                } catch (Exception e) {

                    /*
                     * Log the complete exception and report the failed
                     * workflow step before stopping the workflow.
                     */
                    logger.error(
                            "Workflow step failed: {}",
                            step.getModule(),
                            e
                    );

                    ReportManager.logFailure(
                            "FAILED: "
                                    + step.getModule()
                                    + " | "
                                    + e.getMessage()
                    );

                    throw new RuntimeException(
                            "Workflow failed at: "
                                    + step.getModule(),
                            e
                    );
                }
            }

        } finally {

            /*
             * Always flush the report and reset reporting state,
             * including when the workflow fails.
             */
            logger.info(
                    "Flushing report for module: {}",
                    workflow.getModuleName()
            );

            ReportManager.flush();

            logger.info(
                    "Report saved for module: {}",
                    workflow.getModuleName()
            );

            ReportManager.clearTest();

            ExtentManager.reset();
        }
    }

    private String resolveEmployeeUsername(String envPrefix, String module, String role, String defaultUsername) {
        String modKey = normalizeModuleKey(module);
        String roleKey = role != null ? role.toLowerCase() : "";

        if (!roleKey.isEmpty()) {
            String customRoleUser = ConfigReader.get(envPrefix + "." + modKey + "." + roleKey + ".username");
            if (customRoleUser != null && !customRoleUser.isBlank()) return customRoleUser.trim();

            String roleOnlyUser = ConfigReader.get(envPrefix + "." + roleKey + ".username");
            if (roleOnlyUser != null && !roleOnlyUser.isBlank()) return roleOnlyUser.trim();
        }

        String customModUser = ConfigReader.get(envPrefix + "." + modKey + ".username");
        if (customModUser != null && !customModUser.isBlank()) return customModUser.trim();

        String rawModUser = ConfigReader.get(envPrefix + "." + module.toLowerCase() + ".username");
        if (rawModUser != null && !rawModUser.isBlank()) return rawModUser.trim();

        return defaultUsername;
    }

    private String resolveEmployeePassword(String envPrefix, String module, String role, String defaultPassword) {
        String modKey = normalizeModuleKey(module);
        String roleKey = role != null ? role.toLowerCase() : "";

        if (!roleKey.isEmpty()) {
            String customRolePass = ConfigReader.get(envPrefix + "." + modKey + "." + roleKey + ".password");
            if (customRolePass != null && !customRolePass.isBlank()) return customRolePass.trim();

            String roleOnlyPass = ConfigReader.get(envPrefix + "." + roleKey + ".password");
            if (roleOnlyPass != null && !roleOnlyPass.isBlank()) return roleOnlyPass.trim();
        }

        String customModPass = ConfigReader.get(envPrefix + "." + modKey + ".password");
        if (customModPass != null && !customModPass.isBlank()) return customModPass.trim();

        String rawModPass = ConfigReader.get(envPrefix + "." + module.toLowerCase() + ".password");
        if (rawModPass != null && !rawModPass.isBlank()) return rawModPass.trim();

        return defaultPassword;
    }

    private String normalizeModuleKey(String module) {
        if (module == null) return "";
        String mod = module.toLowerCase()
                .replace("_employee", "")
                .replace("_emp", "")
                .replace("_citizen", "")
                .replace("_module", "")
                .replace("_workflow", "");
        if (mod.equals("pet_registration")) return "pet";
        if (mod.equals("trade_license")) return "tl";
        if (mod.equals("property_tax")) return "pt";
        if (mod.equals("public_grievance_redressal")) return "pgr";
        if (mod.equals("online_building_plan_approval_system") || mod.equals("obpas_oc")) return "obpas";
        if (mod.equals("street_vending")) return "sv";
        if (mod.equals("advertisement")) return "adv";
        if (mod.equals("water_and_sewerage") || mod.equals("water") || mod.equals("sewerage")) return "ws";
        if (mod.equals("community_hall_booking")) return "chb";
        if (mod.equals("construction_and_demolition")) return "cnd";
        if (mod.equals("garbage_collection") || mod.equals("garbage_collection_payment")) return "gc";
        if (mod.equals("no_due_certificate")) return "ndc";
        if (mod.equals("challan_generation")) return "challan";
        if (mod.equals("asset_management")) return "asset";
        if (mod.equals("estate_management")) return "estate";
        if (mod.equals("tree_pruning")) return "tp";
        if (mod.equals("water_tanker")) return "wt";
        if (mod.equals("mobile_toilet")) return "mt";
        if (mod.equals("ewaste_management_system")) return "ewaste";
        if (mod.equals("desludging_service") || mod.equals("desludging_service_payment") || mod.equals("desludging_service_payment2")) return "desludging";
        return mod;
    }
}