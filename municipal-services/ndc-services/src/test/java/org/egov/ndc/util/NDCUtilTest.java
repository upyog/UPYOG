
package org.egov.ndc.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.*;
import org.egov.ndc.config.NDCConfiguration;
import org.egov.ndc.repository.ServiceRequestRepository;
import org.egov.ndc.util.NDCConstants;
import org.egov.ndc.web.model.idgen.IdGenerationRequest;
import org.egov.ndc.web.model.idgen.IdGenerationResponse;
import org.egov.ndc.web.model.idgen.IdResponse;
import org.egov.ndc.workflow.WorkflowService;
import org.egov.tracer.model.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class NDCUtilTest {

    @InjectMocks
    private NDCUtil ndcUtil;

    @Mock
    private NDCConfiguration config;

    @Mock
    private ServiceRequestRepository serviceRequestRepository;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private ObjectMapper mapper;

    @BeforeEach
    void setup() throws IllegalAccessException, NoSuchFieldException {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(ndcUtil, "mapper", mapper);
        Field idGenHostField = NDCUtil.class.getDeclaredField("idGenHost");
        idGenHostField.setAccessible(true);
        idGenHostField.set(ndcUtil, "http://idgen-host/");

        Field idGenPathField = NDCUtil.class.getDeclaredField("idGenPath");
        idGenPathField.setAccessible(true);
        idGenPathField.set(ndcUtil, "/idgen-path");
    }

    @Test
    void getMdmsSearchUrl_shouldConstructUrl() {
        when(config.getMdmsHost()).thenReturn("http://mdms-host/");
        when(config.getMdmsEndPoint()).thenReturn("/mdms-search");

        StringBuilder url = ndcUtil.getMdmsSearchUrl();

        assertNotNull(url);
        assertTrue(url.toString().startsWith("http://mdms-host/"));
        assertTrue(url.toString().contains("mdms-search"));
    }

    @Test
    void getMDMSRequest_shouldBuildMdmsCriteriaReq() {
        RequestInfo requestInfo = new RequestInfo();

        // Spy to stub getNDCModuleRequest for controlled test
        NDCUtil spyUtil = spy(ndcUtil);

        List<ModuleDetail> dummyModuleDetails = Collections.singletonList(ModuleDetail.builder().moduleName("dummy").build());
        doReturn(dummyModuleDetails).when(spyUtil).getNDCModuleRequest();

        MdmsCriteriaReq req = spyUtil.getMDMSRequest(requestInfo, "tenant1");

        assertNotNull(req);
        assertEquals(requestInfo, req.getRequestInfo());
        assertNotNull(req.getMdmsCriteria());
        assertEquals("tenant1", req.getMdmsCriteria().getTenantId());
        assertEquals(dummyModuleDetails, req.getMdmsCriteria().getModuleDetails());
    }

    @Test
    void getNDCModuleRequest_shouldReturnModulesWithMasterDetails() {
        List<ModuleDetail> moduleDetails = ndcUtil.getNDCModuleRequest();

        assertNotNull(moduleDetails);
        assertEquals(2, moduleDetails.size());

        ModuleDetail ndcModule = moduleDetails.stream()
                .filter(md -> NDCConstants.NDC_MODULE.equals(md.getModuleName()))
                .findFirst().orElse(null);
        assertNotNull(ndcModule);
        assertFalse(ndcModule.getMasterDetails().isEmpty());

        ModuleDetail commonMastersModule = moduleDetails.stream()
                .filter(md -> NDCConstants.COMMON_MASTERS_MODULE.equals(md.getModuleName()))
                .findFirst().orElse(null);
        assertNotNull(commonMastersModule);
    }

    @Test
    void getIdList_shouldReturnIds_whenSuccess() throws Exception {
        RequestInfo requestInfo = new RequestInfo();

        Object rawFetchResult = new Object();
        when(serviceRequestRepository.fetchResult(any(StringBuilder.class), any())).thenReturn(rawFetchResult);

        IdResponse idResponseMock = new IdResponse();
        idResponseMock.setId("ID_001");
        IdGenerationResponse idGenerationResponseMock = new IdGenerationResponse();
        idGenerationResponseMock.setIdResponses(Collections.singletonList(idResponseMock));

        when(mapper.convertValue(eq(rawFetchResult), eq(IdGenerationResponse.class))).thenReturn(idGenerationResponseMock);

        List<String> ids = ndcUtil.getIdList(requestInfo, "tenant1", "idName", "idFormat", 1);

        assertNotNull(ids);
        assertEquals(1, ids.size());
        assertEquals("ID_001", ids.get(0));
    }


    @Test
    void getIdList_shouldThrowException_whenEmptyIdResponses() throws Exception {
        RequestInfo requestInfo = new RequestInfo();

        IdGenerationResponse idGenResp = new IdGenerationResponse();
        idGenResp.setIdResponses(Collections.emptyList());

        when(serviceRequestRepository.fetchResult(any(StringBuilder.class), any())).thenReturn(new Object());
        when(mapper.convertValue(any(), eq(IdGenerationResponse.class))).thenReturn(idGenResp);

        CustomException ex = assertThrows(CustomException.class, () ->
                ndcUtil.getIdList(requestInfo, "tenant1", "idName", "idFormat", 1));
        assertEquals("IDGEN ERROR", ex.getCode());
    }
}
