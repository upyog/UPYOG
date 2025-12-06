package org.upyog.pgrai.web.controllers;

<<<<<<< HEAD
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
=======
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
>>>>>>> master-LTS
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.upyog.pgrai.TestConfiguration;

<<<<<<< HEAD
=======
import static org.mockito.Matchers.any;
>>>>>>> master-LTS
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
* API tests for RequestsApiController
*/
<<<<<<< HEAD
@Disabled
@ExtendWith(SpringExtension.class)
=======
@Ignore
@RunWith(SpringRunner.class)
>>>>>>> master-LTS
@WebMvcTest(RequestsApiController.class)
@Import(TestConfiguration.class)
public class RequestsApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void requestsCreatePostSuccess() throws Exception {
<<<<<<< HEAD
        mockMvc.perform(post("/requests/_create").contentType(MediaType.APPLICATION_JSON))
=======
        mockMvc.perform(post("/requests/_create").contentType(MediaType
        .APPLICATION_JSON_UTF8))
>>>>>>> master-LTS
        .andExpect(status().isOk());
    }

    @Test
    public void requestsCreatePostFailure() throws Exception {
<<<<<<< HEAD
        mockMvc.perform(post("/requests/_create").contentType(MediaType.APPLICATION_JSON))
=======
        mockMvc.perform(post("/requests/_create").contentType(MediaType
        .APPLICATION_JSON_UTF8))
>>>>>>> master-LTS
        .andExpect(status().isBadRequest());
    }

    @Test
    public void requestsSearchPostSuccess() throws Exception {
<<<<<<< HEAD
        mockMvc.perform(post("/requests/_search").contentType(MediaType.APPLICATION_JSON))
=======
        mockMvc.perform(post("/requests/_search").contentType(MediaType
        .APPLICATION_JSON_UTF8))
>>>>>>> master-LTS
        .andExpect(status().isOk());
    }

    @Test
    public void requestsSearchPostFailure() throws Exception {
<<<<<<< HEAD
        mockMvc.perform(post("/requests/_search").contentType(MediaType.APPLICATION_JSON))
=======
        mockMvc.perform(post("/requests/_search").contentType(MediaType
        .APPLICATION_JSON_UTF8))
>>>>>>> master-LTS
        .andExpect(status().isBadRequest());
    }

    @Test
    public void requestsUpdatePostSuccess() throws Exception {
<<<<<<< HEAD
        mockMvc.perform(post("/requests/_update").contentType(MediaType.APPLICATION_JSON))
=======
        mockMvc.perform(post("/requests/_update").contentType(MediaType
        .APPLICATION_JSON_UTF8))
>>>>>>> master-LTS
        .andExpect(status().isOk());
    }

    @Test
    public void requestsUpdatePostFailure() throws Exception {
<<<<<<< HEAD
        mockMvc.perform(post("/requests/_update").contentType(MediaType.APPLICATION_JSON))
=======
        mockMvc.perform(post("/requests/_update").contentType(MediaType
        .APPLICATION_JSON_UTF8))
>>>>>>> master-LTS
        .andExpect(status().isBadRequest());
    }

}
