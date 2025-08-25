package org.upyog.cdwm.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import digit.models.coremodels.RequestInfoWrapper;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.upyog.cdwm.service.impl.CNDServiceImpl;
import org.upyog.cdwm.utils.TestRequestObjectBuilder;
import org.upyog.cdwm.web.models.*;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for GlobalExceptionHandler to ensure proper handling of exceptions
 * thrown by CNDController.
 */
@ExtendWith(MockitoExtension.class)
//@SpringBootTest
public class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @MockBean
    private CNDServiceImpl cndService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private CNDController cndController;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.standaloneSetup(cndController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }


    /**
     * Tests handling of IllegalArgumentException for /v1/_create endpoint.
     */
    //      @Test
    void testHandleIllegalArgumentExceptionCreate() throws Exception {
        when(cndService.createConstructionAndDemolitionRequest(null))
                .thenThrow(new IllegalArgumentException("Invalid input data"));

        MvcResult result = mockMvc.perform(post("/v1/_create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error.message").value("An unexpected error occurred. Please contact support."))
                .andReturn();
        System.out.println("testHandleIllegalArgumentExceptionCreate Response: " + result.getResponse().getContentAsString());
    }

    /**
     * Tests handling of NullPointerException for /v1/_create endpoint.
     */
    //      @Test
    void testHandleNullPointerExceptionCreate() throws Exception {
        when(cndService.createConstructionAndDemolitionRequest(null))
                .thenThrow(new NullPointerException("Unexpected error occurred"));

        MvcResult result = mockMvc.perform(post("/v1/_create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error.message").value("An unexpected error occurred. Please contact support."))
                .andReturn();
        System.out.println("testHandleNullPointerExceptionCreate Response: " + result.getResponse().getContentAsString());
    }

    /**
     * Tests handling of custom RuntimeException for /v1/_create endpoint.
     */
    //      @Test
    void testHandleCustomApplicationExceptionCreate() throws Exception {
        when(cndService.createConstructionAndDemolitionRequest(any(CNDApplicationRequest.class)))
                .thenThrow(new RuntimeException("Application processing failed"));
        String requestBody = new ObjectMapper().writeValueAsString(TestRequestObjectBuilder.createCNDApplicationRequest());
        MvcResult result = mockMvc.perform(post("/v1/_create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isInternalServerError())
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        ErrorResponse errorResponse = new ObjectMapper().readValue(responseContent, ErrorResponse.class);
        assertEquals("An unexpected error occurred. Please contact support.", errorResponse.getError().getMessage());
        System.out.println("testHandleCustomApplicationExceptionCreate Response: " + responseContent);
    }

    /**
     * Tests handling of generic RuntimeException for /v1/_create endpoint.
     */
    //      @Test
    void testHandleUnknownExceptionCreate() throws Exception {
        when(cndService.createConstructionAndDemolitionRequest(any(CNDApplicationRequest.class)))
                .thenThrow(new RuntimeException("Unknown system error"));
        String requestBody = new ObjectMapper().writeValueAsString(TestRequestObjectBuilder.createCNDApplicationRequest());
        MvcResult result = mockMvc.perform(post("/v1/_create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error.message").value("An unexpected error occurred. Please contact support."))
                .andReturn();
        System.out.println("testHandleUnknownExceptionCreate Response: " + result.getResponse().getContentAsString());
    }

    /**
     * Tests handling of validation exceptions (empty request body) for /v1/_create endpoint.
     */
    //      @Test
    void testHandleValidationExceptionCreate() throws Exception {
        MvcResult result = mockMvc.perform(post("/v1/_create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error.message").exists())
                .andReturn();
        System.out.println("testHandleValidationExceptionCreate Response: " + result.getResponse().getContentAsString());
    }

    /**
     * Tests handling of IllegalArgumentException for /v1/_search endpoint.
     */
    //      @Test
    void testHandleIllegalArgumentExceptionSearch() throws Exception {
        when(cndService.getCNDApplicationDetails(any(RequestInfo.class), any(CNDServiceSearchCriteria.class)))
                .thenThrow(new IllegalArgumentException("Invalid search criteria"));
        RequestInfoWrapper requestInfoWrapper = new RequestInfoWrapper();
        requestInfoWrapper.setRequestInfo(TestRequestObjectBuilder.createSampleRequestInfo());

        MvcResult result = mockMvc.perform(post("/v1/_search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestInfoWrapper)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error.message").value("An unexpected error occurred. Please contact support."))
                .andReturn();
        System.out.println("testHandleIllegalArgumentExceptionSearch Response: " + result.getResponse().getContentAsString());
    }

    /**
     * Tests handling of NullPointerException for /v1/_search endpoint.
     */
    //      @Test
    void testHandleNullPointerExceptionSearch() throws Exception {
        when(cndService.getCNDApplicationDetails(any(RequestInfo.class), any(CNDServiceSearchCriteria.class)))
                .thenThrow(new NullPointerException("Unexpected error in search"));

        RequestInfoWrapper requestInfoWrapper = new RequestInfoWrapper();
        requestInfoWrapper.setRequestInfo(TestRequestObjectBuilder.createSampleRequestInfo());

        MvcResult result = mockMvc.perform(post("/v1/_search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestInfoWrapper)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error.message").value("An unexpected error occurred. Please contact support."))
                .andReturn();
        System.out.println("testHandleNullPointerExceptionSearch Response: " + result.getResponse().getContentAsString());
    }

    /**
     * Tests handling of generic RuntimeException for /v1/_search endpoint.
     */
    //      @Test
    void testHandleUnknownExceptionSearch() throws Exception {
        when(cndService.getCNDApplicationDetails(any(RequestInfo.class), any(CNDServiceSearchCriteria.class)))
                .thenThrow(new RuntimeException("Search operation failed"));

        RequestInfoWrapper requestInfoWrapper = new RequestInfoWrapper();
        requestInfoWrapper.setRequestInfo(TestRequestObjectBuilder.createSampleRequestInfo());

        MvcResult result = mockMvc.perform(post("/v1/_search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestInfoWrapper)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error.message").value("An unexpected error occurred. Please contact support."))
                .andReturn();
        System.out.println("testHandleUnknownExceptionSearch Response: " + result.getResponse().getContentAsString());
    }

    /**
     * Tests handling of IllegalArgumentException for /v1/_update endpoint.
     */
    //      @Test
    void testHandleIllegalArgumentExceptionUpdate() throws Exception {
        when(cndService.updateCNDApplicationDetails(any(CNDApplicationRequest.class), any(), any()))
                .thenThrow(new IllegalArgumentException("Invalid update data"));

        String requestBody = new ObjectMapper().writeValueAsString(TestRequestObjectBuilder.createCNDApplicationRequest());

        MvcResult result = mockMvc.perform(post("/v1/_update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error.message").value("An unexpected error occurred. Please contact support."))
                .andReturn();
        System.out.println("testHandleIllegalArgumentExceptionUpdate Response: " + result.getResponse().getContentAsString());
    }

    /**
     * Tests handling of NullPointerException for /v1/_update endpoint.
     */
    //      @Test
    void testHandleNullPointerExceptionUpdate() throws Exception {
        when(cndService.updateCNDApplicationDetails(any(CNDApplicationRequest.class), any(), any()))
                .thenThrow(new NullPointerException("Update operation failed"));

        String requestBody = new ObjectMapper().writeValueAsString(TestRequestObjectBuilder.createCNDApplicationRequest());

        MvcResult result = mockMvc.perform(post("/v1/_update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error.message").value("An unexpected error occurred. Please contact support."))
                .andReturn();
        System.out.println("testHandleNullPointerExceptionUpdate Response: " + result.getResponse().getContentAsString());
    }

    /**
     * Tests handling of generic RuntimeException for /v1/_update endpoint.
     */
    //      @Test
    void testHandleUnknownExceptionUpdate() throws Exception {
        when(cndService.updateCNDApplicationDetails(any(CNDApplicationRequest.class), any(), any()))
                .thenThrow(new RuntimeException("Update operation failed"));

        String requestBody = new ObjectMapper().writeValueAsString(TestRequestObjectBuilder.createCNDApplicationRequest());

        MvcResult result = mockMvc.perform(post("/v1/_update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error.message").value("An unexpected error occurred. Please contact support."))
                .andReturn();
        System.out.println("testHandleUnknownExceptionUpdate Response: " + result.getResponse().getContentAsString());
    }
}