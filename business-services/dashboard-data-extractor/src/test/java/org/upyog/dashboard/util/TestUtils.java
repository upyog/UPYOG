package org.upyog.dashboard.util;

import java.lang.reflect.Field;

/**
 * Utility helper methods for unit testing reflection-based operations.
 */
public class TestUtils {

    /**
     * Private constructor to prevent instantiation.
     */
    private TestUtils() {}

    /**
     * Injects a value into a private field of a target object via reflection.
     *
     * @param target the target object
     * @param fieldName the name of the field to inject
     * @param value the value to set
     * @throws Exception if reflection access fails
     */
    public static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
