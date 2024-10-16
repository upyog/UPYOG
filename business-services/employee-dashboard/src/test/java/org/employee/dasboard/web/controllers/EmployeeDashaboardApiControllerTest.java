package org.employee.dasboard.web.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.employee.dasboard.TestConfiguration;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.upyog.employee.dasboard.web.controllers.EmployeeDashaboardApiController;

/**
* API tests for EmployeeDashaboardApiController
*/
@Ignore
@RunWith(SpringRunner.class)
@WebMvcTest(EmployeeDashaboardApiController.class)
@Import(TestConfiguration.class)
public class EmployeeDashaboardApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void employeeDashaboardSearchPostSuccess() throws Exception {
        mockMvc.perform(post("/employee-dasboard/employee-dashaboard/_search").contentType(MediaType
        .APPLICATION_JSON_UTF8))
        .andExpect(status().isOk());
    }

    @Test
    public void employeeDashaboardSearchPostFailure() throws Exception {
        mockMvc.perform(post("/employee-dasboard/employee-dashaboard/_search").contentType(MediaType
        .APPLICATION_JSON_UTF8))
        .andExpect(status().isBadRequest());
    }

}
