package org.upyog.adapter.extractor;

import java.time.LocalDate;

import org.upyog.adapter.common.constants.Module;
import org.upyog.adapter.model.DashboardData;

/**
 * Strategy interface defining data extraction rules for a specific UPYOG business module.
 * 
 * <p>Implementations (exception.g. {@code PtModuleExtractor}, {@code TlModuleExtractor}) encapsulate
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
}
