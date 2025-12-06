package org.upyog.sv.web.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

<<<<<<< HEAD
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
=======
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
>>>>>>> master-LTS
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
<<<<<<< HEAD
import org.springframework.test.context.junit.jupiter.SpringExtension;
=======
import org.springframework.test.context.junit4.SpringRunner;
>>>>>>> master-LTS
import org.springframework.test.web.servlet.MockMvc;
import org.upyog.sv.TestConfiguration;

/**
* API tests for CreateApiController
*/
<<<<<<< HEAD
@Disabled
@ExtendWith(SpringExtension.class)
=======
@Ignore
@RunWith(SpringRunner.class)
>>>>>>> master-LTS
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
