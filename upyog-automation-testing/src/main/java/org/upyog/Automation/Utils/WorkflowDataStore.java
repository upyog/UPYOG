package org.upyog.Automation.Utils;

import java.util.HashMap;
import java.util.Map;

public class WorkflowDataStore {

    private static final Map<String, String> store =
            new HashMap<>();

    public static void put(
            String key,
            String value) {

        store.put(
                key,
                value
        );
    }

    public static String get(
            String key) {

        return store.get(
                key
        );
    }

    public static void clear() {

        store.clear();
    }

    public static void remove(String key) {
        store.remove(key);
    }
}