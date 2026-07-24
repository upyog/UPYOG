package org.egov.loadgenerator.generator.impl;

import lombok.RequiredArgsConstructor;
import org.egov.loadgenerator.config.LoadGeneratorConfig;
import org.egov.loadgenerator.generator.ModuleGenerator;
import org.egov.loadgenerator.util.FakeDataUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Property Tax (PT) payload generator.
 */
@Component
@RequiredArgsConstructor
public class PTModuleGenerator implements ModuleGenerator {

    private final FakeDataUtil fakeDataUtil;
    private final LoadGeneratorConfig config;

// Supported property types
private static final String[] PROPERTY_TYPES = {
    "BUILTUP.INDEPENDENTPROPERTY"
};    // Supported usage categories
    private static final String[] USAGE_TYPES = {"RESIDENTIAL"};
    // Supported ownership categories
    private static final String[] OWNERSHIP_TYPES = {"INDIVIDUAL.SINGLEOWNER", "INDIVIDUAL.MULTIPLEOWNERS"};

    @Override
    public String getModuleName() {
        return "PT";
    }

    @Override
    /** Returns the PT create API endpoint. */
    public String getCreateApiUrl() {
        return config.getPtHost() + config.getPtCreateEndpoint();
    }
    
    @Override
/** Returns the PT workflow update API endpoint. */
public String getUpdateApiUrl() {
    return config.getPtHost() + config.getPtUpdateEndpoint();

    
}
@Override
/** Returns the PT search API endpoint. */
public String getSearchApiUrl() {
    return config.getPtHost() + config.getPtSearchEndpoint();
}
    @Override
    /** Builds a randomized PT create request payload. */
    public Object buildPayload(String tenantId, int index) {
    // Build RequestInfo
    Map<String, Object> requestInfo = buildRequestInfo(tenantId);
        // Build property address
        Map<String, Object> address = new HashMap<>();
        address.put("tenantId", tenantId);
        address.put("doorNo", fakeDataUtil.randomDoorNo());
        address.put("street", fakeDataUtil.randomAddress());
        address.put("city", "New Delhi");
        address.put("pincode", fakeDataUtil.randomPincode());
        address.put("buildingName", "Building " + index);
        // Configure locality information
        Map<String, Object> locality = new HashMap<>();
        locality.put("code", "JLC476");
        locality.put("area", "Area1");

        address.put("locality", locality);

        // Configure geo location
        Map<String, Object> geoLocation = new HashMap<>();
        geoLocation.put("latitude", fakeDataUtil.randomLatitude());
        geoLocation.put("longitude", fakeDataUtil.randomLongitude());
        address.put("geoLocation", geoLocation);

       // Configure construction details
       Map<String, Object> constructionDetail = new HashMap<>();
       constructionDetail.put("builtUpArea", fakeDataUtil.randomInt(150, 400));


// Configure property unit
Map<String, Object> unit = new HashMap<>();
unit.put("tenantId", tenantId);
unit.put("usageCategory", "RESIDENTIAL");
unit.put("occupancyType", "SELFOCCUPIED");
unit.put("floorNo", "0");
unit.put("constructionDetail", constructionDetail);

        // Generate owner information
        Map<String, Object> owner = new HashMap<>();
        owner.put("name", fakeDataUtil.randomName());
        owner.put("mobileNumber", fakeDataUtil.randomMobile());
        owner.put("emailId", fakeDataUtil.randomEmail());
        owner.put("gender", "MALE");
        owner.put("fatherOrHusbandName", fakeDataUtil.randomName());
        owner.put("correspondenceAddress", fakeDataUtil.randomAddress());
        owner.put("permanentAddress", fakeDataUtil.randomAddress());
        owner.put("relationship", "FATHER");
        owner.put("ownerType", "NONE");
        owner.put("isPrimaryOwner", true);

        // Assemble property details
        Map<String, Object> property = new HashMap<>();
        property.put("tenantId", tenantId);
        property.put("propertyType", "BUILTUP.INDEPENDENTPROPERTY");
        property.put("ownershipCategory", fakeDataUtil.randomFrom(OWNERSHIP_TYPES));
        property.put("usageCategory", "RESIDENTIAL");
        property.put("usageCategoryMajor", "RESIDENTIAL");
        property.put("usageCategoryMinor", null);
        property.put("superBuiltUpArea", fakeDataUtil.randomInt(150, 400));
        property.put("address", address);
        property.put("units", List.of(unit));
        property.put("owners", List.of(owner));
        property.put("landArea", fakeDataUtil.randomInt(50, 1000));
        property.put("noOfFloors", fakeDataUtil.randomInt(1, 5));
        property.put("source", "MUNICIPAL_RECORDS");
        property.put("channel", "CFC_COUNTER");
        property.put("creationReason", "CREATE");
        property.put("applicationStatus", "CREATE");

        // Build final request payload
        Map<String, Object> payload = new HashMap<>();
        payload.put("RequestInfo", requestInfo);
        payload.put("Property", property);

        return payload;
    }
    /** Builds RequestInfo required by PT APIs. */
    private Map<String, Object> buildRequestInfo(String tenantId) {
        // Configure system user information
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", 1);
        userInfo.put("uuid", config.getSystemUserUuid());
        userInfo.put("userName", "load-test-user");
        userInfo.put("mobileNumber", fakeDataUtil.randomMobile());
        userInfo.put("type", "EMPLOYEE");
        
        // Assign workflow roles
        List<Map<String, Object>> roles = new ArrayList<>();

// Supported workflow roles
String[][] roleData = {
    {"EGF_VOUCHER_CREATOR", "Finance Voucher Creator"},
    {"PT_DOC_VERIFIER", "PT Doc Verifier"},
    {"PT_COLLECTION_EMP", "Property Tax Collection Employee"},
    {"DASHBOARD_EMPLOYEE", "Dashboard Employee"},
    {"PTR_VERIFIER", "Pet Document Verifier"},
    {"PROPERTY_APPROVER", "Property Approver"},
    {"PT_CEMP", "PT Counter Employee"},
    {"PTR_CEMP", "Pet Counter Employee"},
    {"PT_REPORT_VIEWER", "PT Report Viewer"},
    {"SYS_INTEGRATOR_FINANCE", "System Integrator Finance"},
    {"CR_PT", "Property Tax Receipt Cancellator"},
    {"PT_DASHBOARD_VIEWER", "PT Dashboard Viewer"},
    {"EGF_VOUCHER_APPROVER", "Finance Voucher Approver"},
    {"PT_APPROVER", "PT Counter Approver"},
    {"PT_FIELD_INSPECTOR", "PT Field Inspector"},
    {"PTR_DASHBOARD_VIEWER", "PTR Dashboard Viewer"},
    {"PROPERTY_VERIFIER", "Property Verifier"},
    {"PTR_APPROVER", "Pet Approver"},
    {"SUPERUSER", "Super User"}
};

for (String[] r : roleData) {
    Map<String, Object> role = new HashMap<>();
    role.put("code", r[0]);
    role.put("name", r[1]);
    role.put("tenantId", tenantId);
    roles.add(role);
}

userInfo.put("roles", roles);

        // Build RequestInfo
        Map<String, Object> requestInfo = new HashMap<>();
        requestInfo.put("apiId", "load-generator");
        requestInfo.put("ver", "1.0");
        requestInfo.put("ts", fakeDataUtil.currentEpoch());
        requestInfo.put("action", "_create");
        requestInfo.put("msgId", fakeDataUtil.uuid());
        requestInfo.put("authToken", config.getInternalAuthToken());
        requestInfo.put("userInfo", userInfo);
        return requestInfo;
    }
}
