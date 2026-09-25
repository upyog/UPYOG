package org.upyog.chb.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.tracer.model.ServiceCallException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServiceRequestRepositoryTest {

    @Mock
    private ObjectMapper mapper;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ServiceRequestRepository serviceRequestRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFetchResultSuccess() {
        // Arrange
        StringBuilder uri = new StringBuilder("http://localhost:8080/test");
        Object request = new Object();
        Map<String, Object> mockResponse = Map.of("key", "value");

        when(restTemplate.postForObject(uri.toString(), request, Map.class)).thenReturn(mockResponse);

        // Act
        Object result = serviceRequestRepository.fetchResult(uri, request);

        // Assert
        assertNotNull(result);
        assertEquals(mockResponse, result);
        verify(restTemplate, times(1)).postForObject(uri.toString(), request, Map.class);
    }

    @Test
    void testFetchResultHttpClientErrorException() {
        // Arrange
        StringBuilder uri = new StringBuilder("http://localhost:8080/test");
        Object request = new Object();
        HttpClientErrorException exception = mock(HttpClientErrorException.class);
        when(exception.getResponseBodyAsString()).thenReturn("Error_response");
        when(exception.getMessage()).thenReturn("Error_response"); // Ensure the message is set
        when(restTemplate.postForObject(uri.toString(), request, Map.class)).thenThrow(exception);

        // Act & Assert
        ServiceCallException thrown = assertThrows(ServiceCallException.class, () -> serviceRequestRepository.fetchResult(uri, request));
        assertEquals("Error_response", thrown.getError());
        verify(restTemplate, times(1)).postForObject(uri.toString(), request, Map.class);
    }

    @Test
    void testFetchResultGenericException() {
        // Arrange
        StringBuilder uri = new StringBuilder("http://localhost:8080/test");
        Object request = new Object();
        when(restTemplate.postForObject(uri.toString(), request, Map.class)).thenThrow(RuntimeException.class);

        // Act
        Object result = serviceRequestRepository.fetchResult(uri, request);

        // Assert
        assertNull(result);
        verify(restTemplate, times(1)).postForObject(uri.toString(), request, Map.class);
    }

    @Test
    void testGetShorteningURLSuccess() {
        // Arrange
        StringBuilder uri = new StringBuilder("http://localhost:8080/shorten");
        Object request = new Object();
        String mockResponse = "http://short.url";

        when(restTemplate.postForObject(uri.toString(), request, String.class)).thenReturn(mockResponse);

        // Act
        String result = serviceRequestRepository.getShorteningURL(uri, request);

        // Assert
        assertNotNull(result);
        assertEquals(mockResponse, result);
        verify(restTemplate, times(1)).postForObject(uri.toString(), request, String.class);
    }

    @Test
    void testGetShorteningURLHttpClientErrorException() {
        // Arrange
        StringBuilder uri = new StringBuilder("http://localhost:8080/shorten");
        Object request = new Object();
        HttpClientErrorException exception = mock(HttpClientErrorException.class);
        when(exception.getResponseBodyAsString()).thenReturn("Error response");
        when(restTemplate.postForObject(uri.toString(), request, String.class)).thenThrow(exception);

        // Act & Assert
        ServiceCallException thrown = assertThrows(ServiceCallException.class, () -> serviceRequestRepository.getShorteningURL(uri, request));
        assertEquals("Error response", thrown.getError());
        verify(restTemplate, times(1)).postForObject(uri.toString(), request, String.class);
    }

    @Test
    void testGetShorteningURLGenericException() {
        // Arrange
        StringBuilder uri = new StringBuilder("http://localhost:8080/shorten");
        Object request = new Object();
        when(restTemplate.postForObject(uri.toString(), request, String.class)).thenThrow(RuntimeException.class);

        // Act
        String result = serviceRequestRepository.getShorteningURL(uri, request);

        // Assert
        assertNull(result);
        verify(restTemplate, times(1)).postForObject(uri.toString(), request, String.class);
    }
}