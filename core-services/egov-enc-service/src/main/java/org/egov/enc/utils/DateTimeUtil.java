package org.egov.enc.utils;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * Utility class for datetime operations used across the encryption service.
 * Provides consistent methods for getting current datetime values in different formats.
 */
public class DateTimeUtil {

    /**
     * Gets the current epoch milliseconds timestamp.
     * 
     * @return Current time in epoch milliseconds
     */
    public static long getCurrentEpochMillis() {
        return Instant.now().toEpochMilli();
    }

    /**
     * Gets the current LocalDateTime in system default timezone.
     * 
     * @return Current LocalDateTime
     */
    public static LocalDateTime getCurrentLocalDateTime() {
        return LocalDateTime.now();
    }


} 