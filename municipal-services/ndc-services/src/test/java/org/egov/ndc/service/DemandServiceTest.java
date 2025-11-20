package org.egov.ndc.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.common.contract.request.RequestInfo;
import org.egov.ndc.config.NDCConfiguration;
import org.egov.ndc.repository.ServiceRequestRepository;
import org.egov.ndc.util.NDCConstants;
import org.egov.ndc.web.model.calculator.Calculation;
import org.egov.ndc.web.model.demand.Demand;
import org.egov.ndc.web.model.demand.DemandDetail;
import org.egov.ndc.web.model.demand.DemandRequest;
import org.egov.ndc.web.model.demand.DemandResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DemandServiceTest {

    @InjectMocks
    private DemandService demandService;

    @Mock
    private NDCConfiguration ndcConfiguration;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private ServiceRequestRepository repository;

    @Captor
    private ArgumentCaptor<DemandRequest> demandRequestCaptor;

    @BeforeEach
    void setUp() {
        // if needed
    }

    @Test
    void generateDemands_shouldBuildAndReturnDemands() {
        // given
        RequestInfo requestInfo = new RequestInfo();
        Calculation calculation = new Calculation();
        calculation.setTenantId("pb.amritsar");
        calculation.setApplicationNumber("APP-001");
        calculation.setTotalAmount(1000.0);

        when(ndcConfiguration.getTaxHeadMasterCode()).thenReturn("NDC_TAX_HEAD");
        when(ndcConfiguration.getBillingServiceHost()).thenReturn("http://billing");
        when(ndcConfiguration.getDemandCreateEndpoint()).thenReturn("/demand/_create");

        Demand demandFromBilling = Demand.builder()
                .tenantId("pb.amritsar")
                .consumerCode("APP-001")
                .demandDetails(Collections.singletonList(
                        DemandDetail.builder()
                                .tenantId("pb.amritsar")
                                .taxAmount(BigDecimal.valueOf(1000.0))
                                .taxHeadMasterCode("NDC_TAX_HEAD")
                                .build()
                ))
                .build();

        DemandResponse demandResponse = DemandResponse.builder()
                .demands(Collections.singletonList(demandFromBilling))
                .build();

        when(repository.fetchResult(any(StringBuilder.class), any(DemandRequest.class)))
                .thenReturn(new Object()); // raw object from external call
        when(mapper.convertValue(any(), eq(DemandResponse.class))).thenReturn(demandResponse);

        // when
        List<Demand> result = demandService.generateDemands(requestInfo,
                Collections.singletonList(calculation));

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        Demand demand = result.get(0);
        assertEquals("pb.amritsar", demand.getTenantId());
        assertEquals("APP-001", demand.getConsumerCode());
        assertEquals(1, demand.getDemandDetails().size());
        assertEquals(BigDecimal.valueOf(1000.0), demand.getDemandDetails().get(0).getTaxAmount());
        assertEquals("NDC_TAX_HEAD", demand.getDemandDetails().get(0).getTaxHeadMasterCode());

        // verify URL and payload
        verify(ndcConfiguration).getBillingServiceHost();
        verify(ndcConfiguration).getDemandCreateEndpoint();
        verify(repository).fetchResult(any(StringBuilder.class), demandRequestCaptor.capture());

        DemandRequest captured = demandRequestCaptor.getValue();
        assertSame(requestInfo, captured.getRequestInfo());
        assertEquals(1, captured.getDemands().size());
        assertEquals("APP-001", captured.getDemands().get(0).getConsumerCode());
        assertEquals(NDCConstants.NDC_MODULE, captured.getDemands().get(0).getBusinessService());
    }

    @Test
    void generateDemands_shouldHandleEmptyCalculations() {
        RequestInfo requestInfo = new RequestInfo();

        when(ndcConfiguration.getBillingServiceHost()).thenReturn("http://billing");
        when(ndcConfiguration.getDemandCreateEndpoint()).thenReturn("/demand/_create");

        DemandResponse demandResponse = DemandResponse.builder()
                .demands(Collections.emptyList())
                .build();

        when(repository.fetchResult(any(StringBuilder.class), any(DemandRequest.class)))
                .thenReturn(new Object());
        when(mapper.convertValue(any(), eq(DemandResponse.class))).thenReturn(demandResponse);

        List<Calculation> emptyCalculations = Collections.emptyList();

        List<Demand> result = demandService.generateDemands(requestInfo, emptyCalculations);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository).fetchResult(any(StringBuilder.class), any(DemandRequest.class));
    }
}
