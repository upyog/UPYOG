package org.egov.ndc.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.tracer.model.ServiceCallException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

class ServiceRequestRepositoryTest {

    @Mock
    private ObjectMapper mapper;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ServiceRequestRepository serviceRequestRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testFetchResult_Success() throws Exception {
        StringBuilder uri = new StringBuilder("http://example.com/api");
        Object request = new Object();
        Map<String, String> expectedResponse = new HashMap<>();
        expectedResponse.put("key", "value");

        when(mapper.writeValueAsString(request)).thenReturn("{}");
        when(restTemplate.postForObject(anyString(), any(), eq(Map.class))).thenReturn(expectedResponse);

        Object response = serviceRequestRepository.fetchResult(uri, request);

        assertNotNull(response);
        assertEquals(expectedResponse, response);
        verify(mapper).writeValueAsString(request);
        verify(restTemplate).postForObject(uri.toString(), request, Map.class);
    }

    @Test
    void testFetchResult_ThrowsServiceCallException() throws Exception {
        StringBuilder uri = new StringBuilder("http://example.com/api");
        Object request = new Object();

        HttpClientErrorException httpException =
                new HttpClientErrorException(org.springframework.http.HttpStatus.BAD_REQUEST, "Bad Request");
        when(mapper.writeValueAsString(request)).thenReturn("{}");
        when(restTemplate.postForObject(anyString(), any(), eq(Map.class))).thenThrow(httpException);

        ServiceCallException ex = assertThrows(ServiceCallException.class, () -> {
            serviceRequestRepository.fetchResult(uri, request);
        });
        verify(mapper).writeValueAsString(request);
    }

    @Test
    void testFetchResult_HandlesGenericException() throws Exception {
        StringBuilder uri = new StringBuilder("http://example.com/api");
        Object request = new Object();

        when(mapper.writeValueAsString(request)).thenReturn("{}");
        when(restTemplate.postForObject(anyString(), any(), eq(Map.class))).thenThrow(new RuntimeException("Error"));

        // The method returns null and logs error on generic exception
        Object response = serviceRequestRepository.fetchResult(uri, request);

        assertNull(response);
        verify(mapper).writeValueAsString(request);
        verify(restTemplate).postForObject(uri.toString(), request, Map.class);
    }
}
