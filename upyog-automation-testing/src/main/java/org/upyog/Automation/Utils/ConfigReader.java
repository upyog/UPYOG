package org.upyog.Automation.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private static final Logger logger =
            LoggerFactory.getLogger(ConfigReader.class);

    private static final Properties props = new Properties();

    static {
        try {
            // Load common.properties
            InputStream commonInput = ConfigReader.class
                    .getClassLoader()
                    .getResourceAsStream(
                            "config/common.properties"
                    );

            if (commonInput != null) {
                props.load(commonInput);
            }

            // Load env file
            String env = System.getProperty("env", "dev");
            logger.info("ACTIVE ENV = {}", env);

            String fileName = "config/" + env + ".properties";

            InputStream envInput = ConfigReader.class
                    .getClassLoader()
                    .getResourceAsStream(fileName);

            if (envInput == null) {
                throw new RuntimeException("File not found: " + fileName);
            }

            props.load(envInput); // override if same key

        } catch (Exception e) {
            throw new RuntimeException("Failed to load config", e);
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }
}