package org.upyog.adv.web.controllers;

import org.upyog.adv.web.models.ErrorRes;

import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.upyog.adv.TestConfiguration;

    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
* API tests for AdvertisementServiceApiController
*/
@Ignore
@RunWith(SpringRunner.class)
@WebMvcTest(AdvertisementServiceApiController.class)
@Import(TestConfiguration.class)
public class AdvertisementServiceApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void advertisementServiceCreatePostSuccess() throws Exception {
        mockMvc.perform(post("/advertisement-service/advertisement-service/_create").contentType(MediaType
        .APPLICATION_JSON_UTF8))
        .andExpect(status().isOk());
    }

    @Test
    public void advertisementServiceCreatePostFailure() throws Exception {
        mockMvc.perform(post("/advertisement-service/advertisement-service/_create").contentType(MediaType
        .APPLICATION_JSON_UTF8))
        .andExpect(status().isBadRequest());
    }

}
