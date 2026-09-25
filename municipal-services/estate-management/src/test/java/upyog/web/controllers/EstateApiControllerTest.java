package upyog.web.controllers;

import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import upyog.TestConfiguration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
* API tests for EstateApiController
*/
@Ignore
@RunWith(SpringRunner.class)
@WebMvcTest(EstateController.class)
@Import(TestConfiguration.class)
public class EstateApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void estateAllotmentV1CreatePostSuccess() throws Exception {
        mockMvc.perform(post("/estate/allotment/v1/_create").contentType(MediaType
        .APPLICATION_JSON_UTF8))
        .andExpect(status().isOk());
    }

    @Test
    public void estateAllotmentV1CreatePostFailure() throws Exception {
        mockMvc.perform(post("/estate/allotment/v1/_create").contentType(MediaType
        .APPLICATION_JSON_UTF8))
        .andExpect(status().isBadRequest());
    }

    @Test
    public void estateAllotmentV1SearchPostSuccess() throws Exception {
        mockMvc.perform(post("/estate/allotment/v1/_search").contentType(MediaType
        .APPLICATION_JSON_UTF8))
        .andExpect(status().isOk());
    }

    @Test
    public void estateAllotmentV1SearchPostFailure() throws Exception {
        mockMvc.perform(post("/estate/allotment/v1/_search").contentType(MediaType
        .APPLICATION_JSON_UTF8))
        .andExpect(status().isBadRequest());
    }

    @Test
    public void estateAssetV1CreatePostSuccess() throws Exception {
        mockMvc.perform(post("/estate/asset/v1/_create").contentType(MediaType
        .APPLICATION_JSON_UTF8))
        .andExpect(status().isOk());
    }

    @Test
    public void estateAssetV1CreatePostFailure() throws Exception {
        mockMvc.perform(post("/estate/asset/v1/_create").contentType(MediaType
        .APPLICATION_JSON_UTF8))
        .andExpect(status().isBadRequest());
    }

    @Test
    public void estateAssetV1SearchPostSuccess() throws Exception {
        mockMvc.perform(post("/estate/asset/v1/_search").contentType(MediaType
        .APPLICATION_JSON_UTF8))
        .andExpect(status().isOk());
    }

    @Test
    public void estateAssetV1SearchPostFailure() throws Exception {
        mockMvc.perform(post("/estate/asset/v1/_search").contentType(MediaType
        .APPLICATION_JSON_UTF8))
        .andExpect(status().isBadRequest());
    }

}
