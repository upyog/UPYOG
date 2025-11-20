package org.egov.ndc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.common.contract.request.PlainAccessRequest;
import org.egov.common.contract.request.RequestInfo;
import org.egov.ndc.config.NDCConfiguration;
import org.egov.ndc.repository.IdGenRepository;
import org.egov.ndc.util.NDCConstants;
import org.egov.ndc.web.model.UserResponse;
import org.egov.ndc.web.model.OwnerInfo;
import org.egov.ndc.web.model.Workflow;
import org.egov.ndc.web.model.idgen.IdResponse;
import org.egov.ndc.web.model.ndc.Application;
import org.egov.ndc.web.model.ndc.NdcApplicationRequest;
import org.egov.ndc.web.model.ndc.NdcApplicationSearchCriteria;
import org.egov.ndc.web.model.workflow.BusinessService;
import org.egov.ndc.web.model.workflow.ProcessInstance;
import org.egov.ndc.web.model.workflow.SearchCriteria;
import org.egov.ndc.web.model.workflow.State;
import org.egov.ndc.workflow.WorkflowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EnrichmentServiceTest {

    @InjectMocks
    private EnrichmentService enrichmentService;

    @Mock
    private NDCConfiguration config;

    @Mock
    private IdGenRepository idGenRepository;

    @Mock
    private WorkflowService workflowService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testPostStatusEnrichment_approvedState_assignsApplicationNo_and_activeTrue() {
        // Arrange
        Application application = new Application();
        application.setApplicationStatus(NDCConstants.APPROVED_STATE);
        application.setTenantId("tenant1");
        application.setActive(true);

        NdcApplicationRequest ndcRequest = new NdcApplicationRequest();
        ndcRequest.setApplications(Collections.singletonList(application));
        RequestInfo requestInfo = new RequestInfo();
        ndcRequest.setRequestInfo(requestInfo);

        BusinessService businessService = new BusinessService();
        State state = new State();
        state.setState(NDCConstants.APPROVED_STATE);

        IdResponse idResponse = IdResponse.builder().id("APPNO123").build();

        when(workflowService.getBusinessService(eq(ndcRequest), eq(requestInfo), anyString())).thenReturn(businessService);
        when(workflowService.getCurrentState(eq(NDCConstants.APPROVED_STATE), eq(businessService))).thenReturn(state);
        when(idGenRepository.getId(eq(requestInfo), eq("tenant1"), anyString(), eq(1)))
                .thenReturn(org.egov.ndc.web.model.idgen.IdGenerationResponse.builder().idResponses(Collections.singletonList(idResponse)).build());
        when(config.getApplicationNoIdgenName()).thenReturn("app_no_gen");

        // Act
        enrichmentService.postStatusEnrichment(ndcRequest, "BUSSVC");

        // Assert
        assertEquals("APPNO123", ndcRequest.getApplications().get(0).getApplicationNo());
        assertTrue(ndcRequest.getApplications().get(0).getActive());
    }

    @Test
    void testPostStatusEnrichment_cancelState_setsActiveFalse() {
        // Arrange
        Application application = new Application();
        application.setApplicationStatus("CANCEL");
        application.setTenantId("tenant1");
        application.setActive(true);

        NdcApplicationRequest ndcRequest = new NdcApplicationRequest();
        ndcRequest.setApplications(Collections.singletonList(application));
        RequestInfo requestInfo = new RequestInfo();
        ndcRequest.setRequestInfo(requestInfo);

        BusinessService businessService = new BusinessService();
        State state = new State();
        state.setState("CANCEL");

        when(workflowService.getBusinessService(eq(ndcRequest), eq(requestInfo), anyString())).thenReturn(businessService);
        when(workflowService.getCurrentState(eq("CANCEL"), eq(businessService))).thenReturn(state);

        // Act
        enrichmentService.postStatusEnrichment(ndcRequest, "BUSSVC");

        // Assert
        assertFalse(ndcRequest.getApplications().get(0).getActive());
    }

    @Test
    void testEnrichApplicationCriteriaWithOwnerids_addsOwnerIds() {
        // Arrange
        OwnerInfo owner1 = OwnerInfo.builder().uuid("owner1").build();
        OwnerInfo owner2 = OwnerInfo.builder().uuid("owner2").build();
        Application application = new Application();
        application.setOwners(Arrays.asList(owner1, owner2));

        NdcApplicationSearchCriteria criteria = new NdcApplicationSearchCriteria();

        // Act
        enrichmentService.enrichApplicationCriteriaWithOwnerids(criteria, Collections.singletonList(application));

        // Assert
        assertNotNull(criteria.getOwnerIds());
        assertTrue(criteria.getOwnerIds().contains("owner1"));
        assertTrue(criteria.getOwnerIds().contains("owner2"));
    }

    @Test
    void testEnrichOwner_enrichesApplicationOwnersFromUserDetails() throws JsonProcessingException {
        // Arrange
        OwnerInfo owner = OwnerInfo.builder().uuid("owner1").build();
        OwnerInfo userDetail = OwnerInfo.builder().uuid("owner1").name("John Doe").build();
        Application application = new Application();
        application.setOwners(Collections.singletonList(owner));

        String jsonString = "{ \"user\": [ { \"uuid\": \"owner1\", \"name\": \"John Doe\" }, { \"uuid\": \"owner2\", \"name\": \"Jane Smith\" } ] }";

        ObjectMapper mapper = new ObjectMapper();
        UserResponse userResponse = mapper.readValue(jsonString, UserResponse.class);

        enrichmentService.enrichOwner(userResponse, Collections.singletonList(application));

        assertFalse(application.getOwners().isEmpty());
        assertEquals("John Doe", application.getOwners().get(0).getName());
    }

    @Test
    void testEnrichProcessInstance_populatesProcessInstanceAndWorkflow() {
        // Arrange
        Application application = new Application();
        application.setApplicationNo("APP123");

        SearchCriteria criteria = new SearchCriteria();
        criteria.setTenantId("tenant1");

        RequestInfo requestInfo = new RequestInfo();
        PlainAccessRequest plainAccessRequest = new PlainAccessRequest();
        requestInfo.setPlainAccessRequest(plainAccessRequest);
        ProcessInstance processInstance = new ProcessInstance();
        processInstance.setBusinessService("BUSSVC");
        processInstance.setModuleName("ndc-module");
        processInstance.setComment("comment");
        processInstance.setDocuments(Collections.emptyList());
        processInstance.setAssignes(Collections.emptyList());
        processInstance.setAction("action");
        processInstance.setId("pid");
        processInstance.setTenantId("tenant1");

        Map<String, ProcessInstance> processInstanceMap = new HashMap<>();
        processInstanceMap.put("APP123", processInstance);

        when(workflowService.getProcessInstances(eq(requestInfo), anySet(), eq("tenant1"), isNull()))
                .thenReturn(processInstanceMap);

        enrichmentService.enrichProcessInstance(Collections.singletonList(application), criteria, requestInfo);

        assertNotNull(application.getProcessInstance());
        assertEquals("BUSSVC", application.getProcessInstance().getBusinessService());
        assertNotNull(application.getWorkflow());
        assertEquals("action", application.getWorkflow().getAction());
    }

    public UserResponse convertJsonToUserResponse(String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(jsonString, UserResponse.class);
        } catch (Exception e) {
            // Handle exceptions appropriately
            e.printStackTrace();
            return null;
        }
    }
}
