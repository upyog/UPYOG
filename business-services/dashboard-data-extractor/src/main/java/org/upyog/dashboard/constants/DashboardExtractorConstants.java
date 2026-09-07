package org.upyog.dashboard.constants;

import org.upyog.dashboard.common.constants.DashboardConstants;

/**
 * Constants specific to the data extractor service, extending common dashboard constants.
 */
public final class DashboardExtractorConstants extends DashboardConstants {
    
    /**
     * Private constructor to prevent instantiation of constant class.
     */
    private DashboardExtractorConstants() {}
    
    // Extractor-Specific Hierarchy / JSON Keys
    /** Hierarchy key for Ward level. */
    public static final String KEY_WARD = "ward";
    /** Hierarchy key for ULB (Urban Local Body) level. */
    public static final String KEY_ULB = "ulb";
    /** Hierarchy key for Region level. */
    public static final String KEY_REGION = "region";
    /** Hierarchy key for State level. */
    public static final String KEY_STATE = "state";

    // Query Parameter Keys
    /** Named parameter key for start timestamp in SQL extraction queries. */
    public static final String PARAM_START_TIME = "startTime";
    /** Named parameter key for end timestamp in SQL extraction queries. */
    public static final String PARAM_END_TIME = "endTime";
}
