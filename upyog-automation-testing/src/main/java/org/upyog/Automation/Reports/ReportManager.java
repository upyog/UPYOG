package org.upyog.Automation.Reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
}