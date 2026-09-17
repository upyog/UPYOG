package org.egov.loadgenerator.generator.impl;

import lombok.RequiredArgsConstructor;
import org.egov.loadgenerator.config.LoadGeneratorConfig;
import org.egov.loadgenerator.generator.ModuleGenerator;
import org.egov.loadgenerator.util.FakeDataUtil;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Street Vending (SV) payload generator.
 */
@Component
@RequiredArgsConstructor
public class SVModuleGenerator implements ModuleGenerator {

    private final FakeDataUtil fakeDataUtil;
    private final LoadGeneratorConfig config;

    private static final String[] VENDING_TYPES = {"STATIONARY", "MOBILE"};
    private static final String[] VENDING_ZONES = {"ZONE_A", "ZONE_B", "ZONE_C"};

    @Override
    public String getModuleName() {
        return "SV";
    }

    @Override
    public String getCreateApiUrl() {
        return config.getSvHost() + config.getSvCreateEndpoint();
    }


    
    @Override
    public Object buildPayload(String tenantId, int index) {
        Map<String, Object> requestInfo = buildRequestInfo();

        Map<String, Object> address = new HashMap<>();
        address.put("tenantId", tenantId);
        address.put("doorNo", fakeDataUtil.randomDoorNo());
        address.put("street", fakeDataUtil.randomAddress());
        address.put("city", fakeDataUtil.randomCity());
        address.put("pincode", fakeDataUtil.randomPincode());

        Map<String, Object> vendorDetail = new HashMap<>();
        vendorDetail.put("name", fakeDataUtil.randomName());
        vendorDetail.put("fatherName", fakeDataUtil.randomName());
        vendorDetail.put("mobileNo", fakeDataUtil.randomMobile());
        vendorDetail.put("emailId", fakeDataUtil.randomEmail());
        vendorDetail.put("gender", "MALE");
        vendorDetail.put("dob", "1990-01-01");
        vendorDetail.put("vendingActivity", fakeDataUtil.randomFrom(VENDING_TYPES));
        vendorDetail.put("vendingZone", fakeDataUtil.randomFrom(VENDING_ZONES));
        vendorDetail.put("vendingArea", fakeDataUtil.randomInt(5, 50));
        vendorDetail.put("localAuthorityName", tenantId);
        vendorDetail.put("tenantId", tenantId);
        vendorDetail.put("addressDetails", List.of(address));

        Map<String, Object> payload = new HashMap<>();
        payload.put("RequestInfo", requestInfo);
        payload.put("SVDetail", vendorDetail);

        return payload;
    }

    private Map<String, Object> buildRequestInfo() {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", 1);
        userInfo.put("uuid", config.getSystemUserUuid());
        userInfo.put("userName", "load-test-user");
        userInfo.put("mobileNumber", fakeDataUtil.randomMobile());
        userInfo.put("type", "CITIZEN");

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
