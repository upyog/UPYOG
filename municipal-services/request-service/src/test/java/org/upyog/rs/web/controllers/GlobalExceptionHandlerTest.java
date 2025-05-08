package org.upyog.rs.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.tracer.model.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.upyog.rs.util.TestRequestBuilder;
import org.upyog.rs.web.models.waterTanker.WaterTankerBookingRequest;

import java.lang.reflect.Method;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for GlobalExceptionHandler.
 * Tests various exception handling scenarios.
 */
public class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private RequestServiceController requestServiceController;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(requestServiceController)
                .setControllerAdvice(globalExceptionHandler)
                .build();
        objectMapper = new ObjectMapper();
    }

    /**
     * Test for handling HttpMessageNotReadableException (invalid JSON).
     */
//     @Test
    public void testHandleJsonParseException() throws Exception {
        // Create a request with invalid JSON
        String invalidJson = "{ invalid json }";
        
        // Mock the controller to throw HttpMessageNotReadableException
        lenient().when(requestServiceController.createWaterTankerBooking(any()))
                .thenThrow(new HttpMessageNotReadableException("Invalid JSON", new RuntimeException()));
        
        // Perform the request and verify the response
        mockMvc.perform(post("/water-tanker/v1/_create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value(400))
                .andExpect(jsonPath("$.error.message").value("Invalid JSON format: Invalid JSON"));
    }

    /**
     * Test for handling MethodArgumentNotValidException (validation errors).
     */
//     @Test
    public void testHandleMethodArgumentNotValidExceptionCreate() throws Exception {
        // Create a request with invalid data
        WaterTankerBookingRequest request = TestRequestBuilder.createWaterTankerRequest();
        
        // Create a MethodArgumentNotValidException with field errors
        BindingResult bindingResult = org.mockito.Mockito.mock(BindingResult.class);
        List<FieldError> fieldErrors = new ArrayList<>();
        fieldErrors.add(new FieldError("waterTankerRequest", "request.description", null, false, null, null, "must not be null"));
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);
        when(bindingResult.getAllErrors()).thenReturn(new ArrayList<>(fieldErrors));
        
        // Create a MethodParameter for the createRequest method
        Method createRequestMethod = RequestServiceController.class.getMethod("createRequest", WaterTankerBookingRequest.class);
        MethodParameter methodParameter = new MethodParameter(createRequestMethod, 0);
        
        // Mock the controller to throw the exception
        lenient().when(requestServiceController.createWaterTankerBooking(any()))
                .thenAnswer(invocation -> {
                    throw new MethodArgumentNotValidException(methodParameter, bindingResult);
                });
        
        // Perform the request and verify the response
        mockMvc.perform(post("/water-tanker/v1/_create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value(400))
                .andExpect(jsonPath("$.error.message").value("Validation failed: must not be null"));
    }

    /**
     * Test for handling CustomException.
     */
//     @Test
    public void testHandleCustomException() throws Exception {
        // Create a request
        WaterTankerBookingRequest request = TestRequestBuilder.createWaterTankerRequest();
        
        // Mock the controller to throw CustomException
        lenient().when(requestServiceController.createWaterTankerBooking(any()))
                .thenThrow(new CustomException("CUSTOM_ERROR", "Custom validation error"));
        
        // Perform the request and verify the response
        mockMvc.perform(post("/water-tanker/v1/_create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value(400))
                .andExpect(jsonPath("$.error.message").value("Custom validation error"));
    }

    /**
     * Test for handling HttpClientErrorException.
     */
//     @Test
    public void testHandleHttpClientErrorException() throws Exception {
        // Create a request
        WaterTankerBookingRequest request = TestRequestBuilder.createWaterTankerRequest();
        
        // Mock the controller to throw HttpClientErrorException
        lenient().when(requestServiceController.createWaterTankerBooking(any()))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Client error"));
        
        // Perform the request and verify the response
        mockMvc.perform(post("/water-tanker/v1/_create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value(400))
                .andExpect(jsonPath("$.error.message").value("Client error from external service: 400 BAD_REQUEST"));
    }

    /**
     * Test for handling HttpServerErrorException.
     */
//     @Test
    public void testHandleHttpServerErrorException() throws Exception {
        // Create a request
        WaterTankerBookingRequest request = TestRequestBuilder.createWaterTankerRequest();
        
        // Mock the controller to throw HttpServerErrorException
        lenient().when(requestServiceController.createWaterTankerBooking(any()))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Server error"));
        
        // Perform the request and verify the response
        mockMvc.perform(post("/water-tanker/v1/_create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error.code").value(500))
                .andExpect(jsonPath("$.error.message").value("Server error from external service: 500 INTERNAL_SERVER_ERROR"));
    }

    /**
     * Test for handling ResourceAccessException.
     */
//     @Test
    public void testHandleResourceAccessException() throws Exception {
        // Create a request
        WaterTankerBookingRequest request = TestRequestBuilder.createWaterTankerRequest();
        
        // Mock the controller to throw ResourceAccessException
        lenient().when(requestServiceController.createWaterTankerBooking(any()))
                .thenThrow(new ResourceAccessException("Connection timeout"));
        
        // Perform the request and verify the response
        mockMvc.perform(post("/water-tanker/v1/_create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error.code").value(503))
                .andExpect(jsonPath("$.error.message").value("Service unavailable: Connection timeout"));
    }

    /**
     * Test for handling UnknownHostException.
     */
//     @Test
    public void testHandleUnknownHostException() throws Exception {
        // Create a request
        WaterTankerBookingRequest request = TestRequestBuilder.createWaterTankerRequest();
        
        // Mock the controller to throw UnknownHostException wrapped in RuntimeException
        lenient().when(requestServiceController.createWaterTankerBooking(any()))
                .thenThrow(new RuntimeException(new UnknownHostException("Unknown host")));
        
        // Perform the request and verify the response
        mockMvc.perform(post("/water-tanker/v1/_create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error.code").value(503))
                .andExpect(jsonPath("$.error.message").value("Service unavailable: Unknown host - Unknown host"));
    }

    /**
     * Test for handling generic Exception.
     */
//     @Test
    public void testHandleGenericException() throws Exception {
        // Create a request
        WaterTankerBookingRequest request = TestRequestBuilder.createWaterTankerRequest();
        
        // Mock the controller to throw a generic Exception
        lenient().when(requestServiceController.createWaterTankerBooking(any()))
                .thenThrow(new RuntimeException("Unexpected error"));
        
        // Perform the request and verify the response
        mockMvc.perform(post("/water-tanker/v1/_create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error.code").value(500))
                .andExpect(jsonPath("$.error.message").value("Internal Server Error: Unexpected error"));
    }
} 