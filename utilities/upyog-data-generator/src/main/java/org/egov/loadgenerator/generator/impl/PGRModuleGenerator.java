package org.egov.loadgenerator.generator.impl;

import lombok.RequiredArgsConstructor;
import org.egov.loadgenerator.config.LoadGeneratorConfig;
import org.egov.loadgenerator.generator.ModuleGenerator;
import org.egov.loadgenerator.util.FakeDataUtil;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * PGR (Public Grievance Redressal) payload generator.
 * Matches the ServiceRequest structure of pgr-services.
 */
@Component
@RequiredArgsConstructor
public class PGRModuleGenerator implements ModuleGenerator {

    private final FakeDataUtil fakeDataUtil;
    private final LoadGeneratorConfig config;

    private static final String[] SERVICE_CODES = {
            "NoStreetLight", "BrokenRoad", "GarbageNeedsToBeCleaned",
            "WaterLeakage", "BlockedDrain", "DamagedRoad"
    };

    @Override
    public String getModuleName() {
        return "PGR";
    }

        
    @Override
    public String getCreateApiUrl() {
        return config.getPgrHost() + config.getPgrCreateEndpoint();
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
        address.put("landmark", "Near " + fakeDataUtil.randomCity());

        Map<String, Object> geoLocation = new HashMap<>();
        geoLocation.put("latitude", fakeDataUtil.randomLatitude());
        geoLocation.put("longitude", fakeDataUtil.randomLongitude());
        address.put("geoLocation", geoLocation);

        Map<String, Object> service = new HashMap<>();
        service.put("tenantId", tenantId);
        service.put("serviceCode", fakeDataUtil.randomFrom(SERVICE_CODES));
        service.put("description", "Load test complaint #" + index + " - " + fakeDataUtil.randomAddress());
        service.put("source", "web");
        service.put("address", address);

        Map<String, Object> workflow = new HashMap<>();
        workflow.put("action", "APPLY");
        workflow.put("comments", "Load test record");

        Map<String, Object> payload = new HashMap<>();
        payload.put("RequestInfo", requestInfo);
        payload.put("service", service);
        payload.put("workflow", workflow);

        return payload;
    }

    private Map<String, Object> buildRequestInfo() {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", 1);
        userInfo.put("uuid", config.getSystemUserUuid());
        userInfo.put("userName", "load-test-user");
        userInfo.put("mobileNumber", fakeDataUtil.randomMobile());
        userInfo.put("emailId", fakeDataUtil.randomEmail());
        userInfo.put("type", "CITIZEN");

        Map<String, Object> requestInfo = new HashMap<>();
        requestInfo.put("apiId", "load-generator");
        requestInfo.put("ver", "1.0");
        requestInfo.put("ts", fakeDataUtil.currentEpoch());
        requestInfo.put("action", "_create");
        requestInfo.put("did", "1");
        requestInfo.put("msgId", fakeDataUtil.uuid());
        requestInfo.put("authToken", config.getInternalAuthToken());
        requestInfo.put("userInfo", userInfo);
        return requestInfo;
    }
}
