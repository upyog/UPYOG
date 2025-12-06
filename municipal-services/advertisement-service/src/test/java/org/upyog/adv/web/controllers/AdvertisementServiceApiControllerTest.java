package org.upyog.adv.web.controllers;

<<<<<<< HEAD
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;
=======
import org.upyog.adv.web.models.ErrorRes;

import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
>>>>>>> master-LTS
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.upyog.adv.TestConfiguration;

<<<<<<< HEAD
import static org.mockito.ArgumentMatchers.any;
=======
    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
>>>>>>> master-LTS
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
<<<<<<< HEAD
 * API tests for AdvertisementServiceApiController
 */
@Disabled
@ExtendWith(SpringExtension.class)
=======
* API tests for AdvertisementServiceApiController
*/
@Ignore
@RunWith(SpringRunner.class)
>>>>>>> master-LTS
@WebMvcTest(AdvertisementServiceApiController.class)
@Import(TestConfiguration.class)
public class AdvertisementServiceApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void advertisementServiceCreatePostSuccess() throws Exception {
<<<<<<< HEAD
        mockMvc.perform(post("/advertisement-service/advertisement-service/_create")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
=======
        mockMvc.perform(post("/advertisement-service/advertisement-service/_create").contentType(MediaType
        .APPLICATION_JSON_UTF8))
        .andExpect(status().isOk());
>>>>>>> master-LTS
    }

    @Test
    public void advertisementServiceCreatePostFailure() throws Exception {
<<<<<<< HEAD
        mockMvc.perform(post("/advertisement-service/advertisement-service/_create")
                        .contentType(MediaType.APPLICATION_JSON))
=======
        mockMvc.perform(post("/advertisement-service/advertisement-service/_create").contentType(MediaType
        .APPLICATION_JSON_UTF8))
>>>>>>> master-LTS
        .andExpect(status().isBadRequest());
    }

}
