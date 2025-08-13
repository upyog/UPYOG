package org.egov.vendor.service;

import org.egov.common.contract.request.RequestInfo;
import org.egov.vendor.util.VendorUtil;

public interface RequestType {
    RequestInfo getRequestInfo();
    String getTenantId();
    String getModuleNameOrDefault(VendorUtil vendorUtil);
}

