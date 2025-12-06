package org.upyog.cdwm.web.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

<<<<<<< HEAD
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
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
import org.springframework.test.web.servlet.MockMvc;
import org.upyog.cdwm.TestConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * API tests for CreateApiController
 */
@Disabled
@ExtendWith(SpringExtension.class)   // ✅ JUnit 5 extension
=======
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.upyog.cdwm.TestConfiguration;

/**
* API tests for CreateApiController
*/
@Ignore
@RunWith(SpringRunner.class)
>>>>>>> master-LTS
@WebMvcTest(CNDController.class)
@Import(TestConfiguration.class)
public class CreateApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void createPostSuccess() throws Exception {
<<<<<<< HEAD
        mockMvc.perform(post("/cnd-service/v1/_create").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
=======
        mockMvc.perform(post("/sv/_create").contentType(MediaType
        .APPLICATION_JSON_UTF8))
        .andExpect(status().isOk());
>>>>>>> master-LTS
    }

    @Test
    public void createPostFailure() throws Exception {
<<<<<<< HEAD
        mockMvc.perform(post("/cnd-service/v1/_create").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
=======
        mockMvc.perform(post("/sv/_create").contentType(MediaType
        .APPLICATION_JSON_UTF8))
        .andExpect(status().isBadRequest());
    }

>>>>>>> master-LTS
}
