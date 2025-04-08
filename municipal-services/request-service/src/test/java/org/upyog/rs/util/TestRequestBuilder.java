package org.upyog.rs.util;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.upyog.rs.web.models.RequestServiceRequest;
import org.upyog.rs.web.models.Request;

import java.util.UUID;

/**
 * Utility class to build test request objects for the Request Service.
 * This class provides methods to create valid request objects for testing.
 */
public class TestRequestBuilder {

    /**
     * Creates a valid RequestServiceRequest for testing.
     * 
     * @return A RequestServiceRequest with valid data
     */
    public static RequestServiceRequest createRequestServiceRequest() {
        // Set request info
        RequestInfo requestInfo = createRequestInfo();
        
        // Set request detail
        Request request = new Request();
        request.setTenantId("pb");
        request.setRequestNo("REQ-" + UUID.randomUUID().toString().substring(0, 8));
        request.setStatus("INITIATED");
        request.setDescription("Test request description");
        request.setRequestType("GENERAL");
        request.setDepartment("ADMIN");
        request.setWard("W1");
        request.setLocality("L1");
        request.setAddress("123 Test Street");
        request.setLatitude(12.9716);
        request.setLongitude(77.5946);
        
        // Create and return the request
        return RequestServiceRequest.builder()
                .requestInfo(requestInfo)
                .request(request)
                .build();
    }
    
    /**
     * Creates a valid RequestInfo object for testing.
     * 
     * @return A RequestInfo with valid data
     */
    public static RequestInfo createRequestInfo() {
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.setApiId("api-id");
        requestInfo.setMsgId("msg-id");
        requestInfo.setTs(System.currentTimeMillis());
        
        User user = new User();
        user.setId(1L);
        user.setUuid(UUID.randomUUID().toString());
        user.setUserName("test-user");
        user.setType("CITIZEN");
        user.setMobileNumber("1234567890");
        user.setEmailId("test@example.com");
        user.setTenantId("pb");
        
        requestInfo.setUserInfo(user);
        return requestInfo;
    }
} 