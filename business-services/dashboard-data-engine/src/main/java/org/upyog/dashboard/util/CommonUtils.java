package org.upyog.dashboard.util;

import java.time.Instant;
import java.util.UUID;

public class CommonUtils {

    /**
     * Gets the current time in epoch milliseconds.
     * @return current epoch time in milliseconds
     */
    public static long getCurrentEpochMillis() {
        return Instant.now().toEpochMilli();
    }

    /**
     * Generates a random UUID as a string.
     * @return a random UUID string
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }
}
