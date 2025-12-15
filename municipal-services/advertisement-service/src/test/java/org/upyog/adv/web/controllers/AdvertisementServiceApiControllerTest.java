package org.upyog.adv.web.controllers;

import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;
import org.upyog.adv.TestConfiguration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for AdvertisementServiceApiController
 */
@WebMvcTest(AdvertisementServiceApiController.class)
@Import(TestConfiguration.class)
public class AdvertisementServiceApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void advertisementServiceCreatePostSuccess() throws Exception {
    }

    @Test
    public void advertisementServiceCreatePostFailure() throws Exception {
        // Test implementation
    }

}
