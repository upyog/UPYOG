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
* API tests for AadhaarApiController
*/
@Disabled
@WebMvcTest(AadhaarApiController.class)
@Import(TestConfiguration.class)
class AadhaarApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void aadhaarAuthenticatePostSuccess() throws Exception {
        mockMvc.perform(post("/aadhaar/authenticate").contentType(MediaType
        .APPLICATION_JSON))
        .andExpect(status().isOk());
    }

    @Test
    void aadhaarAuthenticatePostFailure() throws Exception {
        mockMvc.perform(post("/aadhaar/authenticate").contentType(MediaType
        .APPLICATION_JSON))
        .andExpect(status().isBadRequest());
    }

    @Test
    void aadhaarVerifyPostSuccess() throws Exception {
        mockMvc.perform(post("/aadhaar/verify").contentType(MediaType
        .APPLICATION_JSON))
        .andExpect(status().isOk());
    }

    @Test
    void aadhaarVerifyPostFailure() throws Exception {
        mockMvc.perform(post("/aadhaar/verify").contentType(MediaType
        .APPLICATION_JSON))
        .andExpect(status().isBadRequest());
    }

}
