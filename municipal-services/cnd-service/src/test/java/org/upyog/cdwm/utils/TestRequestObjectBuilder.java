package org.upyog.cdwm.utils;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.common.contract.request.User;
import org.upyog.cdwm.web.models.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for creating test request objects.
 * Provides methods to generate common request objects used in unit tests.
 */
public class TestRequestObjectBuilder {

    /**
     * Creates a sample {@link RequestInfo} object for testing purposes.
     * This method initializes a {@code RequestInfo} object with predefined values
     * that are commonly used in unit tests.
     *
     * @return A {@code RequestInfo} object populated with test data.
     */
    public static RequestInfo createSampleRequestInfo() {
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.setApiId("Rainmaker");
        requestInfo.setAuthToken("4c42ad22-d598-4412-9a0f-765b688f7b89");

        User userInfo = new User();
        userInfo.setId(243L);
        userInfo.setUuid("f8672e56-71e7-4863-b75d-9f3f37d5f742");
        userInfo.setUserName("9999009999");
        userInfo.setName("Master");
        userInfo.setMobileNumber("9999009999");
        userInfo.setEmailId("master@gmail.com");
        userInfo.setType("CITIZEN");
        userInfo.setTenantId("pg");

        List<Role> roles = new ArrayList<>();
        Role role = new Role();
        role.setName("Citizen");
        role.setCode("CITIZEN");
        role.setTenantId("pg");
        roles.add(role);

        userInfo.setRoles(roles);
        requestInfo.setUserInfo(userInfo);
        requestInfo.setMsgId("1742902395870|en_IN");
        requestInfo.setPlainAccessRequest(null);

        return requestInfo;
    }

    /**
     * Creates a minimal valid {@link CNDApplicationDetail} object for testing.
     * This method generates a {@code CNDApplicationDetail} object with essential fields populated
     * to represent a valid application detail, suitable for use in unit tests.
     *
     * @return A {@code CNDApplicationDetail} object populated with minimal valid data.
     */
    public static CNDApplicationDetail createMinimalValidCNDApplicationDetail() {
        CNDApplicationDetail cndApplicationDetail = new CNDApplicationDetail();
        cndApplicationDetail.setTenantId("pg.citya");
        cndApplicationDetail.setApplicationType(CNDApplicationDetail.ApplicationType.valueOf("REQUEST_FOR_PICKUP"));
        cndApplicationDetail.setApplicationStatus("BOOKING_CREATED");
        cndApplicationDetail.setApplicantDetailId("f8672e56-71e7-4863-b75d-9f3f37d5f742");
        cndApplicationDetail.setNoOfTrips(0);
        cndApplicationDetail.setFacilityCenterDetail(FacilityCenterDetail.builder().disposalId("").build());
        cndApplicationDetail.setWasteTypeDetails(new ArrayList<>());
        cndApplicationDetail.setDocumentDetails(new ArrayList<>());
        cndApplicationDetail.setWorkflow(Workflow.builder()
                .action("APPLY")
                .businessService("cnd")
                .moduleName("cnd-service")
                .comments("").build());
        cndApplicationDetail.setApplicantDetail(CNDApplicantDetail.builder()
                .nameOfApplicant("Master")
                .mobileNumber("9999009991")
                .emailId("master@gmail.com")
                .build());
        cndApplicationDetail.setAddressDetail(CNDAddressDetail.builder()
                .houseNumber("Core 5")
                .addressLine1("Lodhi Gali")
                .addressLine2("Habitat Centre")
                .landmark("lodhi hotel rd")
                .floorNumber("Floor 2")
                .city("new delhi")
                .pinCode("123456")
                .build());
        return cndApplicationDetail;
    }

    /**
     * Creates a {@link CNDApplicationRequest} object using the sample {@code RequestInfo}
     * and minimal valid {@code CNDApplicationDetail}. This method combines the creation of
     * these two objects to generate a complete request object suitable for testing.
     *
     * @return A {@code CNDApplicationRequest} object populated with test data.
     */
    public static CNDApplicationRequest createCNDApplicationRequest() {
        CNDApplicationRequest cndApplicationRequest = new CNDApplicationRequest();
        cndApplicationRequest.setRequestInfo(createSampleRequestInfo());
        cndApplicationRequest.setCndApplication(createMinimalValidCNDApplicationDetail());
        return cndApplicationRequest;
    }
}