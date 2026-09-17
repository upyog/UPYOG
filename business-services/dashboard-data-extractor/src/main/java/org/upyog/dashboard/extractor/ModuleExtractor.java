package org.upyog.dashboard.extractor;

import java.time.LocalDate;

import org.upyog.dashboard.common.constants.Module;

/**
 * Strategy interface defining data extraction rules for a specific UPYOG business module.
 * 
 * <p>Implementations (e.g. {@code PtModuleExtractor}, {@code ChbModuleExtractor}, {@code PgrModuleExtractor}) encapsulate
 * database metrics collection and SQL query execution for their target business module.
 */
public interface ModuleExtractor<T> {

    /**
     * Returns the business module constant handled by this extractor implementation.
     * 
     * @return the {@link Module} enum constant
     */
    Module getModule();

    /**
     * Extracts daily dashboard metrics for the target date from the business database.
     * 
     * @param targetDate the date for metric extraction
     * @return T object containing the extracted metrics payload
     */
    T extractData(LocalDate targetDate);

    /**
     * Evaluates whether all metrics in an extracted data object for this module are zero.
     * <p>
     * Default implementation evaluates to false (indicating non-zero data present).
     * Modules can override this to implement domain-specific zero-metric checks,
     * adhering to the Open/Closed Principle (OCP).
     *
     * @param item the extracted domain item (or list item)
     * @return true if all metrics are zero, false otherwise
     */
    default boolean isZeroMetrics(Object item) {
        return false;
    }
}
