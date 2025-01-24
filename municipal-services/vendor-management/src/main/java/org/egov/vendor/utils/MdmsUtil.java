package org.egov.vendor.utils;

import lombok.extern.slf4j.Slf4j;
import org.egov.vendor.config.VendorConfiguration;
import org.egov.vendor.repository.ServiceRequestRepository;
import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.egov.vendor.web.controller.VendorController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Component
public class MdmsUtil {


    private final VendorConfiguration config;

    @Autowired
    private final ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private RestTemplate restTemplate;


    @Autowired
    public MdmsUtil(VendorConfiguration config, ServiceRequestRepository serviceRequestRepository) {
        this.config = config;
        this.serviceRequestRepository = serviceRequestRepository;
    }


    /**
     * makes mdms call with the given criteria and reutrn mdms data
     *
     * @param requestInfo
     * @param tenantId
     * @return
     */
    public Object mDMSCall(RequestInfo requestInfo, String tenantId) {
        MdmsCriteriaReq mdmsCriteriaReq = getMDMSRequest(requestInfo, tenantId);
        Object result = serviceRequestRepository.fetchResult(getMdmsSearchUrl(), mdmsCriteriaReq);
        return result;
    }


    /**
     * Returns the URL for MDMS search end point
     *
     * @return URL for MDMS search end point
     */
    public StringBuilder getMdmsSearchUrl() {
        return new StringBuilder().append(config.getMdmsHost()).append(config.getMdmsEndPoint());
    }

    /**
     * prepares the mdms request object
     *
     * @param requestInfo
     * @param tenantId
     * @return
     */
    public MdmsCriteriaReq getMDMSRequest(RequestInfo requestInfo, String tenantId) {
        List<ModuleDetail> moduleRequest = getBPAModuleRequest();

        List<ModuleDetail> moduleDetails = new LinkedList<>();
        moduleDetails.addAll(moduleRequest);

        MdmsCriteria mdmsCriteria = MdmsCriteria.builder().moduleDetails(moduleDetails).tenantId(tenantId).build();

        MdmsCriteriaReq mdmsCriteriaReq = MdmsCriteriaReq.builder().mdmsCriteria(mdmsCriteria).requestInfo(requestInfo)
                .build();
        return mdmsCriteriaReq;
    }

    /**
     * Creates request to search ApplicationType and etc from MDMS
     *
     * @return request to search ApplicationType and etc from MDMS
     */
    public List<ModuleDetail> getBPAModuleRequest() {

        // master details for BPA module
        List<MasterDetail> assetMasterDtls = new ArrayList<>();

        // filter to only get code field from master data
        final String filterCode = "$.[?(@.active==true)].code";

        //assetMasterDtls.add(MasterDetail.builder().name(AssetConstants.ASSET_CLASSIFICATION_MAPPING).filter(filterCode).build());
        assetMasterDtls.add(MasterDetail.builder().name(VendorConstants.VENDOR_CLASSIFICATION).filter(filterCode).build());

        ModuleDetail bpaModuleDtls = ModuleDetail.builder().masterDetails(assetMasterDtls)
                .moduleName(VendorConstants.VENDOR_MANAGEMENT_MODULE).build();

        return Collections.singletonList(bpaModuleDtls);

    }

}