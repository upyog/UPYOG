package org.egov.loadgenerator.generator.impl;

import lombok.RequiredArgsConstructor;
import org.egov.loadgenerator.config.LoadGeneratorConfig;
import org.egov.loadgenerator.generator.ModuleGenerator;
import org.egov.loadgenerator.util.FakeDataUtil;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Asset module payload generator.
 * Matches AssetRequest -> Asset structure of asset-services.
 */
@Component
@RequiredArgsConstructor
public class AssetModuleGenerator implements ModuleGenerator {

    private final FakeDataUtil fakeDataUtil;
    private final LoadGeneratorConfig config;

    private static final String[] ASSET_PARENT_CATEGORIES = {
            "IMMOVABLE", "MOVABLE", "INFRASTRUCTURE"
    };

    private static final String[] ASSET_CATEGORIES = {
            "LAND", "BUILDING", "VEHICLE", "FURNITURE", "EQUIPMENT", "COMPUTER"
    };

    private static final String[] ASSET_SUB_CATEGORIES = {
            "OFFICE_BUILDING", "ROAD", "TRUCK", "CHAIR", "LAPTOP", "PRINTER"
    };

    private static final String[] DEPARTMENTS = {
            "DEPT_1", "DEPT_2", "DEPT_3", "DEPT_4"
    };

    private static final String[] MODES_OF_ACQUISITION = {
            "PURCHASE", "DONATION", "TRANSFER", "CONSTRUCTION"
    };

    private static final String[] ASSET_USAGES = {
            "ACTIVE", "INACTIVE", "UNDER_MAINTENANCE"
    };

    private static final String[] SOURCE_OF_FINANCE = {
            "MUNICIPAL_FUND", "STATE_GRANT", "CENTRAL_GRANT", "LOAN"
    };

    @Override
    public String getModuleName() {
        return "ASSET";
    }

    

    @Override
    public String getCreateApiUrl() {
        return config.getAssetHost() + config.getAssetCreateEndpoint();
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

        Map<String, Object> workflow = new HashMap<>();
        workflow.put("action", "INITIATE");
        workflow.put("businessService", "asset-create");
        workflow.put("moduleName", "asset-services");
        workflow.put("comments", "Load test asset creation #" + index);

        long purchaseDate = fakeDataUtil.currentEpoch() - (long) fakeDataUtil.randomInt(1, 365) * 86400000L;

        Map<String, Object> asset = new HashMap<>();
        asset.put("tenantId", tenantId);
        asset.put("assetName", "Load Test Asset " + index);
        asset.put("description", "Auto-generated asset for load testing - record #" + index);
        asset.put("assetParentCategory", fakeDataUtil.randomFrom(ASSET_PARENT_CATEGORIES));
        asset.put("assetCategory", fakeDataUtil.randomFrom(ASSET_CATEGORIES));
        asset.put("assetSubCategory", fakeDataUtil.randomFrom(ASSET_SUB_CATEGORIES));
        asset.put("department", fakeDataUtil.randomFrom(DEPARTMENTS));
        asset.put("addressDetails", address);
        asset.put("purchaseCost", fakeDataUtil.randomInt(10000, 500000));
        asset.put("acquisitionCost", fakeDataUtil.randomInt(10000, 500000));
        asset.put("bookValue", fakeDataUtil.randomInt(5000, 400000));
        asset.put("originalBookValue", fakeDataUtil.randomInt(10000, 500000));
        asset.put("purchaseDate", purchaseDate);
        asset.put("invoiceDate", purchaseDate);
        asset.put("invoiceNumber", "INV-" + fakeDataUtil.randomInt(1000, 99999));
        asset.put("purchaseOrderNumber", "PO-" + fakeDataUtil.randomInt(1000, 99999));
        asset.put("modeOfPossessionOrAcquisition", fakeDataUtil.randomFrom(MODES_OF_ACQUISITION));
        asset.put("assetUsage", fakeDataUtil.randomFrom(ASSET_USAGES));
        asset.put("sourceOfFinance", fakeDataUtil.randomFrom(SOURCE_OF_FINANCE));
        asset.put("lifeOfAsset", String.valueOf(fakeDataUtil.randomInt(5, 30)));
        asset.put("financialYear", "2024-25");
        asset.put("location", fakeDataUtil.randomCity());
        asset.put("remarks", "Load test remark #" + index);
        asset.put("workflow", workflow);

        Map<String, Object> payload = new HashMap<>();
        payload.put("RequestInfo", requestInfo);
        payload.put("Asset", asset);

        return payload;
    }

    private Map<String, Object> buildRequestInfo() {
        Map<String, Object> role1 = new HashMap<>();
        role1.put("code", "ASSET_INITIATOR");
        role1.put("name", "Asset Initiator Employee");
        role1.put("tenantId", "pg.citya");

        Map<String, Object> role2 = new HashMap<>();
        role2.put("code", "ASSET_INITIATOR");
        role2.put("name", "Asset Initiator Employee");
        role2.put("tenantId", "pg");

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", config.getSystemUserId());
        userInfo.put("uuid", config.getSystemUserUuid());
        userInfo.put("userName", "UPYOGADMIN");
        userInfo.put("mobileNumber", "7676112322");
        userInfo.put("type", "EMPLOYEE");
        userInfo.put("tenantId", "pg.citya");
        userInfo.put("roles", java.util.List.of(role1, role2));

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
