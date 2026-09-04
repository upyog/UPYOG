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

    /**
     * Generates a standard S3 object key formatted as:
     * {@code <folder>/<tenantId>/<moduleName>/<uuid>_<fileName>}
     *
     * @param folder     the root S3 folder
     * @param tenantId   the tenant identifier
     * @param moduleName the module name
     * @param fileName   the file name
     * @return structured S3 object key
     */
    public static String buildS3Key(String folder, String tenantId, String moduleName, String fileName) {
        StringBuilder keyBuilder = new StringBuilder();
        if (folder != null && !folder.trim().isEmpty()) {
            keyBuilder.append(folder.trim()).append("/");
        }
        if (tenantId != null && !tenantId.trim().isEmpty()) {
            keyBuilder.append(tenantId.trim()).append("/");
        }
        if (moduleName != null && !moduleName.trim().isEmpty()) {
            keyBuilder.append(moduleName.trim()).append("/");
        }
        keyBuilder.append(generateUUID()).append("_").append(fileName != null ? fileName : "file");
        return keyBuilder.toString();
    }
}
