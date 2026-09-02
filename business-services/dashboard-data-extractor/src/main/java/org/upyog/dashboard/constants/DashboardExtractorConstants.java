package org.upyog.dashboard.constants;

import org.upyog.dashboard.common.constants.DashboardConstants;

public final class DashboardExtractorConstants extends DashboardConstants {
    
    private DashboardExtractorConstants() {} // Prevent instantiation
    
    // Extractor-Specific Hierarchy / JSON Keys
    public static final String KEY_WARD = "ward";
    public static final String KEY_ULB = "ulb";
    public static final String KEY_REGION = "region";
    public static final String KEY_STATE = "state";

    // Query Parameter Keys
    public static final String PARAM_START_TIME = "startTime";
    public static final String PARAM_END_TIME = "endTime";
}
