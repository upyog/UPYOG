package org.upyog.Automation.Reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ReportManager {

    private static final Logger logger =
            LoggerFactory.getLogger(ReportManager.class);

    private static ExtentReports extent;

    private static ExtentTest test;

    public static void startTest(
            String reportName,
            String testName
    ) {

        extent =
                ExtentManager.getInstance(
                        reportName
                );

        test =
                extent.createTest(
                        testName
                );

        logger.info(
                "REPORT INSTANCE = " + extent
        );

        logger.info(
                "TEST INSTANCE = " + test
        );
    }

    public static ExtentTest getTest() {
        return test;
    }

    public static boolean hasActiveTest() {
        return test != null;
    }

    public static void clearTest() {
        test = null;
    }

    public static void flush() {

        if (extent != null) {
            extent.flush();
        }
    }

    public static void logStep(String stepName) {

        logger.info(
                "LOG STEP CALLED = " + stepName
        );

        ExtentTest extentTest = getTest();

        logger.info(
                "CURRENT TEST = " + extentTest
        );

        if (extentTest != null) {
            extentTest.pass(stepName);
        }
    }

    public static void logScreen(String stepName, String base64Screenshot) {

        logger.info(
                "LOG SCREEN CALLED = {}", stepName
        );

        ExtentTest extentTest = getTest();

        if (extentTest != null) {
            if (base64Screenshot != null && !base64Screenshot.trim().isEmpty()) {
                try {
                    extentTest.info(
                            stepName,
                            MediaEntityBuilder.createScreenCaptureFromBase64String(base64Screenshot).build()
                    );
                    return;
                } catch (Exception e) {
                    logger.error("Unable to attach base64 screenshot to ExtentTest: {}", e.getMessage());
                }
            }
            extentTest.info(stepName);
        }
    }

    public static void logFlow(String flowName) {

        ExtentTest extentTest = getTest();

        if (extentTest != null) {

            extentTest.info(
                    "===================="
            );

            extentTest.info(flowName);

            extentTest.info(
                    "===================="
            );
        }
    }

    public static void logFailure(String stepName) {

        ExtentTest extentTest = getTest();

        if (extentTest != null) {
            extentTest.fail(stepName);
        } else {
            logger.info(
                    "NO ACTIVE TEST : " + stepName
            );
        }
    }

    public static void logFailure(String stepName, String base64Screenshot) {

        ExtentTest extentTest = getTest();

        if (extentTest != null) {
            if (base64Screenshot != null && !base64Screenshot.trim().isEmpty()) {
                try {
                    extentTest.fail(
                            stepName,
                            MediaEntityBuilder.createScreenCaptureFromBase64String(base64Screenshot).build()
                    );
                    return;
                } catch (Exception e) {
                    logger.error("Unable to attach base64 failure screenshot to ExtentTest: {}", e.getMessage());
                }
            }
            extentTest.fail(stepName);
        } else {
            logger.info(
                    "NO ACTIVE TEST : " + stepName
            );
        }
    }
    public static void logTestData(Map<String, String> testData) {

        if (testData == null || testData.isEmpty()) {
            return;
        }

        logStep("========== TEST DATA ==========");

        for (Map.Entry<String, String> entry : testData.entrySet()) {

            String key = entry.getKey();
            String value = entry.getValue();

            // Don't show execution control in the report
            if ("Execute".equalsIgnoreCase(key)
                    || "TestCase".equalsIgnoreCase(key)) {
                continue;
            }

            logStep(key + " : " + value);
        }

        logStep("========== END TEST DATA ==========");
    }
    public static void logFailure(
            String stepName,
            Exception exception
    ) {

        ExtentTest extentTest = getTest();

        if (extentTest != null) {

            String errorMessage =
                    exception.getMessage();

            if (errorMessage == null ||
                    errorMessage.trim().isEmpty()) {

                errorMessage =
                        exception.getClass().getSimpleName();
            }

            extentTest.fail(
                    "FAILED STEP : " + stepName
            );

            extentTest.fail(
                    "ERROR : " + errorMessage
            );

            extentTest.fail(
                    "EXCEPTION : "
                            + exception.getClass().getSimpleName()
            );

        } else {

            logger.info(
                    "NO ACTIVE TEST : " + stepName
            );
        }
    }
}