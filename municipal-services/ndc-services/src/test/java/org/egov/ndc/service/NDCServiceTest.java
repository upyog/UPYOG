package org.egov.ndc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.ndc.config.NDCConfiguration;
import org.egov.ndc.producer.Producer;
import org.egov.ndc.repository.NDCRepository;
import org.egov.ndc.repository.ServiceRequestRepository;
import org.egov.ndc.util.NDCConstants;
import org.egov.ndc.util.NDCUtil;
import org.egov.ndc.web.model.AuditDetails;
import org.egov.ndc.web.model.OwnerInfo;
import org.egov.ndc.web.model.UserResponse;
import org.egov.ndc.web.model.Workflow;
import org.egov.ndc.web.model.calculator.CalculationRes;
import org.egov.ndc.web.model.ndc.*;
import org.egov.ndc.workflow.WorkflowIntegrator;
import org.egov.tracer.model.CustomException;
import org.egov.tracer.model.ServiceCallException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NDCServiceTest {

    @InjectMocks
    private NDCService ndcService;

    @Mock
    private NDCUtil ndcUtil;

    @Mock
    private UserService userService;

    @Mock
    private CalculationService calculationService;

    @Mock
    private WorkflowIntegrator workflowIntegrator;

    @Mock
    private NDCConfiguration config;
    @Mock
    private NDCConfiguration ndcConfiguration;

    @Mock
    private NDCRepository ndcRepository;

    @Mock
    private ServiceRequestRepository serviceRequestRepository;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private Producer producer;

    @Mock
    private EnrichmentService enrichmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        when(config.getSaveTopic()).thenReturn("save-topic");
        when(ndcConfiguration.getSaveTopic()).thenReturn("save-topic");
        doNothing().when(producer).push(anyString(), any());
    }

    @Test
    void createNdcApplication_shouldSetIdsAndCallWorkflowAndProducer() {

        RequestInfo requestInfo = getRequestInfo();

        Application application = new Application();
        application.setTenantId("tenant1");
        application.setDocuments(Collections.emptyList());
        application.setNdcDetails(Collections.emptyList());

        NdcApplicationRequest ndcApplicationRequest = NdcApplicationRequest.builder()
                .requestInfo(requestInfo)
                .applications(Collections.singletonList(application))
                .build();
        when(ndcUtil.getIdList(any(), eq("tenant1"), eq("ndc.applicationid"), anyString(), eq(1)))
                .thenReturn(Collections.singletonList("NDC-001"));
        NdcApplicationRequest response = ndcService.createNdcApplication(false, ndcApplicationRequest);

        assertNotNull(response);
        assertEquals(1, response.getApplications().size());
        assertNotNull(response.getApplications().get(0).getUuid());
        assertEquals("NDC-001", response.getApplications().get(0).getApplicationNo());
        assertTrue(response.getApplications().get(0).getActive());

        verify(userService).createUser(any(), any());
        verify(workflowIntegrator).callWorkFlow(any(), eq(NDCConstants.NDC_BUSINESS_SERVICE));
        verify(producer).push(eq("save-topic"), any());
    }

    @Test
    void updateNdcApplication_shouldThrowException_whenApplicationsEmpty() {
        RequestInfo requestInfo = getRequestInfo();
        NdcApplicationRequest request = NdcApplicationRequest.builder()
                .requestInfo(requestInfo)
                .applications(Collections.emptyList())
                .build();

        CustomException exception = assertThrows(CustomException.class, () -> {
            ndcService.updateNdcApplication(false, request);
        });

        assertEquals("APPLICATIONS_EMPTY", exception.getCode());
    }

    @Test
    void searchNdcApplications_shouldReturnEmptyList_ifUserResponseEmpty() throws JsonProcessingException {
        NdcApplicationSearchCriteria criteria = new NdcApplicationSearchCriteria();
        criteria.setTenantId("tenant1");
        criteria.setMobileNumber("1234567890");
        RequestInfo requestInfo = new RequestInfo();
        String jsonString = "{ \"user\": [  ] }";
        ObjectMapper mapper = new ObjectMapper();
        UserResponse emptyUserResponse = mapper.readValue(jsonString, UserResponse.class);
        when(userService.getUser(any(), any())).thenReturn(emptyUserResponse);
        List<Application> applications = ndcService.searchNdcApplications(criteria, requestInfo);

        assertNotNull(applications);
        assertTrue(applications.isEmpty());
        verify(userService).getUser(any(), any());
    }

    @Test
    void createNdcApplication_setsActiveTrue_whenNullAndSkipsWorkflow() {
        Application app = new Application();
        app.setTenantId("tenant1");
        app.setActive(null);
        app.setNdcDetails(Collections.emptyList());
        app.setDocuments(Collections.emptyList());
        NdcApplicationRequest request = NdcApplicationRequest.builder()
                .requestInfo(getRequestInfo())
                .applications(Arrays.asList(app))
                .build();
        when(ndcUtil.getIdList(any(), anyString(), anyString(), anyString(), anyInt()))
                .thenReturn(Arrays.asList("NDC-001"));
        when(config.getSaveTopic()).thenReturn("save-topic");
        doNothing().when(producer).push(anyString(), any());
        NdcApplicationRequest result = ndcService.createNdcApplication(true, request);
        assertTrue(result.getApplications().get(0).getActive());
        verifyNoInteractions(workflowIntegrator);
        verify(producer).push("save-topic", request);
    }

    @Test
    void createNdcApplication_throwsErrorWhenDocumentUuidNull() {
        Application app = new Application();
        app.setTenantId("tenant1");
        DocumentRequest doc = new DocumentRequest();
        doc.setDocumentAttachment("valid-attachment");
        doc.setUuid(null); // UUID null should cause exception
        app.setDocuments(Arrays.asList(doc));
        NdcApplicationRequest request = NdcApplicationRequest.builder()
                .requestInfo(getRequestInfo())
                .applications(Arrays.asList(app))
                .build();
        when(ndcUtil.getIdList(any(), anyString(), anyString(), anyString(), anyInt()))
                .thenReturn(Arrays.asList("NDC-001"));
        CustomException ex = assertThrows(CustomException.class, () ->
                ndcService.createNdcApplication(true, request));
        assertEquals("DOCUMENT_UUID_NULL", ex.getCode());
    }

    @Test
    void updateNdcApplication_throwsExceptionWhenUuidNotFound() {
        Workflow workflow = new Workflow();
        workflow.setAction("APPLY");
        Application app = new Application();
        app.setWorkflow(workflow);
        app.setUuid("non-existent-uuid");
        app.setAuditDetails(new AuditDetails());
        app.setNdcDetails(Collections.emptyList());
        app.setDocuments(Collections.emptyList());

        NdcApplicationRequest request = NdcApplicationRequest.builder()
                .requestInfo(getRequestInfo())
                .applications(Arrays.asList(app))
                .build();

        when(ndcRepository.checkApplicationExists("non-existent-uuid")).thenReturn(false);

        CustomException ex = assertThrows(CustomException.class, () ->
                ndcService.updateNdcApplication(true, request));
        assertEquals("APPLICANT_NOT_FOUND", ex.getCode());
    }

    @Test
    void updateNdcApplication_callsGetCalculationWhenWorkflowActionApply() {
        Workflow workflow = new Workflow();
        workflow.setAction("APPLY");
        Application app = new Application();
        app.setWorkflow(workflow);
        app.setUuid("exists-uuid");
        RequestInfo requestInfo = getRequestInfo();
        AuditDetails auditDetails = new AuditDetails();
        app.setAuditDetails(auditDetails);
        app.setNdcDetails(Arrays.asList(ndcDetailWithBusinessServiceWithFeeType()));
        app.setDocuments(Collections.emptyList());

        NdcApplicationRequest req = NdcApplicationRequest.builder()
                .requestInfo(requestInfo)
                .applications(Arrays.asList(app))
                .build();

        when(ndcRepository.checkApplicationExists("exists-uuid")).thenReturn(true);
        when(ndcRepository.getExistingUuids(anyString(), anyList())).thenReturn(new HashSet<>());
        when(config.getUpdateTopic()).thenReturn("update-topic");
        doNothing().when(producer).push(anyString(), any());
        when(serviceRequestRepository.fetchResult(any(), any())).thenReturn(new Object());
        when(mapper.convertValue(any(), eq(CalculationRes.class))).thenReturn(new CalculationRes());

        ndcService.updateNdcApplication(false, req);

        verify(producer).push("update-topic", req);
    }

    @Test
    void updateNdcApplication_callsGetCalculationWhenWorkflowActionApply_propertyType_COMMERCIAL() throws JsonProcessingException {
        Workflow workflow = new Workflow();
        workflow.setAction("APPLY");
        Application app = new Application();
        app.setWorkflow(workflow);
        app.setUuid("exists-uuid");
        RequestInfo requestInfo = getRequestInfo();
        AuditDetails auditDetails = new AuditDetails();
        app.setAuditDetails(auditDetails);
        NdcDetailsRequest detail = new NdcDetailsRequest();
        detail.setBusinessService(NDCConstants.PROPERTY_BUSINESSSERVICE);
        ObjectMapper mapper1 = new ObjectMapper();
        detail.setAdditionalDetails(mapper1.readTree("{\"propertyType\":\"COMMERCIAL\"}"));
        app.setNdcDetails(Arrays.asList(detail));
        app.setDocuments(Collections.emptyList());

        NdcApplicationRequest req = NdcApplicationRequest.builder()
                .requestInfo(requestInfo)
                .applications(Arrays.asList(app))
                .build();

        when(ndcRepository.checkApplicationExists("exists-uuid")).thenReturn(true);
        when(ndcRepository.getExistingUuids(anyString(), anyList())).thenReturn(new HashSet<>());
        when(config.getUpdateTopic()).thenReturn("update-topic");
        doNothing().when(producer).push(anyString(), any());
        when(serviceRequestRepository.fetchResult(any(), any())).thenReturn(new Object());
        when(mapper.convertValue(any(), eq(CalculationRes.class))).thenReturn(new CalculationRes());

        ndcService.updateNdcApplication(false, req);

        verify(producer).push("update-topic", req);
    }

    @Test
    void updateNdcApplication_callsGetCalculationWhenWorkflowActionApply_propertyTypeMissing() throws JsonProcessingException {
        Workflow workflow = new Workflow();
        workflow.setAction("APPLY");
        Application app = new Application();
        app.setWorkflow(workflow);
        app.setUuid("exists-uuid");
        RequestInfo requestInfo = getRequestInfo();
        AuditDetails auditDetails = new AuditDetails();
        app.setAuditDetails(auditDetails);
        NdcDetailsRequest detail = new NdcDetailsRequest();
        detail.setBusinessService(NDCConstants.PROPERTY_BUSINESSSERVICE);
        app.setDocuments(Collections.emptyList());
        ObjectMapper mapper = new ObjectMapper();
        detail.setAdditionalDetails(mapper.readTree("{\"name\":\"ABC\"}"));
        app.setNdcDetails(Arrays.asList(detail));
            NdcApplicationRequest req = NdcApplicationRequest.builder()
                .requestInfo(requestInfo)
                .applications(Arrays.asList(app))
                .build();

        when(ndcRepository.checkApplicationExists("exists-uuid")).thenReturn(true);
        when(ndcRepository.getExistingUuids(anyString(), anyList())).thenReturn(new HashSet<>());
        when(config.getUpdateTopic()).thenReturn("update-topic");
        doNothing().when(producer).push(anyString(), any());
        when(serviceRequestRepository.fetchResult(any(), any())).thenReturn(new Object());
        assertThrows(CustomException.class, () -> {
            ndcService.updateNdcApplication(false, req);
        });
    }


    private RequestInfo getRequestInfo() {
        RequestInfo requestInfo = new RequestInfo();
        User userInfo = new User();
        userInfo.setUuid("user-uuid");
        requestInfo.setUserInfo(userInfo);
        return requestInfo;
    }

    private NdcDetailsRequest ndcDetailWithBusinessServiceWithFeeType() {
        NdcDetailsRequest detail = new NdcDetailsRequest();
        detail.setBusinessService(NDCConstants.PROPERTY_BUSINESSSERVICE);

        ObjectMapper mapper = new ObjectMapper();
        try {
            detail.setAdditionalDetails(mapper.readTree("{\"propertyType\":\"RESIDENTIAL\"}"));
        } catch (Exception e) {
        }
        return detail;
    }
    @Test
    void updateNdcApplication_throwsException_whenDocumentUuidNull() {
        // Arrange
        Application app = new Application();
        app.setUuid("app-uuid");
        app.setAuditDetails(new AuditDetails());
        app.setWorkflow(new Workflow());

        // Document with null UUID triggers exception
        DocumentRequest docWithNullUuid = new DocumentRequest();
        docWithNullUuid.setUuid(null);

        app.setDocuments(Collections.singletonList(docWithNullUuid));

        NdcApplicationRequest request = NdcApplicationRequest.builder()
                .requestInfo(getRequestInfo())
                .applications(Collections.singletonList(app))
                .build();

        // Mock repository for application existence
        when(ndcRepository.checkApplicationExists("app-uuid")).thenReturn(true);

        // Act & Assert
        CustomException ex = assertThrows(CustomException.class, () ->
                ndcService.updateNdcApplication(true, request));
        assertEquals("DOCUMENT_ID_ERR", ex.getCode());
    }

    @Test
    void updateNdcApplication_processesDocument_whenUuidNotInExistingSet() {
        // Arrange
        Application app = new Application();
        app.setUuid("app-uuid");
        app.setAuditDetails(new AuditDetails());
        Workflow workflow = new Workflow();
        workflow.setAction("UPDATE");
        app.setWorkflow(workflow);

        DocumentRequest doc1 = new DocumentRequest();
        doc1.setUuid("doc-uuid-1");

        DocumentRequest doc2 = new DocumentRequest();
        doc2.setUuid("doc-uuid-2");

        app.setDocuments(Arrays.asList(doc1, doc2));

        NdcApplicationRequest request = NdcApplicationRequest.builder()
                .requestInfo(getRequestInfo())
                .applications(Collections.singletonList(app))
                .build();

        when(ndcRepository.checkApplicationExists("app-uuid")).thenReturn(true);

        // Simulate only doc-uuid-2 exists, so doc-uuid-1 triggers the setNested block
        Set<String> existingDocUuids = new HashSet<>();
        existingDocUuids.add("doc-uuid-2");

        when(ndcRepository.getExistingUuids(eq("eg_ndc_documents"), anyList())).thenReturn(existingDocUuids);

        // Capture current time to verify timestamps
        long beforeCall = System.currentTimeMillis();

        // Act
        ndcService.updateNdcApplication(true, request);

        // Assert
        // doc1 should have been updated since its UUID was not in existingDocUuids
        assertEquals("doc-uuid-1", doc1.getUuid());
        assertEquals("app-uuid", doc1.getApplicationId());
        assertEquals("user-uuid", doc1.getCreatedby());
        assertTrue(doc1.getCreatedtime() >= beforeCall);

        // Both docs should have last modified set
        assertEquals("user-uuid", doc1.getLastmodifiedby());
        assertTrue(doc1.getLastmodifiedtime() >= beforeCall);

        assertEquals("user-uuid", doc2.getLastmodifiedby());
        assertTrue(doc2.getLastmodifiedtime() >= beforeCall);
    }

    @Test
    void getApplicationsWithOwnerInfo_shouldReturnEmptyList_whenRepositoryReturnsEmpty() {
        NdcApplicationSearchCriteria criteria = new NdcApplicationSearchCriteria();
        RequestInfo requestInfo = mock(RequestInfo.class);

        when(ndcRepository.fetchNdcApplications(criteria)).thenReturn(Collections.emptyList());

        List<Application> result = ndcService.getApplicationsWithOwnerInfo(criteria, requestInfo);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(enrichmentService, never()).enrichApplicationCriteriaWithOwnerids(any(), any());
        verify(userService, never()).getUser(any(), any());
        verify(enrichmentService, never()).enrichOwner(any(), any());
    }

    @Test
    void getApplicationsWithOwnerInfo_shouldInvokeEnrichments_whenRepositoryReturnsApplications() throws JsonProcessingException {
        // Setup applications list with one application and one owner
        OwnerInfo ownerInfo = new OwnerInfo();
        ownerInfo.setUuid("owner-uuid");

        Application application = new Application();
        application.setOwners(Collections.singletonList(ownerInfo));

        List<Application> applicationsList = Collections.singletonList(application);

        NdcApplicationSearchCriteria criteria = new NdcApplicationSearchCriteria();
        RequestInfo requestInfo = mock(RequestInfo.class);
        // Assume user detail list contains enriched owner info with the same uuid
        OwnerInfo enrichedOwner = new OwnerInfo();
        enrichedOwner.setUuid("owner-uuid");
        enrichedOwner.setName("John Doe");
        String jsonString = "{ \"user\": [ { \"uuid\": \"owner1\", \"name\": \"John Doe\" }, { \"uuid\": \"owner2\", \"name\": \"Jane Smith\" } ] }";

        ObjectMapper mapper = new ObjectMapper();
        UserResponse userResponse = mapper.readValue(jsonString, UserResponse.class);

        when(ndcRepository.fetchNdcApplications(criteria)).thenReturn(applicationsList);
        doNothing().when(enrichmentService).enrichApplicationCriteriaWithOwnerids(criteria, applicationsList);
        when(userService.getUser(criteria, requestInfo)).thenReturn(userResponse);
        doNothing().when(enrichmentService).enrichOwner(userResponse, applicationsList);

        List<Application> result = ndcService.getApplicationsWithOwnerInfo(criteria, requestInfo);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(applicationsList, result);

        verify(enrichmentService, times(1)).enrichApplicationCriteriaWithOwnerids(criteria, applicationsList);
        verify(userService, times(1)).getUser(criteria, requestInfo);
        verify(enrichmentService, times(1)).enrichOwner(userResponse, applicationsList);
    }

    @Test
    void deleteNdcApplication_shouldThrowException_whenUuidIsNull() {
        NdcDeleteRequest deleteRequest = new NdcDeleteRequest();
        deleteRequest.setUuid(null);
        deleteRequest.setTenantId("tenant1");
        deleteRequest.setRequestInfo(mockRequestInfo());
        deleteRequest.setActive(false);

        CustomException ex = assertThrows(CustomException.class, () ->
                ndcService.deleteNdcApplication(deleteRequest));
        assertEquals("APPLICANT_UUID_NULL", ex.getCode());
    }

    @Test
    void deleteNdcApplication_shouldThrowException_whenTenantIdIsNull() {
        NdcDeleteRequest deleteRequest = new NdcDeleteRequest();
        deleteRequest.setUuid("uuid1");
        deleteRequest.setTenantId(null);
        deleteRequest.setRequestInfo(mockRequestInfo());
        deleteRequest.setActive(false);

        CustomException ex = assertThrows(CustomException.class, () ->
                ndcService.deleteNdcApplication(deleteRequest));
        assertEquals("APPLICANT_TENANT_NULL", ex.getCode());
    }

    @Test
    void deleteNdcApplication_shouldThrowException_whenApplicationNotFound() {
        NdcDeleteRequest deleteRequest = new NdcDeleteRequest();
        deleteRequest.setUuid("uuid1");
        deleteRequest.setTenantId("tenant1");
        deleteRequest.setRequestInfo(mockRequestInfo());
        deleteRequest.setActive(false);

        when(ndcRepository.checkApplicationExists("uuid1")).thenReturn(false);

        CustomException ex = assertThrows(CustomException.class, () ->
                ndcService.deleteNdcApplication(deleteRequest));
        assertEquals("APPLICANT_NOT_FOUND", ex.getCode());
    }

    @Test
    void deleteNdcApplication_shouldSuccessfullyDeleteAndPushEvent() {
        // Arrange
        String uuid = "uuid1";
        String tenantId = "tenant1";
        RequestInfo requestInfo = mockRequestInfo();
        String userId = requestInfo.getUserInfo().getUuid();

        NdcDeleteRequest deleteRequest = new NdcDeleteRequest();
        deleteRequest.setUuid(uuid);
        deleteRequest.setTenantId(tenantId);
        deleteRequest.setRequestInfo(requestInfo);
        deleteRequest.setActive(false);

        AuditDetails auditDetails = new AuditDetails();
        Application application = new Application();
        application.setUuid(uuid);
        application.setTenantId(tenantId);
        application.setAuditDetails(auditDetails);
        application.setActive(true);

        NdcApplicationSearchCriteria criteria = NdcApplicationSearchCriteria.builder()
                .tenantId(tenantId)
                .uuid(Collections.singletonList(uuid))
                .build();

        List<Application> applications = Collections.singletonList(application);

        when(ndcRepository.checkApplicationExists(uuid)).thenReturn(true);
        when(config.getDeleteTopic()).thenReturn("delete-topic");
        doNothing().when(producer).push(anyString(), any());

        NDCService spyService = spy(ndcService);
        doReturn(applications).when(spyService).searchNdcApplications(any(), any());

        // Act
        NdcApplicationRequest result = spyService.deleteNdcApplication(deleteRequest);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getApplications());
        assertEquals(1, result.getApplications().size());
        Application updatedApp = result.getApplications().get(0);

        assertEquals(uuid, updatedApp.getUuid());
        assertFalse(updatedApp.getActive());
        assertEquals(userId, updatedApp.getAuditDetails().getLastModifiedBy());
        assertTrue(updatedApp.getAuditDetails().getLastModifiedTime() > 0);

        verify(producer).push("delete-topic", application);
    }

    private RequestInfo mockRequestInfo() {
        RequestInfo ri = new RequestInfo();
        org.egov.common.contract.request.User user = new org.egov.common.contract.request.User();
        user.setUuid("user-uuid");
        ri.setUserInfo(user);
        return ri;
    }
}
