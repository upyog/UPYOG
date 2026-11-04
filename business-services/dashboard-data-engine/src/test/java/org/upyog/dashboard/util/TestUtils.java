package org.upyog.dashboard.util;

import java.lang.reflect.Field;

public class TestUtils {

    public static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
