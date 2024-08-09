package digit.web.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import digit.TestConfiguration;

/**
* API tests for ApplicationApiController
*/
@Disabled
@WebMvcTest(ApplicationApiController.class)
@Import(TestConfiguration.class)
class ApplicationApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void applicationDetailsPostSuccess() throws Exception {
        mockMvc.perform(post("/application/details").contentType(MediaType
        .APPLICATION_JSON))
        .andExpect(status().isOk());
    }

    @Test
    void applicationDetailsPostFailure() throws Exception {
        mockMvc.perform(post("/application/details").contentType(MediaType
        .APPLICATION_JSON))
        .andExpect(status().isBadRequest());
    }

    @Test
    void applicationStatusPostSuccess() throws Exception {
        mockMvc.perform(post("/application/status").contentType(MediaType
        .APPLICATION_JSON))
        .andExpect(status().isOk());
    }

    @Test
    void applicationStatusPostFailure() throws Exception {
        mockMvc.perform(post("/application/status").contentType(MediaType
        .APPLICATION_JSON))
        .andExpect(status().isBadRequest());
    }

    @Test
    void applicationSubmitPostSuccess() throws Exception {
        mockMvc.perform(post("/application/submit").contentType(MediaType
        .APPLICATION_JSON))
        .andExpect(status().isOk());
    }

    @Test
    void applicationSubmitPostFailure() throws Exception {
        mockMvc.perform(post("/application/submit").contentType(MediaType
        .APPLICATION_JSON))
        .andExpect(status().isBadRequest());
    }

}
