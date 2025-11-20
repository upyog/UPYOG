package org.egov.ndc.service;

import org.egov.common.contract.request.RequestInfo;
import org.egov.ndc.config.NDCConfiguration;
import org.egov.ndc.repository.ServiceRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MDMSServiceTest {

    @InjectMocks
    private MDMSService mdmsService;

    @Mock
    private NDCConfiguration config;

    @Mock
    private ServiceRequestRepository serviceRequestRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testMDMSCall_shouldReturnResultFromRepository() {
        // Arrange
        RequestInfo requestInfo = new RequestInfo();
        String tenantId = "tenant1";

        // Setup config for MDMS host and endpoint
        when(config.getMdmsHost()).thenReturn("http://mdms-host/");
        when(config.getMdmsEndPoint()).thenReturn("mdms-search");

        // Mock repository to return a dummy response object
        Object expectedResponse = new Object();
        when(serviceRequestRepository.fetchResult(any(StringBuilder.class), any())).thenReturn(expectedResponse);

        // Act
        Object result = mdmsService.mDMSCall(requestInfo, tenantId);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResponse, result);

        // Verify url construction and repository call
        ArgumentCaptor<StringBuilder> urlCaptor = ArgumentCaptor.forClass(StringBuilder.class);
        ArgumentCaptor<Object> requestCaptor = ArgumentCaptor.forClass(Object.class);

        verify(serviceRequestRepository, times(1)).fetchResult(urlCaptor.capture(), requestCaptor.capture());

        StringBuilder capturedUrl = urlCaptor.getValue();
        assertTrue(capturedUrl.toString().startsWith("http://mdms-host/"));
        assertTrue(capturedUrl.toString().contains("mdms-search"));
    }
}
