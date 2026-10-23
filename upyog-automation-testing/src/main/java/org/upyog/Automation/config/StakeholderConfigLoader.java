package org.upyog.Automation.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;

public class StakeholderConfigLoader {

    private static final ObjectMapper mapper =
            new ObjectMapper();

    public static ModuleData load(String jsonPath) {

        try (InputStream is =
                     StakeholderConfigLoader.class
                             .getClassLoader()
                             .getResourceAsStream(jsonPath)) {

            if (is == null) {
                throw new RuntimeException(
                        "Config file not found : " + jsonPath
                );
            }

            return mapper.readValue(
                    is,
                    ModuleData.class
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to load config : " + jsonPath,
                    e
            );
        }
    }
}