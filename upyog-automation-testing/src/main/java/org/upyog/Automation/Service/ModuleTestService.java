package org.upyog.Automation.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.Automation.Controller.ModuleTestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.upyog.Automation.model.ModuleExecutionResult;

import java.util.ArrayList;
import java.util.List;


@Service
public class ModuleTestService {

    private static final Logger logger =
            LoggerFactory.getLogger(ModuleTestService.class);

    @Autowired
    private WorkflowExecutor workflowExecutor;

    public List<ModuleExecutionResult> runModule(ModuleTestController.ModuleRequest request) {

        String moduleName = request.getModuleName();

        String citizenUrl = request.getBaseUrl();

        logger.info(
                "Module Received: " + moduleName
        );

        if (moduleName.contains(",")) {

            String[] modules = moduleName.split(",");

            List<ModuleExecutionResult> results = new ArrayList<>();

            for (String module : modules) {

                module = module.trim();

                logger.info("RUNNING MODULE = " + module);

                try {

                    executeSingleModule(module, citizenUrl);

                    results.add(
                            new ModuleExecutionResult(
                                    module,
                                    "PASS",
                                    "Executed Successfully"
                            )
                    );

                } catch (Exception e) {

                    results.add(
                            new ModuleExecutionResult(
                                    module,
                                    "FAIL",
                                    e.getMessage()
                            )
                    );

                }
            }

            return results;
        }

        try {

            executeSingleModule(
                    moduleName,
                    citizenUrl
            );

            return List.of(
                    new ModuleExecutionResult(
                            moduleName,
                            "PASS",
                            "Executed Successfully"
                    )
            );

        } catch (Exception e) {

            return List.of(
                    new ModuleExecutionResult(
                            moduleName,
                            "FAIL",
                            e.getMessage()
                    )
            );

        }
    }

    private String executeSingleModule(
            String moduleName,
            String citizenUrl

    ) {

        switch (moduleName.toUpperCase()) {

            case "DESLUDGING_SERVICE":

                workflowExecutor.executeWorkflow(
                        "test-config/desludging/desludging_workflow.json",
                        "test-config/desludging/desludging_stakeholder_module.json",
                        citizenUrl
                );

                return "Workflow Executed";

            case "PET_REGISTRATION":

                workflowExecutor.executeWorkflow(
                        "test-config/pet/pet_workflow.json",
                        "test-config/pet/pet_stakeholder_module.json",
                        citizenUrl
                );

                return "Workflow Executed";

            case "EWASTE_MANAGEMENT_SYSTEM":

                workflowExecutor.executeWorkflow(
                        "test-config/ewaste/ewaste_workflow.json",
                        "test-config/ewaste/ewaste_stakeholder_module.json",
                        citizenUrl
                );

                return "Workflow Executed";

            case "WATER_TANKER":

                workflowExecutor.executeWorkflow(
                        "test-config/requestService/water_tanker_workflow.json",
                        "test-config/requestService/water_tanker_stakeholder_module.json",
                        citizenUrl
                );

                return "Workflow Executed";

            case "TREE_PRUNING":

                workflowExecutor.executeWorkflow(
                        "test-config/requestService/tree_pruning_workflow.json",
                        "test-config/requestService/tree_pruning_stakeholder_module.json",
                        citizenUrl
                );

                return "Workflow Executed";

            case "MOBILE_TOILET":

                workflowExecutor.executeWorkflow(
                        "test-config/requestService/mobile_toilet_workflow.json",
                        "test-config/requestService/mobile_toilet_stakeholder_module.json",
                        citizenUrl
                );

                return "Workflow Executed";

            case "STREET_VENDING":

                workflowExecutor.executeWorkflow(
                        "test-config/streetVending/street_vending_workflow.json",
                        "test-config/streetVending/street_vending_stakeholder_module.json",
                        citizenUrl
                );

                return "Workflow Executed";

            case "TRADE_LICENSE":

                workflowExecutor.executeWorkflow(
                        "test-config/tradeLicense/trade_license_workflow.json",
                        "test-config/tradeLicense/trade_license_stakeholder_module.json",
                        citizenUrl
                );

                return "Workflow Executed";

            case "ADVERTISEMENT":

                workflowExecutor.executeWorkflow(
                        "test-config/advertisement/adv_workflow.json",
                        "test-config/advertisement/adv_stakeholder_module.json",
                        citizenUrl
                );

                return "Workflow Executed";

            case "PROPERTY_TAX":

                workflowExecutor.executeWorkflow(
                        "test-config/propertyTax/property_tax_workflow.json",
                        "test-config/propertyTax/property_tax_stakeholder_module.json",
                        citizenUrl
                );

                return "Workflow Executed";

            case "PUBLIC_GRIEVANCE_REDRESSAL":

                workflowExecutor.executeWorkflow(
                        "test-config/pgr/pgr_workflow.json",
                        "test-config/pgr/pgr_stakeholder_module.json",
                        citizenUrl
                );

                return "Workflow Executed";

            case "ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM":

                workflowExecutor.executeWorkflow(
                        "test-config/obpas/obpas_workflow.json",
                        "test-config/obpas/obpas_stakeholder_module.json",
                        citizenUrl
                );

                return "Workflow Executed";


            case "COMMUNITY_HALL_BOOKING":

                workflowExecutor.executeWorkflow(
                        "test-config/chb/chb_workflow.json",
                        "test-config/chb/chb_stakeholder_module.json",
                        citizenUrl
                );

                return "Workflow Executed";


            case "CONSTRUCTION_AND_DEMOLITION":

                workflowExecutor.executeWorkflow(
                        "test-config/cnd/cnd_workflow.json",
                        "test-config/cnd/cnd_stakeholder_module.json",
                        citizenUrl
                );

                return "Workflow Executed";


            case "WATER_AND_SEWERAGE":

                workflowExecutor.executeWorkflow(
                        "test-config/waterAndSewerage/water_and_sewerage_workflow.json",
                        "test-config/waterAndSewerage/water_and_sewerage_stakeholder_module.json",
                        citizenUrl
                );

                return "Workflow Executed";

            case "ASSET_MANAGEMENT_SYSTEM":

                workflowExecutor.executeWorkflow(
                        "test-config/asset/asset_workflow.json",
                        "test-config/asset/asset_stakeholder_module.json",
                        citizenUrl.replace("/citizen/login", "/employee/login")
                );

                return "Workflow Executed";


            default:

                logger.info(
                        "DEFAULT CASE HIT : " + moduleName
                );

                return "Unsupported Module : " + moduleName;
        }
    }
}