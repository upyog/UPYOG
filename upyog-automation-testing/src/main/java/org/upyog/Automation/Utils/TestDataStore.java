package org.upyog.Automation.Utils;

import java.util.HashMap;
import java.util.Map;

public class TestDataStore {

    public static String PERMIT_NUMBER;
    public static String PERMIT_DATE;

    // Excel test data for the current test case
    private static final Map<String, String> TEST_DATA = new HashMap<>();

    public static void set(String key, String value) {
        TEST_DATA.put(key, value);
    }

    public static String get(String key) {
        return TEST_DATA.get(key);
    }

    public static boolean contains(String key) {
        return TEST_DATA.containsKey(key);
    }

    public static void clear() {
        TEST_DATA.clear();
    }
}
