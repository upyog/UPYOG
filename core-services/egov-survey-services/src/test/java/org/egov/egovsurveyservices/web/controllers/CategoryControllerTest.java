package org.egov.egovsurveyservices.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.egovsurveyservices.service.CategoryService;
import org.egov.egovsurveyservices.web.models.CategoryRequest;
import org.egov.egovsurveyservices.web.models.CategorySearchCriteria;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CategoryService categoryService;

    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testCreateCategory() throws Exception {

        mockMvc.perform(post("/egov-ss/category/_create").contentType
                        (MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(new CategoryRequest())))
                .andExpect(status().isCreated());
    }

    @Test
    public void testUpdateCategory() throws Exception {

        mockMvc.perform(put("/egov-ss/category/_update").contentType
                        (MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(new CategoryRequest())))
                .andExpect(status().isOk());
    }

    @Test
    public void testSearchCategory() throws Exception {

        mockMvc.perform(post("/egov-ss/category/_search").contentType
                        (MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(new CategorySearchCriteria())))
                .andExpect(status().isOk());
    }
}