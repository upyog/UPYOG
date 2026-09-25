package org.upyog.tp.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.tracer.model.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.upyog.tp.util.TestRequestBuilder;
import org.upyog.tp.web.models.treePruning.TreePruningBookingRequest;

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
class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private TreePruningController treePruningController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(treePruningController)
                .setControllerAdvice(globalExceptionHandler)
                .build();
        objectMapper = new ObjectMapper();
    }

    /**
     * Test for handling HttpMessageNotReadableException (invalid JSON).
     */
    @Test
    void testHandleJsonParseException() throws Exception {
        String invalidJson = "{ invalid json }";

        mockMvc.perform(post("/tree-pruning/v1/_create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value(400))
                .andExpect(jsonPath("$.error.message").value("Invalid JSON format: Invalid JSON"));
    }

    /**
     * Test for handling MethodArgumentNotValidException (validation errors).
     */
    @Test
    void testHandleMethodArgumentNotValidExceptionCreate() throws Exception {
        TreePruningBookingRequest request = TestRequestBuilder.createTreePruningRequest();

        BindingResult bindingResult = org.mockito.Mockito.mock(BindingResult.class);
        List<FieldError> fieldErrors = new ArrayList<>();
        fieldErrors.add(new FieldError("treePruningRequest", "request.description", null, false, null, null, "must not be null"));
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);
        when(bindingResult.getAllErrors()).thenReturn(new ArrayList<>(fieldErrors));

        Method createRequestMethod = TreePruningController.class.getMethod("createTreePruningBooking", TreePruningBookingRequest.class);
        MethodParameter methodParameter = new MethodParameter(createRequestMethod, 0);

        lenient().when(treePruningController.createTreePruningBooking(any()))
                .thenAnswer(invocation -> {
                    throw new MethodArgumentNotValidException(methodParameter, bindingResult);
                });

        mockMvc.perform(post("/tree-pruning/v1/_create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value(400))
                .andExpect(jsonPath("$.error.message").value("Validation failed: must not be null"));
    }

    /**
     * Test for handling CustomException.
     */
    @Test
    void testHandleCustomException() throws Exception {
        TreePruningBookingRequest request = TestRequestBuilder.createTreePruningRequest();

        lenient().when(treePruningController.createTreePruningBooking(any()))
                .thenThrow(new CustomException("CUSTOM_ERROR", "Custom validation error"));

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
    @Test
    void testHandleHttpClientErrorException() throws Exception {
        TreePruningBookingRequest request = TestRequestBuilder.createTreePruningRequest();

        lenient().when(treePruningController.createTreePruningBooking(any()))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Client error"));

        mockMvc.perform(post("/tree-pruning/v1/_create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value(400))
                .andExpect(jsonPath("$.error.message").value("Client error from external service: 400 BAD_REQUEST"));
    }

    /**
     * Test for handling HttpServerErrorException.
     */
    @Test
    void testHandleHttpServerErrorException() throws Exception {
        TreePruningBookingRequest request = TestRequestBuilder.createTreePruningRequest();

        lenient().when(treePruningController.createTreePruningBooking(any()))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Server error"));

        mockMvc.perform(post("/tree-pruning/v1/_create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error.code").value(500))
                .andExpect(jsonPath("$.error.message").value("Server error from external service: 500 INTERNAL_SERVER_ERROR"));
    }

    /**
     * Test for handling ResourceAccessException.
     */
    @Test
    void testHandleResourceAccessException() throws Exception {
        TreePruningBookingRequest request = TestRequestBuilder.createTreePruningRequest();
        lenient().when(treePruningController.createTreePruningBooking(any()))
                .thenThrow(new ResourceAccessException("Connection timeout"));

        mockMvc.perform(post("/tree-pruning/v1/_create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error.code").value(503))
                .andExpect(jsonPath("$.error.message").value("Service unavailable: Connection timeout"));
    }

    /**
     * Test for handling UnknownHostException.
     */
    @Test
    void testHandleUnknownHostException() throws Exception {
        TreePruningBookingRequest request = TestRequestBuilder.createTreePruningRequest();

        lenient().when(treePruningController.createTreePruningBooking(any()))
                .thenThrow(new RuntimeException(new UnknownHostException("Unknown host")));

        mockMvc.perform(post("/tree-pruning/v1/_create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error.code").value(503))
                .andExpect(jsonPath("$.error.message").value("Service unavailable: Unknown host - Unknown host"));
    }

    /**
     * Test for handling generic Exception.
     */
    @Test
    void testHandleGenericException() throws Exception {
        TreePruningBookingRequest request = TestRequestBuilder.createTreePruningRequest();

        lenient().when(treePruningController.createTreePruningBooking(any()))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(post("/tree-pruning/v1/_create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error.code").value(500))
                .andExpect(jsonPath("$.error.message").value("Internal Server Error: Unexpected error"));
    }
}
