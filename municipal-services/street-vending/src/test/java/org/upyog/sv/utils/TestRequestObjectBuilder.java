package org.upyog.sv.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.upyog.sv.web.models.Address;
import org.upyog.sv.web.models.StreetVendingDetail;
import org.upyog.sv.web.models.StreetVendingRequest;
import org.upyog.sv.web.models.VendorDetail;

/**
 * Utility class to create test request objects for street-vending service.
 */
public class TestRequestObjectBuilder {

    /**
     * Creates a sample RequestInfo object with predefined values for testing.
     *
     * @return RequestInfo object with test data.
     */
    public static RequestInfo createSampleRequestInfo() {
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.setApiId("api-id");
        requestInfo.setMsgId("msg-id");
        requestInfo.setAuthToken("auth-token");
        requestInfo.setUserInfo(new User());
        return requestInfo;
    }

    /**
     * Creates a minimal valid StreetVendingDetail object for testing purposes.
     *
     * @return StreetVendingDetail object with minimal required fields.
     */
    public static StreetVendingDetail createMinimalValidStreetVendingDetail() {
        StreetVendingDetail detail = StreetVendingDetail.builder()
                .tenantId("pb.amritsar")
                .vendingActivity("FOOD")
                .vendingZone("ZONE1")
                .vendingArea(BigDecimal.valueOf(100))
                .localAuthorityName("AMRITSAR_MC")
                .addressDetails(createSampleAddressDetails())
                .vendorDetail(createSampleVendorDetails())
                .build();
        return detail;
    }

    private static List<Address> createSampleAddressDetails() {
        List<Address> addresses = new ArrayList<>();
        Address address = Address.builder()
                .houseNo("123")
                .addressLine1("Test Street")
                .addressLine2("Test Area")
                .city("Amritsar")
                .cityCode("AMR")
                .locality("Test Locality")
                .localityCode("LOC1")
                .pincode("143001")
                .addressType("PERMANENT")
                .build();
        addresses.add(address);
        return addresses;
    }

    private static List<VendorDetail> createSampleVendorDetails() {
        List<VendorDetail> vendors = new ArrayList<>();
        VendorDetail vendor = VendorDetail.builder()
                .name("Test Vendor")
                .dob("1990-01-01")
                .gender('M')
                .fatherName("Test Father")
                .mobileNo("9999999999")
                .emailId("test@example.com")
                .userCategory("GENERAL")
                .build();
        vendors.add(vendor);
        return vendors;
    }

    /**
     * Creates a valid StreetVendingRequest object for testing.
     *
     * @return StreetVendingRequest object with valid data.
     */
    public static StreetVendingRequest createValidStreetVendingRequest() {
        StreetVendingRequest request = StreetVendingRequest.builder()
                .requestInfo(createSampleRequestInfo())
                .streetVendingDetail(createMinimalValidStreetVendingDetail())
                .isDraftApplication(false)
                .build();
        return request;
    }

    /**
     * Creates an invalid StreetVendingRequest object for testing validation errors.
     *
     * @return StreetVendingRequest object with invalid data (missing required fields).
     */
    public static StreetVendingRequest createInvalidStreetVendingRequest() {
        StreetVendingDetail detail = createMinimalValidStreetVendingDetail();
        detail.setVendorDetail(null); // This will trigger validation error
        
        StreetVendingRequest request = StreetVendingRequest.builder()
                .requestInfo(createSampleRequestInfo())
                .streetVendingDetail(detail)
                .isDraftApplication(false)
                .build();
        return request;
    }
} 