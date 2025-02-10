package org.upyog.sv.web.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.upyog.sv.TestConfiguration;

/**
* API tests for CreateApiController
*/
@Ignore
@RunWith(SpringRunner.class)
@WebMvcTest(StreetVendingController.class)
@Import(TestConfiguration.class)
public class CreateApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void createPostSuccess() throws Exception {
        mockMvc.perform(post("/sv/_create").contentType(MediaType
        .APPLICATION_JSON_UTF8))
        .andExpect(status().isOk());
    }

    @Test
    public void createPostFailure() throws Exception {
        mockMvc.perform(post("/sv/_create").contentType(MediaType
        .APPLICATION_JSON_UTF8))
        .andExpect(status().isBadRequest());
    }

}
