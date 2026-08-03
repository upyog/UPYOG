package org.upyog.Automation.Utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.upyog.Automation.model.TestModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Loads JSON configuration files and resolves placeholder variables.
 *
 * Placeholder Resolution:
 * - Variables in format ${property.name} are replaced with values from
 *   a properties file or system properties.
 * - This allows the same JSON config to work across different environments
 *   without modification.
 */
public class JsonConfigLoader {

    private static final Logger logger = LoggerFactory.getLogger(JsonConfigLoader.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Pattern matches ${...} placeholders in JSON values
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{([^}]+)}");

    private final Properties testProperties;

    public JsonConfigLoader(String propertiesPath) {
        this.testProperties = loadProperties(propertiesPath);
    }

    /**
     * Loads properties file from classpath.
     * Properties are used to resolve ${...} placeholders in JSON config.
     */
    private Properties loadProperties(String path) {
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is != null) {
                props.load(is);
                logger.info("Loaded properties from: {}", path);
            } else {
                logger.warn("Properties file not found: {}", path);
            }
        } catch (IOException e) {
            logger.error("Failed to load properties: {}", path, e);
        }
        return props;
    }

    /**
     * Loads a TestModule from JSON file and resolves all placeholders.
     *
     * @param jsonPath Path to JSON config file in classpath
     * @return Fully resolved TestModule ready for execution
     */
    public TestModule loadModule(String jsonPath) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(jsonPath)) {
            if (is == null) {
                throw new IllegalArgumentException("JSON config not found: " + jsonPath);
            }

            // Read raw JSON as string first to resolve placeholders
            java.util.Scanner scanner =
                    new java.util.Scanner(
                            is,
                            "UTF-8"
                    ).useDelimiter("\\A");

            String jsonContent =
                    scanner.hasNext()
                            ? scanner.next()
                            : "";
            String resolvedJson = resolvePlaceholders(jsonContent);

            TestModule module = objectMapper.readValue(resolvedJson, TestModule.class);
            logger.info("Loaded module '{}' with {} instructions",
                    module.getModuleName(), module.getInstructions().size());

            return module;

        } catch (IOException e) {
            throw new RuntimeException("Failed to load JSON config: " + jsonPath, e);
        }
    }

    /**
     * Resolves all ${property.name} placeholders in the JSON string.
     *
     * Resolution order:
     * 1. System properties (highest priority)
     * 2. Properties file values
     * 3. Environment variables
     *
     * If no value found, placeholder remains unchanged (allows debugging).
     */
    private String resolvePlaceholders(String content) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(content);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String placeholder = matcher.group(1);
            String value = resolveProperty(placeholder);

            // Escape special regex characters in replacement value
            matcher.appendReplacement(result, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(result);

        return result.toString();
    }

    /**
     * Resolves a single property name to its value.
     */
    private String resolveProperty(String propertyName) {

        // Priority 1: Runtime values from UI / Workflow
        String value =
                WorkflowDataStore.get(
                        propertyName
                );

        if (value != null) {
            return value;
        }

        // Priority 2: System property
        value =
                System.getProperty(
                        propertyName
                );

        if (value != null) {
            return value;
        }

        // Priority 3: Properties file
        value =
                testProperties.getProperty(
                        propertyName
                );

        if (value != null) {
            return value;
        }

        // Priority 4: Environment variable
        String envName =
                propertyName
                        .replace('.', '_')
                        .toUpperCase();

        value =
                System.getenv(
                        envName
                );

        if (value != null) {
            return value;
        }

        logger.warn(
                "Unresolved placeholder: ${}",
                propertyName
        );

        return "${" + propertyName + "}";
    }
}
