package org.egov.asset.web.controllers;

import org.egov.asset.TestConfiguration;
<<<<<<< HEAD
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * API tests for V1ApiController
 */
<<<<<<< HEAD
@Disabled
@ExtendWith(SpringExtension.class)
=======
@Ignore
@RunWith(SpringRunner.class)
>>>>>>> master-LTS
@WebMvcTest(AssetControllerV1.class)
@Import(TestConfiguration.class)
public class AssetV1ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void v1AssetsCreatePostSuccess() throws Exception {
        mockMvc.perform(post("/asset-services/v1/assets/_create").contentType(MediaType
                        .APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    public void v1AssetsCreatePostFailure() throws Exception {
        mockMvc.perform(post("/asset-services/v1/assets/_create").contentType(MediaType
                        .APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void v1AssetsSearchPostSuccess() throws Exception {
        mockMvc.perform(post("/asset-services/v1/assets/_search").contentType(MediaType
                        .APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    public void v1AssetsSearchPostFailure() throws Exception {
        mockMvc.perform(post("/asset-services/v1/assets/_search").contentType(MediaType
                        .APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void v1AssetsUpdatePostSuccess() throws Exception {
        mockMvc.perform(post("/asset-services/v1/assets/_update").contentType(MediaType
                        .APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    public void v1AssetsUpdatePostFailure() throws Exception {
        mockMvc.perform(post("/asset-services/v1/assets/_update").contentType(MediaType
                        .APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }

}
