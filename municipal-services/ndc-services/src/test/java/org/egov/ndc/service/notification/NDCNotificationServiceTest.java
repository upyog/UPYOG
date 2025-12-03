package org.egov.ndc.service.notification;

import org.egov.common.contract.request.RequestInfo;
import org.egov.ndc.config.NDCConfiguration;
import org.egov.ndc.service.UserService;
import org.egov.ndc.util.NotificationUtil;
import org.egov.ndc.web.model.OwnerInfo;
import org.egov.ndc.web.model.SMSRequest;
import org.egov.ndc.web.model.User;
import org.egov.ndc.web.model.UserResponse;
import org.egov.ndc.web.model.ndc.Application;
import org.egov.ndc.web.model.ndc.NdcApplicationRequest;
import org.egov.ndc.web.model.ndc.NdcApplicationSearchCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class NDCNotificationServiceTest {

    @Mock
    private NDCConfiguration config;

    @Mock
    private NotificationUtil util;

    @Mock
    private UserService userService;

    @Mock
    private org.egov.ndc.repository.ServiceRequestRepository serviceRequestRepository; // mocked but not directly used here

    @InjectMocks
    private NDCNotificationService ndcNotificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testProcess_SMSDisabled_NoSMSSent() {
        NdcApplicationRequest ndcRequest = buildNdcRequest();

        when(config.getIsSMSEnabled()).thenReturn(false);

        ndcNotificationService.process(ndcRequest);

        verify(util, never()).sendSMS(anyList(), anyBoolean());
    }

    private NdcApplicationRequest buildNdcRequest() {
        NdcApplicationRequest request = new NdcApplicationRequest();
        Application app = new Application();
        app.setTenantId("tenant1");
        OwnerInfo owner = new OwnerInfo();
        owner.setUuid("ownerUuid");
        app.setOwners(Collections.singletonList(owner));
        request.setApplications(Collections.singletonList(app));
        request.setRequestInfo(new RequestInfo());
        return request;
    }

    private UserResponse buildUserResponse() throws IllegalAccessException, NoSuchFieldException {
        UserResponse ur = new UserResponse();
        User user = new User();
        user.setMobileNumber("9999999999");
        user.setName("John Doe");
        Field usersField = UserResponse.class.getDeclaredField("user"); // field name as in the class
        usersField.setAccessible(true);
        usersField.set(ur, Collections.singletonList(user));
        return ur;
    }
}
