package org.upyog.cdwm.web.controllers;

import org.upyog.cdwm.web.models.RequestInfo;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.upyog.cdwm.TestConfiguration;

import static org.mockito.Matchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
* API tests for CreateApiController
*/
@Ignore
@RunWith(SpringRunner.class)
@WebMvcTest(CNDController.class)
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
