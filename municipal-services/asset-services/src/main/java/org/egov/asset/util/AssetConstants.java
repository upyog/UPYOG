package org.egov.asset.util;

public class AssetConstants {

    //Constants For MDMS
    public static final String ASSET_MODULE = "ASSET";
    public static final String ASSET_BusinessService = "ASSET";
    public static final String ASSET_MODULE_CODE = "ASSET";
    // mdms path codes
    public static final String ASSET_JSONPATH_CODE = "$.MdmsRes.ASSET";
    public static final String COMMON_MASTER_JSONPATH_CODE = "$.MdmsRes.common-masters";

    public static final String COMMON_MASTERS_MODULE = "common-masters";
    public static final String NOTIFICATION_LOCALE = "en_IN";

    public static final String NOTIFICATION_APPROVED = "asset.en.counter.approved";
    public static final String NOTIFICATION_REJECTED = "asset.en.counter.rejected";
    public static final String NOTIFICATION_CANCELLED = "asset.en.counter.cancelled";

    public static final String NOTIFICATION_OBJECT_ADDED = "asset.en.edit.object.added";
    public static final String NOTIFICATION_OBJECT_REMOVED = "asset.en.edit.object.removed";
    public static final String NOTIFICATION_OBJECT_MODIFIED = "asset.en.edit.object.modified";


    // mdms master names
    public static final String ASSET_CLASSIFICATION = "AssetClassification";
    public static final String ASSET_PARENT_CATEGORY = "AssetParentCategory";
    public static final String ASSET_CATEGORY = "AssetCategory";
    public static final String ASSET_SUB_CATEGORY = "AssetSubCategory";



    // Asset Status
    public static final String STATUS_INITIATED = "INITIATED";
    public static final String STATUS_APPROVED = "APPROVED";
    public static final String STATUS_REJECTED = "REJECTED";
    public static final String STATUS_CANCELLED = "CANCELLED";
    public static final String ACTION_REJECT = "REJECT";
    public static final String ACTION_CANCEL = "CANCEL";
    public static final String EMPLOYEE = "EMPLOYEE";
    public static final String ASSET_APPROVER = "ASSET_APPROVER";
    public static final String ASSET_INITIATOR = "ASSET_INITIATOR";
    public static final String ASSET_VERIFIER = "ASSET_VERIFIER";
    public static final String INVALID_SEARCH = "INVALID SEARCH";

    public static final String ASSET_STATUS_DISPOSED = "0";
    public static final String ASSET_USAGE_DISPOSED = "DISPOSED";
    public static final String ASSET_NOT_FOUND = "ASSET_NOT_FOUND";
    public static final String ASSET_STATUS_DISPOSED_AND_SOLD = "-1";
    public static final String ASSET_USAGE_DISPOSED_AND_SOLD = "DISPOSED_AND_SOLD";

    public static final String ASSET_USAGE_ASSET_STATUS_REPAIRED = "REPAIRED";
    public static final String ASSET_STATUS_REPAIRED = "1";
}
