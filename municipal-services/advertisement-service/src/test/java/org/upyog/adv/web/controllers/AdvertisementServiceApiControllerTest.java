package org.upyog.adv.web.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.upyog.adv.TestConfiguration;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * API tests for AdvertisementServiceApiController
 */
@Disabled
@ExtendWith(SpringExtension.class)
@WebMvcTest(AdvertisementServiceApiController.class)
@Import(TestConfiguration.class)
public class AdvertisementServiceApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void advertisementServiceCreatePostSuccess() throws Exception {
        mockMvc.perform(post("/advertisement-service/advertisement-service/_create")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void advertisementServiceCreatePostFailure() throws Exception {
        mockMvc.perform(post("/advertisement-service/advertisement-service/_create")
                        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
    }

}
