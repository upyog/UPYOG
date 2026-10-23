package org.upyog.Automation.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.upyog.Automation.model.WorkflowData;

import java.io.InputStream;

public class WorkflowConfigLoader {

    private static final ObjectMapper mapper =
            new ObjectMapper();

    public static WorkflowData load(String jsonPath) {

        try (InputStream is =
                     WorkflowConfigLoader.class
                             .getClassLoader()
                             .getResourceAsStream(jsonPath)) {

            if (is == null) {
                throw new RuntimeException(
                        "Workflow file not found : " + jsonPath
                );
            }

            return mapper.readValue(
                    is,
                    WorkflowData.class
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to load workflow : " + jsonPath,
                    e
            );
        }
    }
}