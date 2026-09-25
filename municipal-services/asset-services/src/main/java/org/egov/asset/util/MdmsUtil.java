package org.egov.asset.util;

import lombok.extern.slf4j.Slf4j;
import org.egov.asset.config.AssetConfiguration;
import org.egov.asset.repository.ServiceRequestRepository;
import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Component
public class MdmsUtil {

    private final AssetConfiguration config;
    private final ServiceRequestRepository serviceRequestRepository;

    public MdmsUtil(AssetConfiguration config, ServiceRequestRepository serviceRequestRepository) {
        this.config = config;
        this.serviceRequestRepository = serviceRequestRepository;
    }

    /**
     * makes mdms call with the given criteria and reutrn mdms data
     *
     * @param requestInfo request metadata
     * @param tenantId    tenant identifier
     * @return MDMS response payload
     */
    public Object mDMSCall(RequestInfo requestInfo, String tenantId) {
        MdmsCriteriaReq mdmsCriteriaReq = getMDMSRequest(requestInfo, tenantId);
        return serviceRequestRepository.fetchResult(getMdmsSearchUrl(), mdmsCriteriaReq);
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
     * @param requestInfo request metadata
     * @param tenantId    tenant identifier
     * @return MDMS criteria request
     */
    public MdmsCriteriaReq getMDMSRequest(RequestInfo requestInfo, String tenantId) {
        List<ModuleDetail> moduleRequest = getBPAModuleRequest();
        List<ModuleDetail> moduleDetails = new LinkedList<>(moduleRequest);

        MdmsCriteria mdmsCriteria = MdmsCriteria.builder().moduleDetails(moduleDetails).tenantId(tenantId).build();

        return MdmsCriteriaReq.builder().mdmsCriteria(mdmsCriteria).requestInfo(requestInfo).build();
    }

    /**
     * Creates request to search ApplicationType and etc from MDMS
     *
     * @return request to search ApplicationType and etc from MDMS
     */
    public List<ModuleDetail> getBPAModuleRequest() {
        List<MasterDetail> assetMasterDtls = new ArrayList<>();
        final String filterCode = "$.[?(@.active==true)].code";

        assetMasterDtls.add(MasterDetail.builder().name(AssetConstants.ASSET_CLASSIFICATION).filter(filterCode).build());
        assetMasterDtls.add(MasterDetail.builder().name(AssetConstants.ASSET_PARENT_CATEGORY).filter(filterCode).build());
        assetMasterDtls.add(MasterDetail.builder().name(AssetConstants.ASSET_CATEGORY).filter(filterCode).build());
        assetMasterDtls.add(MasterDetail.builder().name(AssetConstants.ASSET_SUB_CATEGORY).filter(filterCode).build());

        ModuleDetail bpaModuleDtls = ModuleDetail.builder().masterDetails(assetMasterDtls)
                .moduleName(AssetConstants.ASSET_MODULE).build();

        return Collections.singletonList(bpaModuleDtls);
    }
}
