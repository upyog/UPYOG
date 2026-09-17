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
 * Trade License (TL) payload generator.
 */
@Component
@RequiredArgsConstructor
public class TLModuleGenerator implements ModuleGenerator {

    private final FakeDataUtil fakeDataUtil;
    private final LoadGeneratorConfig config;

    private static final String[] TRADE_TYPES = {
            "RETAIL.GENERAL_STORE", "FOOD.RESTAURANT", "SERVICES.SALON",
            "RETAIL.MEDICAL_STORE", "FOOD.BAKERY", "SERVICES.REPAIR_SHOP"
    };

    private static final String[] STRUCTURE_TYPES = {
            "IMMOVABLE.PUCCA", "IMMOVABLE.SEMI_PUCCA", "MOVABLE.VEHICLE"
    };

    @Override
    public String getModuleName() {
        return "TL";
    }
    

    @Override
    public String getCreateApiUrl() {
        return config.getTlHost() + config.getTlCreateEndpoint();
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

        Map<String, Object> geoLocation = new HashMap<>();
        geoLocation.put("latitude", fakeDataUtil.randomLatitude());
        geoLocation.put("longitude", fakeDataUtil.randomLongitude());
        address.put("geoLocation", geoLocation);

        Map<String, Object> owner = new HashMap<>();
        owner.put("name", fakeDataUtil.randomName());
        owner.put("mobileNumber", fakeDataUtil.randomMobile());
        owner.put("emailId", fakeDataUtil.randomEmail());
        owner.put("gender", "MALE");
        owner.put("fatherOrHusbandName", fakeDataUtil.randomName());
        owner.put("correspondenceAddress", fakeDataUtil.randomAddress());
        owner.put("ownerType", "NONE");

        Map<String, Object> tradeUnit = new HashMap<>();
        tradeUnit.put("tradeType", fakeDataUtil.randomFrom(TRADE_TYPES));
        tradeUnit.put("uom", "UNIT");
        tradeUnit.put("uomValue", "1");

        Map<String, Object> tradeLicense = new HashMap<>();
        tradeLicense.put("tenantId", tenantId);
        tradeLicense.put("tradeName", "Load Test Trade " + index);
        tradeLicense.put("structureType", fakeDataUtil.randomFrom(STRUCTURE_TYPES));
        tradeLicense.put("licenseType", "PERMANENT");
        tradeLicense.put("address", address);
        tradeLicense.put("owners", List.of(owner));
        tradeLicense.put("tradeUnits", List.of(tradeUnit));
        tradeLicense.put("applicationDate", fakeDataUtil.currentEpoch());
        tradeLicense.put("commencementDate", fakeDataUtil.currentEpoch());

        Map<String, Object> workflow = new HashMap<>();
        workflow.put("action", "APPLY");
        workflow.put("comments", "Load test TL record");

        Map<String, Object> payload = new HashMap<>();
        payload.put("RequestInfo", requestInfo);
        payload.put("Licenses", List.of(tradeLicense));
        payload.put("workflow", workflow);

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
