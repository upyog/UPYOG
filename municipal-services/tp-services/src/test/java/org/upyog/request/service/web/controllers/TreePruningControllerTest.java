package org.upyog.request.service.web.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.upyog.request.service.TestConfiguration;
import org.upyog.tp.web.controllers.TreePruningController;

/**
* API tests for CreateApiController
*/
@Disabled
@ExtendWith(SpringExtension.class)
@WebMvcTest(TreePruningController.class)
@Import(TestConfiguration.class)
public class TreePruningControllerTest {

    @Autowired
    private MockMvc mockMvc;

//    @Test
    public void createPostSuccess() throws Exception {
        mockMvc.perform(post("/tp/_create").contentType(MediaType
        .APPLICATION_JSON_UTF8))
        .andExpect(status().isOk());
    }

//    @Test
    public void createPostFailure() throws Exception {
        mockMvc.perform(post("/tp/_create").contentType(MediaType
        .APPLICATION_JSON_UTF8))
        .andExpect(status().isBadRequest());
    }

}
