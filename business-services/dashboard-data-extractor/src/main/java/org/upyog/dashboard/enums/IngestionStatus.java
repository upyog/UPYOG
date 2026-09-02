package org.upyog.dashboard.enums;

import org.upyog.dashboard.constants.DashboardExtractorConstants;
import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * Enumeration representing the possible outcomes of an ingestion attempt.
 *
 * <p>Includes an {@link #UNKNOWN} default fallback constant to ensure that
 * unmapped status strings do not disrupt the ingestion pipeline (Point 6).</p>
 */
@Getter
public enum IngestionStatus {

    SUCCESS(DashboardExtractorConstants.STATUS_SUCCESS, true),
    FAILURE(DashboardExtractorConstants.STATUS_FAILURE, false),
    SUCCESS_ZERO_METRICS("SUCCESS_ZERO_METRICS", true),
    SUCCESS_DUPLICATE("SUCCESS_DUPLICATE", true),
    SKIPPED("SKIPPED", false),
    UNKNOWN("UNKNOWN", false);

    private final String value;
    private final boolean isSuccessfulState;

    IngestionStatus(String value, boolean isSuccessfulState) {
        this.value = value;
        this.isSuccessfulState = isSuccessfulState;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    /**
     * Parses a string status into an {@link IngestionStatus} enum constant.
     * Falls back to {@link #UNKNOWN} for unmapped or {@code null} status values.
     *
     * @param status the status string to parse
     * @return the matching {@link IngestionStatus} constant, or {@link #UNKNOWN} as fallback
     */
    @JsonCreator
    public static IngestionStatus fromValue(String status) {
        if (StringUtils.isBlank(status)) {
            return UNKNOWN;
        }
        for (IngestionStatus s : values()) {
            if (s.value.equalsIgnoreCase(status.trim())) {
                return s;
            }
        }
        return UNKNOWN;
    }

    /**
     * Returns whether this status represents a successful ingestion outcome
     * that should advance the tracker date.
     *
     * @return {@code true} if successful (SUCCESS, SUCCESS_ZERO_METRICS, SUCCESS_DUPLICATE), {@code false} otherwise
     */
    public boolean isSuccess() {
        return this.isSuccessfulState;
    }
}
