package org.upyog.rs.util;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.upyog.rs.web.models.Address;
import org.upyog.rs.web.models.ApplicantDetail;
import org.upyog.rs.web.models.AuditDetails;
import org.upyog.rs.web.models.Workflow;
import org.upyog.rs.web.models.waterTanker.WaterTankerBookingDetail;
import org.upyog.rs.web.models.waterTanker.WaterTankerBookingRequest;

import java.time.LocalDate;
import java.time.LocalTime;
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
    public static WaterTankerBookingRequest createWaterTankerRequest() {
        // Set request info
        RequestInfo requestInfo = createRequestInfo();
        WaterTankerBookingDetail waterTankerBookingDetail = WaterTankerBookingDetail.builder()
                .bookingId("BK12345")
                .bookingNo("WT-2025-0001")
                .tankerType("Large")
                .tankerQuantity(2)
                .waterQuantity(5000)
                .description("Water tanker booking for construction site")
                .applicantUuid("applicant-uuid-123")
                .deliveryDate(LocalDate.of(2025, 5, 15))
                .deliveryTime(LocalTime.of(10, 30))
                .extraCharge("200")
                .vendorId("vendor-001")
                .vehicleId("vehicle-001")
                .driverId("driver-001")
                .vehicleType("Truck")
                .vehicleCapacity("5000L")
                .paymentDate(System.currentTimeMillis())
                .applicationDate(System.currentTimeMillis())
                .bookingCreatedBy("Citizen")
                .tenantId("pb.amritsar")
                .addressDetailId("address-001")
                .bookingStatus("PENDING")
                .receiptNo("RCPT-987654")
                .permissionLetterFilestoreId("file-store-id-permission")
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
                .auditDetails(AuditDetails.builder()
                        .createdBy("system")
                        .createdTime(System.currentTimeMillis())
                        .lastModifiedBy("system")
                        .lastModifiedTime(System.currentTimeMillis())
                        .build())
                .build();
        // Set request detail
        WaterTankerBookingRequest request = WaterTankerBookingRequest.builder()
                .requestInfo(requestInfo)
                .waterTankerBookingDetail(waterTankerBookingDetail)
                .build();

        // Create and return the request
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