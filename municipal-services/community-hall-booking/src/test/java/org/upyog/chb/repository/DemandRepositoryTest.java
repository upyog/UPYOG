package org.upyog.chb.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.upyog.chb.config.CommunityHallBookingConfiguration;
import org.upyog.chb.web.models.billing.Demand;
import org.upyog.chb.web.models.billing.DemandRequest;
import org.upyog.chb.web.models.billing.DemandResponse;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DemandRepositoryTest {

    @Mock
    private ServiceRequestRepository serviceRequestRepository;

    @Mock
    private CommunityHallBookingConfiguration config;

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private DemandRepository demandRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveDemand() {
        // Arrange
        RequestInfo requestInfo = mock(RequestInfo.class);
        List<Demand> demands = Collections.singletonList(mock(Demand.class));
        String billingHost = "http://localhost:8080";
        String demandCreateEndpoint = "/demand/_create";
        DemandRequest demandRequest = new DemandRequest(requestInfo, demands);
        DemandResponse demandResponse = new DemandResponse();
        demandResponse.setDemands(demands);

        when(config.getBillingHost()).thenReturn(billingHost);
        when(config.getDemandCreateEndpoint()).thenReturn(demandCreateEndpoint);
        when(serviceRequestRepository.fetchResult(any(), eq(demandRequest))).thenReturn(new Object());
        when(mapper.convertValue(any(), eq(DemandResponse.class))).thenReturn(demandResponse);

        // Act
        List<Demand> result = demandRepository.saveDemand(requestInfo, demands);

        // Assert
        assertNotNull(result);
        assertEquals(demands, result);
        verify(serviceRequestRepository, times(1)).fetchResult(any(), eq(demandRequest));
        verify(mapper, times(1)).convertValue(any(), eq(DemandResponse.class));
    }

    @Test
    void testUpdateDemand() {
        // Arrange
        RequestInfo requestInfo = mock(RequestInfo.class);
        List<Demand> demands = Collections.singletonList(mock(Demand.class));
        String billingHost = "http://localhost:8080";
        String demandUpdateEndpoint = "/demand/_update";
        DemandRequest demandRequest = new DemandRequest(requestInfo, demands);
        DemandResponse demandResponse = new DemandResponse();
        demandResponse.setDemands(demands);

        when(config.getBillingHost()).thenReturn(billingHost);
        when(config.getDemandUpdateEndpoint()).thenReturn(demandUpdateEndpoint);
        when(serviceRequestRepository.fetchResult(any(), eq(demandRequest))).thenReturn(new Object());
        when(mapper.convertValue(any(), eq(DemandResponse.class))).thenReturn(demandResponse);

        // Act
        List<Demand> result = demandRepository.updateDemand(requestInfo, demands);

        // Assert
        assertNotNull(result);
        assertEquals(demands, result);
        verify(serviceRequestRepository, times(1)).fetchResult(any(), eq(demandRequest));
        verify(mapper, times(1)).convertValue(any(), eq(DemandResponse.class));
    }

    @Test
    void testSaveDemandParsingError() {
        // Arrange
        RequestInfo requestInfo = mock(RequestInfo.class);
        List<Demand> demands = Collections.singletonList(mock(Demand.class));
        String billingHost = "http://localhost:8080";
        String demandCreateEndpoint = "/demand/_create";
        DemandRequest demandRequest = new DemandRequest(requestInfo, demands);

        when(config.getBillingHost()).thenReturn(billingHost);
        when(config.getDemandCreateEndpoint()).thenReturn(demandCreateEndpoint);
        when(serviceRequestRepository.fetchResult(any(), eq(demandRequest))).thenReturn(new Object());
        when(mapper.convertValue(any(), eq(DemandResponse.class))).thenThrow(IllegalArgumentException.class);

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> demandRepository.saveDemand(requestInfo, demands));
        assertEquals("PARSING ERROR", exception.getCode());
        assertEquals("Failed to parse response of create demand", exception.getMessage());
    }
}