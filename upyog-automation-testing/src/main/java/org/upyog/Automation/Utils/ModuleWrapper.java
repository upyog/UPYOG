package org.upyog.Automation.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.upyog.Automation.Reports.ReportManager;

import java.util.List;

public class ModuleWrapper {

    private static final Logger logger =
            LoggerFactory.getLogger(ModuleWrapper.class);

    // ==============================
    // SINGLE MODULE
    // ==============================
    public static void execute(String moduleName,
                               Runnable moduleLogic) {

        runModule(moduleName, moduleLogic);
    }

    // ==============================
    // MULTIPLE MODULES
    // ==============================
    public static void executeBatch(List<ModuleTask> modules) {

        for (ModuleTask module : modules) {

            try {

                runModule(
                        module.getModuleName(),
                        module.getModuleLogic()
                );

            } catch (Exception e) {

                logger.error(
                        "Skipping failed module and continuing: {}",
                        module.getModuleName(),
                        e
                );

                // Continue with next module
            }
        }
    }

    // ==============================
    // COMMON EXECUTION
    // ==============================
    private static void runModule(String moduleName,
                                  Runnable moduleLogic) {

        logger.info(
                "MODULE WRAPPER TEST = "
                        + ReportManager.getTest()
        );

        logger.info("============================");
        logger.info("STARTING: {}", moduleName);
        logger.info("============================");

        long start = System.currentTimeMillis();

        try {

            moduleLogic.run();

            logger.info("PASSED: {}", moduleName);

            ReportManager.logStep(
                    "PASSED : " + moduleName
            );

        } catch (Exception e) {

            logger.error("FAILED: " + moduleName, e);

            ReportManager.logStep(
                    "FAILED : " + moduleName
            );

            throw e;

        } finally {

            long duration =
                    (System.currentTimeMillis() - start) / 1000;

            logger.info("Time Taken: {} sec", duration);

            logger.info("============================");
            logger.info("FINISHED: {}", moduleName);
            logger.info("============================");
        }
    }
}