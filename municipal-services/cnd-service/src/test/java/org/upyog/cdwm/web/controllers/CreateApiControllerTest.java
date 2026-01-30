package org.upyog.cdwm.web.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.upyog.cdwm.TestConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * API tests for CreateApiController
 */
@Disabled
@ExtendWith(SpringExtension.class)   // ✅ JUnit 5 extension
@WebMvcTest(CNDController.class)
@Import(TestConfiguration.class)
public class CreateApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void createPostSuccess() throws Exception {
        mockMvc.perform(post("/cnd-service/v1/_create").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void createPostFailure() throws Exception {
        mockMvc.perform(post("/cnd-service/v1/_create").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
