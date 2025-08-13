package org.upyog.tp.util;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.upyog.tp.web.models.*;
import org.upyog.tp.web.models.treePruning.TreePruningBookingDetail;
import org.upyog.tp.web.models.treePruning.TreePruningBookingRequest;

import java.math.BigDecimal;
import java.util.Arrays;
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
    public static TreePruningBookingRequest createTreePruningRequest() {
        // Set request info
        RequestInfo requestInfo = createRequestInfo();
        TreePruningBookingDetail treePruningBookingDetail = TreePruningBookingDetail.builder()
                .bookingId("BK12345")
                .bookingNo("TP-2025-0001")
                .reasonForPruning("Hanging branches")
                .latitude(BigDecimal.valueOf(31.6340))  // FIX 1: Convert double to BigDecimal
                .longitude(BigDecimal.valueOf(74.8723)) // FIX 1: Convert double to BigDecimal
                .paymentDate(System.currentTimeMillis())
                .applicationDate(System.currentTimeMillis())
                .bookingCreatedBy("Citizen")
                .tenantId("pb.amritsar")
                .addressDetailId("address-001")
                .bookingStatus("PENDING")
                .receiptNo("RCPT-987654")
                .paymentReceiptFilestoreId("file-store-id-payment")
                .mobileNumber("9876543210")
                .localityCode("LOC001")
                .applicantDetail(ApplicantDetail.builder()
                        .name("John Doe")
                        .emailId("john.doe@example.com")
                        .mobileNumber("9876543210")
                        .build())
                .address(Address.builder()
                        .doorNo("123")
                        .streetName("Main Street")
                        .city("Amritsar")
                        .pincode("143001")
                        .build())
                .workflow(Workflow.builder()
                        .action("APPLY")
                        .status("INITIATED")
                        .build())
                .documentDetails(Arrays.asList(
                        DocumentDetail.builder()
                                .documentDetailId("DOC123")
                                // FIX 2: Remove the non-existent applicationId() method
                                // .applicationId("BK12345")  // This method doesn't exist
                                .documentType("PruningPermit")
                                .fileStoreId("filestore-123")
                                .auditDetails(AuditDetails.builder()
                                        .createdBy("system")
                                        .createdTime(System.currentTimeMillis())
                                        .lastModifiedBy("system")
                                        .lastModifiedTime(System.currentTimeMillis())
                                        .build())
                                .build()
                ))
                .auditDetails(AuditDetails.builder()
                        .createdBy("system")
                        .createdTime(System.currentTimeMillis())
                        .lastModifiedBy("system")
                        .lastModifiedTime(System.currentTimeMillis())
                        .build())
                .build(); // <--- This is required


        TreePruningBookingRequest request = TreePruningBookingRequest.builder()
                .requestInfo(requestInfo)
                .treePruningBookingDetail(treePruningBookingDetail)
                .build();

        return request;
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