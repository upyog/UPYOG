package org.egov.ndc.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.ndc.web.model.calculator.Calculation;
import org.egov.ndc.web.model.calculator.CalculationCriteria;
import org.egov.ndc.web.model.calculator.CalculationReq;
import org.egov.common.contract.request.RequestInfo;
import org.egov.ndc.config.ResponseInfoFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CalculationServiceTest {

    @InjectMocks
    private CalculationService calculationService;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private MDMSService mdmsService;

    @Mock
    private ResponseInfoFactory responseInfoFactory;

    @Mock
    private DemandService demandService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testCalculate_success_singleCriteria() throws Exception {
        // given
        RequestInfo requestInfo = new RequestInfo();
        CalculationCriteria criteria = new CalculationCriteria();
        criteria.setApplicationNumber("APP-1");
        criteria.setTenantId("pb.amritsar");

        CalculationReq req = new CalculationReq();
        req.setRequestInfo(requestInfo);
        req.setCalculationCriteria(Collections.singletonList(criteria));

        // mdms raw object
        Object mdmsRaw = new HashMap<>();
        when(mdmsService.mDMSCall(requestInfo, "pb.amritsar")).thenReturn(mdmsRaw);

        // JSON produced by ObjectMapper
        String mdmsJson = "{\"MdmsRes\":{\"ndc\":{\"NdcFee\":[{\"flatFee\":250.0}]}}}";
        when(mapper.writeValueAsString(mdmsRaw)).thenReturn(mdmsJson);

        // when
        List<Calculation> result = calculationService.calculate(req);

        // then
        assertEquals(1, result.size());
        Calculation calc = result.get(0);
        assertEquals("APP-1", calc.getApplicationNumber());
        assertEquals("pb.amritsar", calc.getTenantId());
        assertEquals(250.0, calc.getTotalAmount(), 0.001);
        verify(demandService, times(1))
                .generateDemands(requestInfo, result);
    }

    @Test
    void testGetCalculations_multipleCriteria_sameFlatFee() throws Exception {
        // given
        RequestInfo requestInfo = new RequestInfo();

        CalculationCriteria c1 = new CalculationCriteria();
        c1.setApplicationNumber("APP-1");
        c1.setTenantId("pb.amritsar");

        CalculationCriteria c2 = new CalculationCriteria();
        c2.setApplicationNumber("APP-2");
        c2.setTenantId("pb.amritsar");

        CalculationReq req = new CalculationReq();
        req.setRequestInfo(requestInfo);
        req.setCalculationCriteria(Arrays.asList(c1, c2));

        Object mdmsRaw = new HashMap<>();
        when(mdmsService.mDMSCall(requestInfo, "pb.amritsar")).thenReturn(mdmsRaw);
        String mdmsJson = "{\"MdmsRes\":{\"ndc\":{\"NdcFee\":[{\"flatFee\":500.0}]}}}";
        when(mapper.writeValueAsString(mdmsRaw)).thenReturn(mdmsJson);

        // when
        List<Calculation> calculations = calculationService.getCalculations(req);

        // then
        assertEquals(2, calculations.size());
        assertEquals(500.0, calculations.get(0).getTotalAmount(), 0.001);
        assertEquals(500.0, calculations.get(1).getTotalAmount(), 0.001);
    }
}
