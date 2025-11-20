package org.egov.ndc.repository;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.egov.ndc.config.NDCConfiguration;
import org.egov.ndc.repository.IdGenRepository;
import org.egov.ndc.web.model.idgen.IdGenerationRequest;
import org.egov.ndc.web.model.idgen.IdGenerationResponse;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.egov.tracer.model.ServiceCallException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

class IdGenRepositoryTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private NDCConfiguration config;

    @InjectMocks
    private IdGenRepository idGenRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
        when(config.getIdGenHost()).thenReturn("http://localhost");
        when(config.getIdGenPath()).thenReturn("/idgen");
    }

    @Test
    void testGetId_Success() {
        IdGenerationResponse mockResponse = new IdGenerationResponse();
        when(restTemplate.postForObject(anyString(), any(IdGenerationRequest.class), eq(IdGenerationResponse.class)))
                .thenReturn(mockResponse);

        RequestInfo requestInfo = new RequestInfo();
        IdGenerationResponse response = idGenRepository.getId(requestInfo, "tenant1", "name1", 1);

        assertNotNull(response);
        verify(restTemplate).postForObject(anyString(), any(IdGenerationRequest.class), eq(IdGenerationResponse.class));
    }

    @Test
    void testGetId_ThrowsServiceCallException() {

        HttpClientErrorException httpException = new HttpClientErrorException(
                HttpStatus.BAD_REQUEST, "Bad Request", null, "Bad Request".getBytes(), null);
        when(restTemplate.postForObject(anyString(), any(IdGenerationRequest.class), eq(IdGenerationResponse.class)))
                .thenThrow(httpException);
        RequestInfo requestInfo = new RequestInfo();
        ServiceCallException exception = assertThrows(ServiceCallException.class, () -> {
            idGenRepository.getId(requestInfo, "tenant1", "name1", 1);
        });
        assertNotNull(exception.getError());
        assertTrue(exception.getError().contains("Bad Request"));

    }

    @Test
    void testGetId_ThrowsCustomException() {
        when(restTemplate.postForObject(anyString(), any(IdGenerationRequest.class), eq(IdGenerationResponse.class)))
                .thenThrow(new RuntimeException("Error", new Throwable("Cause")));
        RequestInfo requestInfo = new RequestInfo();
        CustomException exception = assertThrows(CustomException.class, () -> {
            idGenRepository.getId(requestInfo, "tenant1", "name1", 1);
        });
        assertTrue(exception.getMessage().contains("Error"));
    }
}