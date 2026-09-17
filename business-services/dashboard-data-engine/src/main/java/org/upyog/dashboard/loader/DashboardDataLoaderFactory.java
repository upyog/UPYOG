package org.upyog.dashboard.loader;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.upyog.dashboard.config.DashboardProperties;

import lombok.extern.slf4j.Slf4j;

/**
 * Factory and resolver for {@link DashboardDataLoader} strategies.
 * <p>
 * Decouples client orchestrators from concrete transport loaders (HTTP REST vs. S3 Excel upload),
 * satisfying the Open/Closed Principle (OCP) and Single Responsibility Principle (SRP).
 */
@Slf4j
@Component
public class DashboardDataLoaderFactory {

    private final DashboardDataLoader httpDataLoader;
    private final DashboardDataLoader s3DataLoader;
    private final DashboardProperties properties;

    public DashboardDataLoaderFactory(
            @Qualifier("httpDataLoader") DashboardDataLoader httpDataLoader,
            @Qualifier("s3DataLoader") DashboardDataLoader s3DataLoader,
            DashboardProperties properties) {
        this.httpDataLoader = httpDataLoader;
        this.s3DataLoader = s3DataLoader;
        this.properties = properties;
    }

    /**
     * Resolves the appropriate {@link DashboardDataLoader} for daily ingestion based on configured upload mode.
     *
     * @return the resolved {@link DashboardDataLoader} instance
     */
    public DashboardDataLoader getDailyDataLoader() {
        String mode = properties.getEffectiveDailyUploadMode();
        return getDataLoader(mode);
    }

    /**
     * Resolves the appropriate {@link DashboardDataLoader} strategy by mode name.
     *
     * @param uploadMode the requested upload mode ("API", "S3", "FILESTORE", etc.)
     * @return the matched {@link DashboardDataLoader} strategy
     */
    public DashboardDataLoader getDataLoader(String uploadMode) {
        if ("S3".equalsIgnoreCase(uploadMode) || "FILESTORE".equalsIgnoreCase(uploadMode)) {
            log.debug("DashboardDataLoaderFactory | Selected S3DashboardDataLoader for mode: {}", uploadMode);
            return s3DataLoader;
        }
        log.debug("DashboardDataLoaderFactory | Selected HttpDashboardDataLoader for mode: {}", uploadMode);
        return httpDataLoader;
    }
}
