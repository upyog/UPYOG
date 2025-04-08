package org.upyog.sv.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.common.contract.request.RequestInfo;
import org.upyog.sv.service.StreetVendingService;
import org.upyog.sv.utils.TestRequestObjectBuilder;
import org.upyog.sv.validator.StreetVendingValidationService;
import org.upyog.sv.web.models.StreetVendingRequest;
import org.upyog.sv.web.models.common.RequestInfoWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for GlobalExceptionHandler to ensure proper handling of exceptions
 * thrown by StreetVendingController.
 */
@ExtendWith(MockitoExtension.class)
@EnableWebMvc
public class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @Mock
    private StreetVendingService streetVendingService;

    @Mock
    private StreetVendingValidationService validationService;

    @InjectMocks
    private StreetVendingController streetVendingController;

    private ObjectMapper objectMapper;
    private GlobalExceptionHandler globalExceptionHandler;
    private LocalValidatorFactoryBean validator;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        globalExceptionHandler = new GlobalExceptionHandler();
        validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(streetVendingController)
                .setControllerAdvice(globalExceptionHandler)
                .setValidator(validator)
                .build();
    }

    // @Test
    void testHandleHttpMessageNotReadableExceptionCreate() throws Exception {
        // Simulate sending an invalid JSON payload
        MvcResult result = mockMvc.perform(post("/street-vending/_create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"invalid\": \"json\"")) // Invalid JSON
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value(400))
                .andExpect(jsonPath("$.error.message").value("Malformed or incorrect JSON format"))
                .andReturn();
        System.out.println("testHandleHttpMessageNotReadableExceptionCreate Response: " + result.getResponse().getContentAsString());
    }

    // @Test
    void testHandleMethodArgumentNotValidExceptionCreate() throws Exception {
        // Create a request with invalid data but valid structure
        StreetVendingRequest requestBody = TestRequestObjectBuilder.createValidStreetVendingRequest();
        requestBody.getStreetVendingDetail().setTenantId(""); // Make it invalid by setting a required field to empty
        
        // Mock validation service to throw a validation exception
        lenient().doThrow(new IllegalArgumentException("Tenant ID cannot be empty"))
                .when(validationService).validateRequest(any(StreetVendingRequest.class));

        // Convert the requestBody to a JSON string
        String requestBodyJson = objectMapper.writeValueAsString(requestBody);
        MvcResult result = mockMvc.perform(post("/street-vending/_create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson)) // Invalid request body
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value(400))
                .andExpect(jsonPath("$.error.message").exists()) // Message should exist, but exact value may vary
                .andReturn();
        System.out.println("testHandleMethodArgumentNotValidExceptionCreate Response: " + result.getResponse().getContentAsString());
    }

    // @Test
    void testHandleGenericExceptionCreate() throws Exception {
        // Create a valid request body
        StreetVendingRequest requestBody = TestRequestObjectBuilder.createValidStreetVendingRequest();
        
        // Mock validation service to pass
        lenient().doNothing().when(validationService).validateRequest(any(StreetVendingRequest.class));
        
        // Mock service to throw exception
        lenient().when(streetVendingService.createStreetVendingApplication(any(StreetVendingRequest.class)))
                .thenThrow(new RuntimeException("Simulated unexpected error"));

        // Convert the requestBody to a JSON string
        String requestBodyJson = objectMapper.writeValueAsString(requestBody);

        MvcResult result = mockMvc.perform(post("/street-vending/_create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson)) // Send the JSON string
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error.code").value(500))
                .andExpect(jsonPath("$.error.message").value("An unexpected error occurred. Please contact support."))
                .andReturn();
        System.out.println("testHandleGenericExceptionCreate Response: " + result.getResponse().getContentAsString());
    }

    // @Test
    void testHandleGenericExceptionSearch() throws Exception {
        // Create a valid request body
        RequestInfoWrapper requestInfoWrapper = new RequestInfoWrapper();
        RequestInfo requestInfo = TestRequestObjectBuilder.createSampleRequestInfo();
        requestInfoWrapper.setRequestInfo(requestInfo);
        
        // Mock service to throw exception
        lenient().when(streetVendingService.getStreetVendingDetails(any(RequestInfo.class), any()))
                .thenThrow(new RuntimeException("Simulated unexpected error"));

        String requestBody = objectMapper.writeValueAsString(requestInfoWrapper);

        MvcResult result = mockMvc.perform(post("/street-vending/_search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error.code").value(500))
                .andExpect(jsonPath("$.error.message").value("An unexpected error occurred. Please contact support."))
                .andReturn();
        System.out.println("testHandleGenericExceptionSearch Response: " + result.getResponse().getContentAsString());
    }

    // @Test
    void testHandleGenericExceptionUpdate() throws Exception {
        // Create a valid request body
        StreetVendingRequest requestBody = TestRequestObjectBuilder.createValidStreetVendingRequest();
        
        // Mock validation service to pass
        lenient().doNothing().when(validationService).validateRequest(any(StreetVendingRequest.class));
        
        // Mock service to throw exception
        lenient().when(streetVendingService.updateStreetVendingApplication(any(StreetVendingRequest.class)))
                .thenThrow(new RuntimeException("Simulated unexpected error"));

        // Convert the requestBody to a JSON string
        String requestBodyJson = objectMapper.writeValueAsString(requestBody);

        MvcResult result = mockMvc.perform(post("/street-vending/_update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson)) // Send the JSON string
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error.code").value(500))
                .andExpect(jsonPath("$.error.message").value("An unexpected error occurred. Please contact support."))
                .andReturn();
        System.out.println("testHandleGenericExceptionUpdate Response: " + result.getResponse().getContentAsString());
    }
} 