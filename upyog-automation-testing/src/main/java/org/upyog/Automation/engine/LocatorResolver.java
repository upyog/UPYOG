package org.upyog.Automation.engine;

import org.openqa.selenium.By;
import org.upyog.Automation.model.TestInstruction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Smart Locator Resolution
 *
 * Dynamically creates Selenium By objects based on the locatorStrategy
 * specified in the JSON configuration. This eliminates hardcoded locators
 * in Java code—all element identification is driven by config.
 *
 * Supported strategies:
 * - ID: By.id()
 * - NAME: By.name()
 * - XPATH: By.xpath()
 * - CSS: By.cssSelector()
 * - CLASS_NAME: By.className()
 * - TAG_NAME: By.tagName()
 * - LINK_TEXT: By.linkText()
 * - PARTIAL_LINK_TEXT: By.partialLinkText()
 */
public class LocatorResolver {

    private static final Logger logger = LoggerFactory.getLogger(LocatorResolver.class);

    /**
     * Resolves a TestInstruction's locator configuration into a Selenium By object.
     *
     * This method uses a switch expression (Java 14+) to map the string-based
     * locatorStrategy from JSON to the appropriate Selenium By factory method.
     *
     * @param instruction The test instruction containing locator details
     * @return Selenium By object for element location
     * @throws IllegalArgumentException if locatorStrategy is unknown
     */
    public By resolveLocator(TestInstruction instruction) {
        String strategy = instruction.getLocatorStrategy();
        String value = instruction.getLocatorValue();

        if (strategy == null || value == null) {
            throw new IllegalArgumentException(
                    "Locator strategy and value must not be null for step: " + instruction.getStepName()
            );
        }

        // Normalize strategy to uppercase for case-insensitive matching
        switch (strategy.toUpperCase()) {

            case "ID":
                return By.id(value);

            case "NAME":
                return By.name(value);

            case "XPATH":
                return By.xpath(value);

            case "CSS":
                return By.cssSelector(value);

            case "CLASS_NAME":
                return By.className(value);

            case "TAG_NAME":
                return By.tagName(value);

            case "LINK_TEXT":
                return By.linkText(value);

            case "PARTIAL_LINK_TEXT":
                return By.partialLinkText(value);

            // Special case: URL actions don't need locators
            case "URL":
                return null;

            default:
                throw new IllegalArgumentException(
                        "Unknown locator strategy '"
                                + strategy
                                + "' for step: "
                                + instruction.getStepName()
                );
        }
    }
}
