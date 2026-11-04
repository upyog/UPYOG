package org.upyog.Automation.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.upyog.Automation.Utils.AutomationConstants;
import org.upyog.Automation.Utils.WorkflowDataStore;
import org.upyog.Automation.model.ModuleExecutionResult;
import org.upyog.Automation.model.ModuleRequest;
import org.upyog.Automation.Utils.ExcelDataReader;

import java.util.Map;

import java.util.ArrayList;
import java.util.List;


@Service
public class ModuleTestService {

    private static final Logger logger =
            LoggerFactory.getLogger(ModuleTestService.class);
    // =========================
// EXECUTION PROGRESS
// =========================

    private volatile int totalTestCases = 0;
    private volatile int completedTestCases = 0;
    private volatile String currentTestCase = "";
    private volatile String currentModule = "";
    private volatile boolean executionRunning = false;
    private volatile String executionMode = "STANDARD";
    private volatile String sourceName = "dev.properties";

    @Autowired
    private WorkflowExecutor workflowExecutor;

    public String resolveSheetName(String moduleName) {
        if (moduleName == null) {
            return AutomationConstants.SHEET_SUFFIX;
        }

        return switch (moduleName.toUpperCase()) {
            case AutomationConstants.MODULE_PET_REGISTRATION, AutomationConstants.MODULE_PET ->
                    AutomationConstants.SHEET_PET;

            case AutomationConstants.MODULE_PUBLIC_GRIEVANCE_REDRESSAL, AutomationConstants.MODULE_PGR ->
                    AutomationConstants.SHEET_PGR;

            case AutomationConstants.MODULE_NO_DUE_CERTIFICATE, AutomationConstants.MODULE_NDC ->
                    AutomationConstants.SHEET_NDC;

            case AutomationConstants.MODULE_PROPERTY_TAX, AutomationConstants.MODULE_PT ->
                    AutomationConstants.SHEET_PT;

            case AutomationConstants.MODULE_ADVERTISEMENT, AutomationConstants.MODULE_ADV ->
                    AutomationConstants.SHEET_ADVERTISEMENT;

            case AutomationConstants.MODULE_STREET_VENDING, AutomationConstants.MODULE_SV ->
                    AutomationConstants.SHEET_STREET_VENDING;

            case AutomationConstants.MODULE_TRADE_LICENSE, AutomationConstants.MODULE_TL ->
                    AutomationConstants.SHEET_TRADE_LICENSE;

            case AutomationConstants.MODULE_TREE_PRUNING ->
                    AutomationConstants.SHEET_TREE_PRUNING;

            case AutomationConstants.MODULE_WATER_TANKER ->
                    AutomationConstants.SHEET_WATER_TANKER;

            case AutomationConstants.MODULE_MOBILE_TOILET ->
                    AutomationConstants.SHEET_MOBILE_TOILET;

            case AutomationConstants.MODULE_OBPAS, AutomationConstants.MODULE_ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM ->
                    AutomationConstants.SHEET_OBPAS;

            case AutomationConstants.MODULE_OBPAS_OC ->
                    AutomationConstants.SHEET_OBPAS_OC_CREATE;

            case AutomationConstants.MODULE_EWASTE, AutomationConstants.MODULE_EWASTE_MANAGEMENT_SYSTEM ->
                    AutomationConstants.SHEET_EWASTE;

            case AutomationConstants.MODULE_CHB, AutomationConstants.MODULE_COMMUNITY_HALL_BOOKING ->
                    AutomationConstants.SHEET_CHB;

            case AutomationConstants.MODULE_CND, AutomationConstants.MODULE_CONSTRUCTION_AND_DEMOLITION ->
                    AutomationConstants.SHEET_CND;

            case AutomationConstants.MODULE_DESLUDGING, AutomationConstants.MODULE_DESLUDGING_SERVICE ->
                    AutomationConstants.SHEET_DESLUDGING;

            case AutomationConstants.MODULE_DESLUDGING_PAYMENT, AutomationConstants.MODULE_DESLUDGING_SERVICE_PAYMENT ->
                    AutomationConstants.SHEET_DESLUDGING_PAYMENT;

            case AutomationConstants.MODULE_DESLUDGING_PAYMENT2, AutomationConstants.MODULE_DESLUDGING_SERVICE_PAYMENT2 ->
                    AutomationConstants.SHEET_DESLUDGING_PAYMENT2;

            case AutomationConstants.MODULE_WATER_AND_SEWERAGE, AutomationConstants.MODULE_WS ->
                    AutomationConstants.SHEET_WATER_AND_SEWERAGE;

            case AutomationConstants.MODULE_GARBAGE_COLLECTION ->
                    AutomationConstants.SHEET_GC;

            case AutomationConstants.MODULE_GARBAGE_COLLECTION_PAYMENT ->
                    AutomationConstants.SHEET_GC_PAYMENT;

            case AutomationConstants.MODULE_ESTATE_MANAGEMENT ->
                    AutomationConstants.SHEET_ESTATE_MANAGEMENT;

            case AutomationConstants.MODULE_ASSET_MANAGEMENT, AutomationConstants.MODULE_ASSET_MANAGEMENT_SYSTEM ->
                    AutomationConstants.SHEET_ASSET;

            case AutomationConstants.MODULE_CHALLAN_GENERATION ->
                    AutomationConstants.SHEET_CHALLAN;

            case AutomationConstants.MODULE_DESLUDGING_EMP_UPDATE, AutomationConstants.MODULE_DESLUDGING_EMPLOYEE_UPDATE ->
                    AutomationConstants.SHEET_DESLUDGING_EMP_UPDATE;

            case AutomationConstants.MODULE_DESLUDGING_EMP_COMPLETE ->
                    AutomationConstants.SHEET_DESLUDGING_EMP_COMPLETE;

            default ->
                    moduleName + AutomationConstants.SHEET_SUFFIX;
        };
    }

    public Map<String, Object> getTestPlan(String[] modules) {
        Map<String, Object> plan = new java.util.HashMap<>();
        List<Map<String, String>> testCaseList = new ArrayList<>();
        boolean isExcelMode = false;
        String source = "dev.properties";

        if (ExcelDataReader.hasUploadedExcelFile()) {
            source = ExcelDataReader.getUploadedExcelFile().getName();
        } else {
            source = AutomationConstants.DEFAULT_EXCEL_FILE;
        }

        int total = 0;
        String firstTestCase = "";
        String firstModule = "";

        if (modules != null) {
            for (String mod : modules) {
                if (mod == null || mod.trim().isEmpty()) continue;
                String trimmedMod = mod.trim();
                if (firstModule.isEmpty()) {
                    firstModule = trimmedMod;
                }
                String sheetName = resolveSheetName(trimmedMod);
                if (ExcelDataReader.hasSheet(sheetName)) {
                    List<Map<String, String>> sheetCases = ExcelDataReader.readSheet(sheetName);
                    if (sheetCases != null && !sheetCases.isEmpty()) {
                        isExcelMode = true;
                        for (Map<String, String> row : sheetCases) {
                            String tcId = row.getOrDefault("TestCase", trimmedMod + "_TC");
                            if (firstTestCase.isEmpty()) {
                                firstTestCase = tcId;
                            }
                            Map<String, String> item = new java.util.HashMap<>();
                            item.put("module", trimmedMod);
                            item.put("testCase", tcId);
                            item.put("sheet", sheetName);
                            item.put("mode", "EXCEL");
                            testCaseList.add(item);
                            total++;
                        }
                        continue;
                    }
                }

                // Default standard config test case
                if (firstTestCase.isEmpty()) {
                    firstTestCase = trimmedMod;
                }
                Map<String, String> item = new java.util.HashMap<>();
                item.put("module", trimmedMod);
                item.put("testCase", trimmedMod);
                item.put("sheet", "");
                item.put("mode", "STANDARD");
                testCaseList.add(item);
                total++;
            }
        }

        if (!isExcelMode) {
            source = "dev.properties";
        }

        plan.put("executionMode", isExcelMode ? "EXCEL" : "STANDARD");
        plan.put("sourceName", source);
        plan.put("isUploadedExcel", ExcelDataReader.hasUploadedExcelFile());
        plan.put("totalTestCases", total > 0 ? total : (modules != null ? modules.length : 0));
        plan.put("firstTestCase", firstTestCase);
        plan.put("firstModule", firstModule);
        plan.put("testCases", testCaseList);
        return plan;
    }

    public int calculateTotalTestCases(String[] modules) {
        Map<String, Object> plan = getTestPlan(modules);
        return (int) plan.getOrDefault("totalTestCases", modules.length);
    }

    public List<ModuleExecutionResult> runModule(ModuleRequest request) {
        String moduleNameStr = request != null ? request.getModuleName() : "";
        String citizenUrl = request != null ? request.getBaseUrl() : "";

        logger.info("Module Received: {}", moduleNameStr);

        if (moduleNameStr == null || moduleNameStr.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String[] modules = moduleNameStr.contains(",") ? moduleNameStr.split(",") : new String[]{moduleNameStr};

        // Initialize progress for the whole execution from the test plan
        Map<String, Object> plan = getTestPlan(modules);
        this.totalTestCases = (int) plan.getOrDefault("totalTestCases", modules.length);
        this.completedTestCases = 0;
        this.executionMode = (String) plan.getOrDefault("executionMode", "STANDARD");
        this.sourceName = (String) plan.getOrDefault("sourceName", "dev.properties");
        this.currentTestCase = (String) plan.getOrDefault("firstTestCase", "");
        this.currentModule = (String) plan.getOrDefault("firstModule", "");
        this.executionRunning = true;

        List<ModuleExecutionResult> results = new ArrayList<>();

        try {
            for (String module : modules) {
                module = module.trim();
                if (module.isEmpty()) continue;

                currentModule = module;
                logger.info("RUNNING MODULE = {}", module);

                try {
                    List<ModuleExecutionResult> excelResults = executeFromExcelIfAvailable(module, citizenUrl);
                    if (!excelResults.isEmpty()) {
                        results.addAll(excelResults);
                    } else {
                        // Standard non-Excel execution (counts as 1 test case)
                        currentTestCase = module;
                        logger.info("EXECUTION PROGRESS: {} / {} | {}", completedTestCases + 1, totalTestCases, module);
                        executeSingleModule(module, citizenUrl);
                        results.add(new ModuleExecutionResult(
                                module,
                                AutomationConstants.STATUS_PASS,
                                "Executed Successfully"
                        ));
                        completedTestCases++;
                    }
                } catch (Exception e) {
                    logger.error("FAILED TO EXECUTE MODULE = {}", module, e);
                    results.add(new ModuleExecutionResult(
                            module,
                            AutomationConstants.STATUS_FAIL,
                            e.getMessage()
                    ));
                    completedTestCases++;
                }
            }
        } finally {
            this.executionRunning = false;
            this.currentTestCase = "";
        }

        return results;
    }

    private List<ModuleExecutionResult> executeFromExcelIfAvailable(
            String moduleName,
            String citizenUrl
    ) {
        List<ModuleExecutionResult> results = new ArrayList<>();

        String sheetName = resolveSheetName(moduleName);

        logger.info("Checking Excel sheet: {}", sheetName);

        // Excel sheet does not exist
        if (!ExcelDataReader.hasSheet(sheetName)) {
            logger.info("Excel sheet not found: {}. Using normal workflow.", sheetName);
            return results;
        }

        logger.info("Excel sheet found: {}", sheetName);

        List<Map<String, String>> testCases = ExcelDataReader.readSheet(sheetName);

        // Sheet exists but no Execute = YES rows
        if (testCases == null || testCases.isEmpty()) {
            logger.info("No executable Excel test cases found in {}. Using normal workflow.", sheetName);
            return results;
        }

        logger.info("Excel test cases found: {}", testCases.size());

        for (Map<String, String> testData : testCases) {
            String testCase = testData.getOrDefault("TestCase", "UNKNOWN");
            currentTestCase = testCase;
            currentModule = moduleName;

            logger.info("EXECUTION PROGRESS: {} / {} | {}", completedTestCases + 1, totalTestCases, testCase);
            logger.info("========== STARTING EXCEL TEST CASE: {} ==========", testCase);

            try {
                // Clear previous Excel data
                WorkflowDataStore.clear();

                WorkflowDataStore.put(AutomationConstants.KEY_CURRENT_MODULE, moduleName);
                WorkflowDataStore.put(AutomationConstants.KEY_CURRENT_TEST_CASE, testCase);

                // Load current Excel row
                for (Map.Entry<String, String> entry : testData.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();

                    if (value != null && !value.trim().isEmpty()) {
                        WorkflowDataStore.put(key, value.trim());
                        logger.info("Excel Data → {} = {}", key, value);
                    }
                }

                // Execute the EXISTING workflow
                executeSingleModule(moduleName, citizenUrl);

                // Test case passed
                results.add(new ModuleExecutionResult(
                        moduleName,
                        testCase,
                        AutomationConstants.STATUS_PASS,
                        "Executed Successfully",
                        ""
                ));

                completedTestCases++;

                logger.info("========== COMPLETED EXCEL TEST CASE: {} ==========", testCase);

            } catch (Exception e) {
                String errorMessage = e.getMessage();
                if (errorMessage == null || errorMessage.trim().isEmpty()) {
                    errorMessage = e.getClass().getSimpleName();
                }

                String failedStep = WorkflowDataStore.get(AutomationConstants.KEY_FAILED_STEP);
                String failedError = WorkflowDataStore.get(AutomationConstants.KEY_FAILED_ERROR);

                if (failedStep == null || failedStep.trim().isEmpty()) {
                    failedStep = "Unknown Step";
                }

                if (failedError != null && !failedError.trim().isEmpty()) {
                    errorMessage = failedError;
                }

                logger.error("FAILED TEST CASE: {} | Step: {} | Error: {}", testCase, failedStep, errorMessage, e);

                results.add(new ModuleExecutionResult(
                        moduleName,
                        testCase,
                        AutomationConstants.STATUS_FAIL,
                        errorMessage,
                        failedStep
                ));

                completedTestCases++;
            } finally {
                WorkflowDataStore.clear();
            }
        }

        return results;
    }

// =========================
// EXECUTION PROGRESS GETTERS
// =========================

    public int getTotalTestCases() {
        return totalTestCases;
    }

    public int getCompletedTestCases() {
        return completedTestCases;
    }

    public String getCurrentTestCase() {
        return currentTestCase;
    }

    public String getCurrentModule() {
        return currentModule;
    }

    public boolean isExecutionRunning() {
        return executionRunning;
    }

    public String getExecutionMode() {
        return executionMode;
    }

    public String getSourceName() {
        return sourceName;
    }


    private String executeSingleModule(
            String moduleName,
            String citizenUrl

    ) {

        switch (moduleName.toUpperCase()) {

            case AutomationConstants.MODULE_DESLUDGING:
            case AutomationConstants.MODULE_DESLUDGING_SERVICE:

                workflowExecutor.executeWorkflow(
                        AutomationConstants.CONFIG_DESLUDGING_WORKFLOW,
                        AutomationConstants.CONFIG_DESLUDGING_STAKEHOLDER,
                        citizenUrl
                );

                return AutomationConstants.MSG_WORKFLOW_EXECUTED;

            case AutomationConstants.MODULE_PET_REGISTRATION:
            case AutomationConstants.MODULE_PET:

                workflowExecutor.executeWorkflow(
                        AutomationConstants.CONFIG_PET_WORKFLOW,
                        AutomationConstants.CONFIG_PET_STAKEHOLDER,
                        citizenUrl
                );

                return AutomationConstants.MSG_WORKFLOW_EXECUTED;

            case AutomationConstants.MODULE_EWASTE:
            case AutomationConstants.MODULE_EWASTE_MANAGEMENT_SYSTEM:

                workflowExecutor.executeWorkflow(
                        AutomationConstants.CONFIG_EWASTE_WORKFLOW,
                        AutomationConstants.CONFIG_EWASTE_STAKEHOLDER,
                        citizenUrl
                );

                return AutomationConstants.MSG_WORKFLOW_EXECUTED;

            case AutomationConstants.MODULE_WATER_TANKER:

                workflowExecutor.executeWorkflow(
                        AutomationConstants.CONFIG_WATER_TANKER_WORKFLOW,
                        AutomationConstants.CONFIG_WATER_TANKER_STAKEHOLDER,
                        citizenUrl
                );

                return AutomationConstants.MSG_WORKFLOW_EXECUTED;

            case AutomationConstants.MODULE_TREE_PRUNING:

                workflowExecutor.executeWorkflow(
                        AutomationConstants.CONFIG_TREE_PRUNING_WORKFLOW,
                        AutomationConstants.CONFIG_TREE_PRUNING_STAKEHOLDER,
                        citizenUrl
                );

                return AutomationConstants.MSG_WORKFLOW_EXECUTED;

            case AutomationConstants.MODULE_MOBILE_TOILET:

                workflowExecutor.executeWorkflow(
                        AutomationConstants.CONFIG_MOBILE_TOILET_WORKFLOW,
                        AutomationConstants.CONFIG_MOBILE_TOILET_STAKEHOLDER,
                        citizenUrl
                );

                return AutomationConstants.MSG_WORKFLOW_EXECUTED;

            case AutomationConstants.MODULE_STREET_VENDING:
            case AutomationConstants.MODULE_SV:

                workflowExecutor.executeWorkflow(
                        AutomationConstants.CONFIG_STREET_VENDING_WORKFLOW,
                        AutomationConstants.CONFIG_STREET_VENDING_STAKEHOLDER,
                        citizenUrl
                );

                return AutomationConstants.MSG_WORKFLOW_EXECUTED;

            case AutomationConstants.MODULE_TRADE_LICENSE:
            case AutomationConstants.MODULE_TL:

                workflowExecutor.executeWorkflow(
                        AutomationConstants.CONFIG_TRADE_LICENSE_WORKFLOW,
                        AutomationConstants.CONFIG_TRADE_LICENSE_STAKEHOLDER,
                        citizenUrl
                );

                return AutomationConstants.MSG_WORKFLOW_EXECUTED;

            case AutomationConstants.MODULE_ADVERTISEMENT:
            case AutomationConstants.MODULE_ADV:

                workflowExecutor.executeWorkflow(
                        AutomationConstants.CONFIG_ADVERTISEMENT_WORKFLOW,
                        AutomationConstants.CONFIG_ADVERTISEMENT_STAKEHOLDER,
                        citizenUrl
                );

                return AutomationConstants.MSG_WORKFLOW_EXECUTED;

            case AutomationConstants.MODULE_PROPERTY_TAX:
            case AutomationConstants.MODULE_PT:

                workflowExecutor.executeWorkflow(
                        AutomationConstants.CONFIG_PROPERTY_TAX_WORKFLOW,
                        AutomationConstants.CONFIG_PROPERTY_TAX_STAKEHOLDER,
                        citizenUrl
                );

                return AutomationConstants.MSG_WORKFLOW_EXECUTED;

            case AutomationConstants.MODULE_PUBLIC_GRIEVANCE_REDRESSAL:
            case AutomationConstants.MODULE_PGR:

                workflowExecutor.executeWorkflow(
                        AutomationConstants.CONFIG_PGR_WORKFLOW,
                        AutomationConstants.CONFIG_PGR_STAKEHOLDER,
                        citizenUrl
                );

                return AutomationConstants.MSG_WORKFLOW_EXECUTED;

            case AutomationConstants.MODULE_OBPAS:
            case AutomationConstants.MODULE_ONLINE_BUILDING_PLAN_APPROVAL_SYSTEM:

                workflowExecutor.executeWorkflow(
                        AutomationConstants.CONFIG_OBPAS_WORKFLOW,
                        AutomationConstants.CONFIG_OBPAS_STAKEHOLDER,
                        citizenUrl
                );

                return AutomationConstants.MSG_WORKFLOW_EXECUTED;


            case AutomationConstants.MODULE_CHB:
            case AutomationConstants.MODULE_COMMUNITY_HALL_BOOKING:

                workflowExecutor.executeWorkflow(
                        AutomationConstants.CONFIG_CHB_WORKFLOW,
                        AutomationConstants.CONFIG_CHB_STAKEHOLDER,
                        citizenUrl
                );

                return AutomationConstants.MSG_WORKFLOW_EXECUTED;


            case AutomationConstants.MODULE_CND:
            case AutomationConstants.MODULE_CONSTRUCTION_AND_DEMOLITION:

                workflowExecutor.executeWorkflow(
                        AutomationConstants.CONFIG_CND_WORKFLOW,
                        AutomationConstants.CONFIG_CND_STAKEHOLDER,
                        citizenUrl
                );

                return AutomationConstants.MSG_WORKFLOW_EXECUTED;


            case AutomationConstants.MODULE_WATER_AND_SEWERAGE:
            case AutomationConstants.MODULE_WS:

                workflowExecutor.executeWorkflow(
                        AutomationConstants.CONFIG_WATER_AND_SEWERAGE_WORKFLOW,
                        AutomationConstants.CONFIG_WATER_AND_SEWERAGE_STAKEHOLDER,
                        citizenUrl
                );

                return AutomationConstants.MSG_WORKFLOW_EXECUTED;

            case AutomationConstants.MODULE_ASSET_MANAGEMENT:
            case AutomationConstants.MODULE_ASSET_MANAGEMENT_SYSTEM:

                workflowExecutor.executeWorkflow(
                        AutomationConstants.CONFIG_ASSET_WORKFLOW,
                        AutomationConstants.CONFIG_ASSET_STAKEHOLDER,
                        citizenUrl.replace(AutomationConstants.CITIZEN_LOGIN_PATH, AutomationConstants.EMPLOYEE_LOGIN_PATH)
                );

                return AutomationConstants.MSG_WORKFLOW_EXECUTED;

            case AutomationConstants.MODULE_GARBAGE_COLLECTION:
                workflowExecutor.executeWorkflow(
                        AutomationConstants.CONFIG_GARBAGE_COLLECTION_WORKFLOW,
                        AutomationConstants.CONFIG_GARBAGE_COLLECTION_STAKEHOLDER,
                        citizenUrl
                );
                return AutomationConstants.MSG_WORKFLOW_EXECUTED;

            case AutomationConstants.MODULE_ESTATE_MANAGEMENT:
                workflowExecutor.executeWorkflow(
                        AutomationConstants.CONFIG_ESTATE_MANAGEMENT_WORKFLOW,
                        AutomationConstants.CONFIG_ESTATE_MANAGEMENT_STAKEHOLDER,
                        citizenUrl
                );
                return AutomationConstants.MSG_WORKFLOW_EXECUTED;

            case AutomationConstants.MODULE_CHALLAN_GENERATION:
                workflowExecutor.executeWorkflow(
                        AutomationConstants.CONFIG_CHALLAN_WORKFLOW,
                        AutomationConstants.CONFIG_CHALLAN_STAKEHOLDER,
                        citizenUrl
                );
                return AutomationConstants.MSG_WORKFLOW_EXECUTED;


            case AutomationConstants.MODULE_NO_DUE_CERTIFICATE:
            case AutomationConstants.MODULE_NDC:
                workflowExecutor.executeWorkflow(
                        AutomationConstants.CONFIG_NDC_WORKFLOW,
                        AutomationConstants.CONFIG_NDC_STAKEHOLDER,
                        citizenUrl
                );
                return AutomationConstants.MSG_WORKFLOW_EXECUTED;




            default:

                logger.info(
                        "DEFAULT CASE HIT : " + moduleName
                );

                return "Unsupported Module : " + moduleName;
        }
    }
}