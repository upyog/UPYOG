package org.upyog.dashboard.util;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Utility class for retry and backoff calculations.
 */
public final class RetryUtil {

    private RetryUtil() {
        // Prevent instantiation
    }

    /**
     * Calculates an exponential backoff delay with full jitter.
     *
     * @param attempt     the current retry attempt (1-indexed)
     * @param baseDelayMs the initial base delay in milliseconds
     * @param maxDelayMs  the maximum allowed delay in milliseconds
     * @return a randomized delay in milliseconds between 0 and the capped exponential delay
     */
    public static long calculateBackoffWithJitter(int attempt, long baseDelayMs, long maxDelayMs) {
        int power = Math.min(attempt - 1, 30);
        long expDelay = baseDelayMs * (1L << power);
        if (expDelay < 0) {
            expDelay = maxDelayMs;
        }
        long currentMaxDelay = Math.min(maxDelayMs, expDelay);
        return ThreadLocalRandom.current().nextLong(0, currentMaxDelay + 1);
    }
}
