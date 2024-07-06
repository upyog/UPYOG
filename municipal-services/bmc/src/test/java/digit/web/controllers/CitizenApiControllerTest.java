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
* API tests for CitizenApiController
*/
@Disabled
@WebMvcTest(CitizenApiController.class)
@Import(TestConfiguration.class)
class CitizenApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void citizenLoginPostSuccess() throws Exception {
        mockMvc.perform(post("/citizen/login").contentType(MediaType
        .APPLICATION_JSON))
        .andExpect(status().isOk());
    }

    @Test
    void citizenLoginPostFailure() throws Exception {
        mockMvc.perform(post("/citizen/login").contentType(MediaType
        .APPLICATION_JSON))
        .andExpect(status().isBadRequest());
    }

    @Test
    void citizenRegisterPostSuccess() throws Exception {
        mockMvc.perform(post("/citizen/register").contentType(MediaType
        .APPLICATION_JSON))
        .andExpect(status().isOk());
    }

    @Test
    void citizenRegisterPostFailure() throws Exception {
        mockMvc.perform(post("/citizen/register").contentType(MediaType
        .APPLICATION_JSON))
        .andExpect(status().isBadRequest());
    }

}
