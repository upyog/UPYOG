package org.upyog.Automation.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.upyog.Automation.Service.ModuleTestService;
import org.upyog.Automation.Utils.AutomationConstants;
import org.upyog.Automation.Utils.ExcelDataReader;
import org.upyog.Automation.Utils.ExcelResultGenerator;
import org.upyog.Automation.model.ModuleExecutionResult;
import org.upyog.Automation.model.ModuleRequest;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller responsible for module test execution,
 * Excel test-data upload/download, and execution progress tracking.
 *
 * <p>Provides endpoints to trigger test execution across various UPYOG modules,
 * track execution progress in real-time, upload custom test datasets, and download
 * execution templates and result workbooks.</p>
 */
@RestController
@RequestMapping("/api/module")
public class ModuleTestController {

    private static final Logger logger = LoggerFactory.getLogger(ModuleTestController.class);

    private static final String TEST_DATA_FILE = "test-data/test-data.xlsx";
    private static final String TEST_DATA_CONTENT_DISPOSITION = "attachment; filename=\"UPYOG_Test_Data.xlsx\"";
    private static final String TEST_RESULT_CONTENT_DISPOSITION = "attachment; filename=\"UPYOG_Test_Result.xlsx\"";
    private static final String EXCEL_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String XLSX_EXTENSION = ".xlsx";
    private static final String TEST_RESULT_FILE_PREFIX = "upyog-test-result-";
    private static final String TEST_DATA_FILE_PREFIX = "upyog-test-data-";

    @Autowired
    private ModuleTestService moduleTestService;

    /**
     * Stores the latest generated result file so that it can be
     * downloaded through the /download-result endpoint.
     */
    private File latestResultFile;

    /**
     * Executes the selected UPYOG module or comma-separated batch of modules.
     *
     * <p>If an Excel test-data file has been uploaded, the execution
     * results are also written back to a temporary result workbook for downloading.</p>
     *
     * @param request the module execution request containing module name and base URL
     * @return ResponseEntity containing list of ModuleExecutionResult objects
     */
    @PostMapping("/run")
    public ResponseEntity<List<ModuleExecutionResult>> runModule(@RequestBody ModuleRequest request) {
        logger.info("Received request to run module: [{}], Base URL: [{}]", 
                request != null ? request.getModuleName() : null, 
                request != null ? request.getBaseUrl() : null);

        // Execute module test service
        List<ModuleExecutionResult> result = moduleTestService.runModule(request);
        logger.info("Module execution completed with [{}] test case result(s).", result != null ? result.size() : 0);

        try {
            // Check if user uploaded a custom test-data Excel file
            if (ExcelDataReader.hasUploadedExcelFile()) {
                logger.debug("Uploaded Excel file detected. Preparing result workbook generation.");
                File sourceExcel = ExcelDataReader.getUploadedExcelFile();

                // Extract module name to locate target sheet
                String moduleName = request != null ? request.getModuleName() : "";
                String firstModule = (moduleName != null && moduleName.contains(",")) 
                        ? moduleName.split(",")[0].trim() 
                        : (moduleName != null ? moduleName.trim() : "");

                String sheetName = getSheetName(firstModule);
                logger.debug("Generating result Excel for sheet: [{}] from source: [{}]", sheetName, sourceExcel.getName());

                // Create a temporary file to store the result workbook
                File outputFile = File.createTempFile(
                        TEST_RESULT_FILE_PREFIX,
                        XLSX_EXTENSION
                );

                // Preserve uploaded workbook structure and append execution results
                latestResultFile = ExcelResultGenerator.generateResultExcel(
                        sourceExcel,
                        sheetName,
                        result,
                        outputFile
                );
                logger.info("Result Excel generated successfully at: [{}]", latestResultFile.getAbsolutePath());
            }

        } catch (Exception e) {
            // Result generation failure should not fail the API response
            logger.error("Failed to generate test result Excel for module execution: {}", e.getMessage(), e);
        }

        return ResponseEntity.ok(result);
    }

    /**
     * Returns the current progress of the ongoing test execution.
     *
     * <p>The frontend periodically polls this endpoint to update
     * execution progress indicators (progress bar, active test case, etc.).</p>
     *
     * @return ResponseEntity containing progress details map
     */
    /**
     * Returns the test plan for the selected modules (number of test cases,
     * whether running from Excel or dev.properties, test case IDs).
     */
    @GetMapping("/test-plan")
    public ResponseEntity<Map<String, Object>> getTestPlan(@RequestParam(value = "modules", defaultValue = "") String moduleNames) {
        String[] modules = (moduleNames != null && !moduleNames.trim().isEmpty())
                ? (moduleNames.contains(",") ? moduleNames.split(",") : new String[]{moduleNames.trim()})
                : new String[0];
        Map<String, Object> plan = moduleTestService.getTestPlan(modules);
        return ResponseEntity.ok(plan);
    }

    /**
     * Returns the current progress of the ongoing test execution.
     *
     * <p>The frontend periodically polls this endpoint to update
     * execution progress indicators (progress bar, active test case, etc.).</p>
     *
     * @return ResponseEntity containing progress details map
     */
    @GetMapping("/progress")
    public ResponseEntity<?> getExecutionProgress() {
        Map<String, Object> progress = new HashMap<>();

        int total = moduleTestService.getTotalTestCases();
        int completed = moduleTestService.getCompletedTestCases();
        String currentTestCase = moduleTestService.getCurrentTestCase();
        String currentModule = moduleTestService.getCurrentModule();
        boolean isRunning = moduleTestService.isExecutionRunning();
        String executionMode = moduleTestService.getExecutionMode();
        String sourceName = moduleTestService.getSourceName();

        progress.put("totalTestCases", total);
        progress.put("completedTestCases", completed);
        progress.put("currentTestCase", currentTestCase);
        progress.put("currentModule", currentModule);
        progress.put("executionRunning", isRunning);
        progress.put("executionMode", executionMode);
        progress.put("sourceName", sourceName);

        logger.debug("Progress check: Total=[{}], Completed=[{}], Active=[{}], Running=[{}], Mode=[{}], Source=[{}]",
                total, completed, currentTestCase, isRunning, executionMode, sourceName);

        return ResponseEntity.ok(progress);
    }

    /**
     * Downloads the bundled master Excel test-data template.
     *
     * @return ResponseEntity with the template file resource
     */
    @GetMapping("/download-template")
    public ResponseEntity<Resource> downloadTemplate() {
        logger.info("Request received to download test data template: [{}]", TEST_DATA_FILE);

        try {
            ClassPathResource resource = new ClassPathResource(TEST_DATA_FILE);
            if (!resource.exists()) {
                logger.error("Test data template file not found on classpath: [{}]", TEST_DATA_FILE);
                return ResponseEntity.notFound().build();
            }

            byte[] bytes = resource.getInputStream().readAllBytes();
            ByteArrayResource byteArrayResource = new ByteArrayResource(bytes);

            return ResponseEntity.ok()
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            TEST_DATA_CONTENT_DISPOSITION
                    )
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(bytes.length))
                    .contentType(
                            MediaType.parseMediaType(EXCEL_CONTENT_TYPE)
                    )
                    .body(byteArrayResource);

        } catch (Exception e) {
            logger.error("Failed to read template file [{}]: {}", TEST_DATA_FILE, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Downloads the most recently generated test-result workbook.
     *
     * @return ResponseEntity containing the result Excel resource, or 404 Not Found if unavailable
     */
    @GetMapping("/download-result")
    public ResponseEntity<Resource> downloadResult() {
        logger.info("Request received to download latest test results.");

        if (latestResultFile == null || !latestResultFile.exists()) {
            logger.warn("Result download requested, but no generated result file is available.");
            return ResponseEntity.notFound().build();
        }

        try {
            byte[] bytes = Files.readAllBytes(latestResultFile.toPath());
            ByteArrayResource byteArrayResource = new ByteArrayResource(bytes);
            logger.info("Serving test result file: [{}] (Size: {} bytes)", latestResultFile.getName(), bytes.length);

            return ResponseEntity.ok()
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            TEST_RESULT_CONTENT_DISPOSITION
                    )
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(bytes.length))
                    .contentType(
                            MediaType.parseMediaType(EXCEL_CONTENT_TYPE)
                    )
                    .body(byteArrayResource);

        } catch (Exception e) {
            logger.error("Failed to read test result file: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Maps an automation module name to its corresponding Excel sheet name.
     *
     * <p>Explicit mappings are maintained for modules whose automation
     * names differ from their Excel sheet names.</p>
     *
     * @param moduleName the name of the automation module
     * @return the corresponding sheet name
     */
    private String getSheetName(String moduleName) {
        return moduleTestService.resolveSheetName(moduleName);
    }

    /**
     * Uploads an Excel test-data workbook for the current execution session.
     *
     * <p>The uploaded workbook is stored temporarily and supplied to
     * ExcelDataReader instead of the bundled default test-data workbook.</p>
     *
     * @param file the multipart Excel file uploaded by the user
     * @return ResponseEntity containing success message or bad request error
     */
    @PostMapping("/upload-excel")
    public ResponseEntity<String> uploadExcel(@RequestParam("file") MultipartFile file) {
        try {
            // Validate presence of uploaded file
            if (file == null || file.isEmpty()) {
                logger.warn("Upload rejected: empty or null file provided.");
                return ResponseEntity.badRequest()
                        .body("Please upload an Excel file.");
            }

            String fileName = file.getOriginalFilename();
            logger.info("Processing uploaded Excel file: [{}] (Size: {} bytes)", fileName, file.getSize());

            // Validate file extension
            if (fileName == null || !fileName.toLowerCase().endsWith(XLSX_EXTENSION)) {
                logger.warn("Upload rejected: invalid file format for [{}]", fileName);
                return ResponseEntity.badRequest()
                        .body("Only .xlsx Excel files are supported.");
            }

            // Create temporary file to store the workbook
            File tempFile = File.createTempFile(
                    TEST_DATA_FILE_PREFIX,
                    XLSX_EXTENSION
            );

            file.transferTo(tempFile);
            ExcelDataReader.setUploadedExcelFile(tempFile);

            logger.info("Excel test-data file stored successfully at: [{}]", tempFile.getAbsolutePath());
            return ResponseEntity.ok("Excel uploaded successfully: " + fileName);

        } catch (Exception e) {
            logger.error("Failed to upload Excel test-data file: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body("Failed to upload Excel: " + e.getMessage());
        }
    }
}