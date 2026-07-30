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
 * Water & Sewerage (WS) payload generator.
 */
@Component
@RequiredArgsConstructor
public class WSModuleGenerator implements ModuleGenerator {

    private final FakeDataUtil fakeDataUtil;
    private final LoadGeneratorConfig config;

    private static final String[] CONNECTION_TYPES = {"Metered", "Non Metered"};
    private static final String[] PROPERTY_USAGE_TYPES = {"Domestic", "Commercial", "Industrial"};

    @Override
    public String getModuleName() {
        return "WS";
    }

    @Override
    public String getCreateApiUrl() {
        return config.getWsHost() + config.getWsCreateEndpoint();
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

        Map<String, Object> connectionHolder = new HashMap<>();
        connectionHolder.put("name", fakeDataUtil.randomName());
        connectionHolder.put("mobileNumber", fakeDataUtil.randomMobile());
        connectionHolder.put("emailId", fakeDataUtil.randomEmail());
        connectionHolder.put("gender", "MALE");
        connectionHolder.put("fatherOrHusbandName", fakeDataUtil.randomName());
        connectionHolder.put("correspondenceAddress", fakeDataUtil.randomAddress());
        connectionHolder.put("ownerType", "NONE");
        connectionHolder.put("isPrimaryOwner", true);

        Map<String, Object> waterConnection = new HashMap<>();
        waterConnection.put("tenantId", tenantId);
        waterConnection.put("connectionType", fakeDataUtil.randomFrom(CONNECTION_TYPES));
        waterConnection.put("propertyUsage", fakeDataUtil.randomFrom(PROPERTY_USAGE_TYPES));
        waterConnection.put("noOfTaps", fakeDataUtil.randomInt(1, 5));
        waterConnection.put("pipeSize", 0.5);
        waterConnection.put("address", address);
        waterConnection.put("connectionHolders", List.of(connectionHolder));
        waterConnection.put("proposedTaps", fakeDataUtil.randomInt(1, 3));
        waterConnection.put("proposedPipeSize", 0.5);

        Map<String, Object> payload = new HashMap<>();
        payload.put("RequestInfo", requestInfo);
        payload.put("WaterConnection", waterConnection);

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
