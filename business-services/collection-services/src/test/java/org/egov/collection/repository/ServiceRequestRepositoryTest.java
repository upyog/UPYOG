package org.egov.collection.repository;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

import org.egov.tracer.model.ServiceCallException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@ContextConfiguration(classes = {ServiceRequestRepository.class})
@ExtendWith(SpringExtension.class)
class ServiceRequestRepositoryTest {
    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Test
    void testFetchResult() throws RestClientException {
        HashMap<Object, Object> objectObjectMap = new HashMap<>();
        when(this.restTemplate.postForObject(anyString(), any(), eq(Map.class))).thenReturn(objectObjectMap);
        Object actualFetchResultResult = this.serviceRequestRepository.fetchResult(new StringBuilder("Str"), "Request");
        assertSame(objectObjectMap, actualFetchResultResult);
        assertTrue(((Map<Object, Object>) actualFetchResultResult).isEmpty());
        verify(this.restTemplate).postForObject(anyString(), any(), eq(Map.class));
    }

    @Test
    void testFetchGetResult() throws RestClientException {
        when(this.restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.CONTINUE));
        assertNull(this.serviceRequestRepository.fetchGetResult("Uri"));
        verify(this.restTemplate).exchange(anyString(), eq(HttpMethod.GET), isNull(), eq(String.class));
    }

    @Test
    void testFetchGetResult2() throws RestClientException {
        when(this.restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), eq(String.class))).thenReturn(null);
        assertThrows(ServiceCallException.class, () -> this.serviceRequestRepository.fetchGetResult("Uri"));
        verify(this.restTemplate).exchange(anyString(), eq(HttpMethod.GET), isNull(), eq(String.class));
    }

    @Test
    void testFetchGetResult3() throws RestClientException {
        when(this.restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.CONTINUE));
        assertThrows(ServiceCallException.class, () -> this.serviceRequestRepository.fetchGetResult("Uri"));
        verify(this.restTemplate).exchange(anyString(), eq(HttpMethod.GET), isNull(), eq(String.class));
    }

    @Test
    void testFetchGetResult4() throws RestClientException {
        when(this.restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), eq(String.class)))
                .thenThrow(new ServiceCallException("An error occurred"));
        assertThrows(ServiceCallException.class, () -> this.serviceRequestRepository.fetchGetResult("Uri"));
        verify(this.restTemplate).exchange(anyString(), eq(HttpMethod.GET), isNull(), eq(String.class));
    }

    @Test
    void testFetchGetResult5() throws RestClientException {
        // Instead of returning a ResponseEntity, throw an exception to trigger ServiceCallException
        when(this.restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), eq(String.class)))
            .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThrows(ServiceCallException.class, () -> this.serviceRequestRepository.fetchGetResult("Uri"));
        verify(this.restTemplate).exchange(anyString(), eq(HttpMethod.GET), isNull(), eq(String.class));
    }
}

