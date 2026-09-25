package org.upyog.cdwm.calculator.web.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.upyog.cdwm.calculator.TestConfiguration;

/**
* API tests for V1ApiController
*/
@Disabled
@ExtendWith(SpringExtension.class)
@WebMvcTest(CalculatorControllerTest.class)
@Import(TestConfiguration.class)
class CalculatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void v1RegistrationCreatePostSuccess() throws Exception {
        mockMvc.perform(post("/cnd-calculator/v1/_create").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
    }

    @Test
    void v1RegistrationCreatePostFailure() throws Exception {
        mockMvc.perform(post("/cnd-calculator/v1/_create").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
    }

}
