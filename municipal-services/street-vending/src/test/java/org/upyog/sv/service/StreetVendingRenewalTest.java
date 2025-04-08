package org.upyog.sv.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.upyog.sv.constants.StreetVendingConstants;
import org.upyog.sv.repository.StreetVendingRepository;
import org.upyog.sv.service.impl.StreetVendingServiceImpl;
import org.upyog.sv.validator.StreetVendingValidator;
import org.upyog.sv.web.models.RenewalStatus;
import org.upyog.sv.web.models.StreetVendingDetail;
import org.upyog.sv.web.models.StreetVendingRequest;
import org.upyog.sv.web.models.StreetVendingSearchCriteria;
import org.upyog.sv.web.models.VendorDetail;
import org.upyog.sv.web.models.Workflow;
import org.upyog.sv.web.models.billing.Demand;
import org.upyog.sv.web.models.workflow.State;

import digit.models.coremodels.UserDetailResponse;

@ExtendWith(MockitoExtension.class)
public class StreetVendingRenewalTest {

    @Mock
    private StreetVendingRepository streetVendingRepository;

    @Mock
    private DemandService demandService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private SchedulerService schedulerService;

    @Mock
    private UserService userService;

    @Mock
    private StreetyVendingNotificationService notificationService;

    @Mock
    private StreetVendingValidator validator;

    @InjectMocks
    private StreetVendingServiceImpl streetVendingService;

    private StreetVendingRequest request;
    private StreetVendingDetail detail;
    private RequestInfo requestInfo;

    @BeforeEach
    void setup() {
        requestInfo = RequestInfo.builder()
                .userInfo(User.builder().uuid("test-uuid").build())
                .build();

        detail = StreetVendingDetail.builder()
                .applicationId("test-app-id")
                .applicationNo("APP001")
                .tenantId("test.tenant")
                .certificateNo("CERT001")
                .applicationStatus(StreetVendingConstants.REGISTRATION_COMPLETED)
                .validityDate(LocalDate.now().plusYears(1))
                .expireFlag(false)
                .renewalStatus(RenewalStatus.ELIGIBLE_TO_RENEW)
                .workflow(Workflow.builder()
                    .action("APPLY")
                    .businessService("street-vending")
                    .moduleName("street-vending")
                    .build())
                .vendorDetail(Arrays.asList(VendorDetail.builder()
                        .name("Test Vendor")
                        .relationshipType(StreetVendingConstants.VENDOR)
                        .build()))
                .build();

        request = StreetVendingRequest.builder()
                .requestInfo(requestInfo)
                .streetVendingDetail(detail)
                .build();
                
        // Mock repository to return the detail when searching
        when(streetVendingRepository.getStreetVendingApplications(any(StreetVendingSearchCriteria.class)))
                .thenReturn(Arrays.asList(detail));

        // Mock validator to return the detail
        when(validator.validateApplicationExistence(any())).thenReturn(detail);

        // Mock user service for scheduler tests
        User systemUser = User.builder().uuid("system-user").build();
        UserDetailResponse userResponse = new UserDetailResponse();
        userResponse.setUser(Arrays.asList(systemUser));
        when(userService.searchByUserName(eq(StreetVendingConstants.SYSTEM_CITIZEN_USERNAME), any()))
            .thenReturn(userResponse);

        // Mock notification service
        doNothing().when(notificationService).process(any(), any());
    }

    // @Test
    void testDirectRenewalFlow() {
        // Set renewal status to RENEW_IN_PROGRESS
        detail.setRenewalStatus(RenewalStatus.RENEW_IN_PROGRESS);
        
        // Mock demand creation
        List<Demand> demands = new ArrayList<>();
        demands.add(Demand.builder().id("test-demand").build());
        when(demandService.createDemand(any(), any())).thenReturn(demands);
        
        // Mock repository update
        doNothing().when(streetVendingRepository).update(any());

        // Execute renewal
        StreetVendingDetail result = streetVendingService.updateStreetVendingApplication(request);

        // Verify
        assertNotNull(result);
        assertEquals(RenewalStatus.RENEW_IN_PROGRESS, result.getRenewalStatus());
        verify(demandService).createDemand(any(), any());
        verify(workflowService, never()).updateWorkflowStatus(any());
        verify(streetVendingRepository).update(any());
    }

    // @Test
    void testEditAndSubmitRenewalFlow() {
        // Set renewal status to RENEW_APPLICATION_CREATED
        detail.setRenewalStatus(RenewalStatus.RENEW_APPLICATION_CREATED);
        detail.setOldApplicationNumber("OLD001");

        // Mock workflow update
        State state = State.builder()
                .applicationStatus(StreetVendingConstants.REGISTRATION_COMPLETED)
                .build();
        when(workflowService.updateWorkflowStatus(any())).thenReturn(state);
        
        // Mock repository update
        doNothing().when(streetVendingRepository).update(any());

        // Execute renewal
        StreetVendingDetail result = streetVendingService.updateStreetVendingApplication(request);

        // Verify
        assertNotNull(result);
        assertEquals(RenewalStatus.RENEW_APPLICATION_CREATED, result.getRenewalStatus());
        assertEquals("OLD001", result.getOldApplicationNumber());
        verify(workflowService).updateWorkflowStatus(any());
        verify(streetVendingRepository).update(any());
    }

    // @Test
    void testExpirationNotification() {
        // Set validity date to 2 months from now
        LocalDate validityDate = LocalDate.now().plusMonths(2);
        detail.setValidityDate(validityDate);

        // Mock repository save
        doNothing().when(streetVendingRepository).save(any());

        // Execute scheduler
        schedulerService.processStreetVendingApplications();

        // Verify
        verify(streetVendingRepository).save(any());
        assertEquals(RenewalStatus.ELIGIBLE_TO_RENEW, detail.getRenewalStatus());
    }

    // @Test
    void testApplicationExpiration() {
        // Set validity date to yesterday
        detail.setValidityDate(LocalDate.now().minusDays(1));

        // Mock repository save
        doNothing().when(streetVendingRepository).save(any());

        // Execute scheduler
        schedulerService.processStreetVendingApplications();

        // Verify
        verify(streetVendingRepository).save(any());
        assertTrue(detail.getExpireFlag());
        assertEquals(StreetVendingConstants.STATUS_EXPIRED, detail.getApplicationStatus());
    }

    // @Test
    void testRenewalAfterExpiration() {
        // Set application as expired
        detail.setExpireFlag(true);
        detail.setApplicationStatus(StreetVendingConstants.STATUS_EXPIRED);
        detail.setRenewalStatus(RenewalStatus.ELIGIBLE_TO_RENEW);
        
        // Mock repository update
        doNothing().when(streetVendingRepository).update(any());

        // Execute renewal
        StreetVendingDetail result = streetVendingService.updateStreetVendingApplication(request);

        // Verify
        assertNotNull(result);
        assertTrue(result.getExpireFlag());
        assertEquals(StreetVendingConstants.STATUS_EXPIRED, result.getApplicationStatus());
        verify(streetVendingRepository).update(any());
    }

    // @Test
    void testCertificateNumberConsistency() {
        // Set old application number
        detail.setOldApplicationNumber("OLD001");
        detail.setRenewalStatus(RenewalStatus.RENEW_APPLICATION_CREATED);
        
        // Mock repository update
        doNothing().when(streetVendingRepository).update(any());

        // Execute renewal
        StreetVendingDetail result = streetVendingService.updateStreetVendingApplication(request);

        // Verify
        assertNotNull(result);
        assertEquals("CERT001", result.getCertificateNo());
        assertEquals("OLD001", result.getOldApplicationNumber());
        verify(streetVendingRepository).update(any());
    }

    // @Test
    void testPaymentCompletion() {
        // Set renewal status to RENEW_IN_PROGRESS
        detail.setRenewalStatus(RenewalStatus.RENEW_IN_PROGRESS);
        
        // Mock repository update
        doNothing().when(streetVendingRepository).update(any());

        // Execute payment completion
        StreetVendingDetail result = streetVendingService.updateStreetVendingApplication(request);

        // Verify
        assertNotNull(result);
        assertEquals(RenewalStatus.RENEWED, result.getRenewalStatus());
        assertEquals(StreetVendingConstants.APPLICATION_STATUS_RENEWED, result.getApplicationStatus());
        verify(streetVendingRepository).update(any());
    }
} 