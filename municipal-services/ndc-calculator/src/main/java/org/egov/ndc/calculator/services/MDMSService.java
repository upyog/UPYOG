package org.egov.ndc.calculator.services;

import java.util.ArrayList;
import java.util.List;

import org.egov.ndc.calculator.config.NDCCalculatorConfig;
import org.egov.ndc.calculator.repository.ServiceRequestRepository;
import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.egov.ndc.calculator.utils.NDCConstants;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class MDMSService {

	private final ServiceRequestRepository serviceRequestRepository;

	private final NDCCalculatorConfig config;

    private MdmsCriteriaReq getMDMSRequest(RequestInfo requestInfo, String tenantId) {

        // master details for TL module
        List<MasterDetail> flatFeeMasterDetails = new ArrayList<>();
        // filter to only get code field from master data

        final String flatFeeFilter = "$.[?(@.flatFee!=null)]";

        flatFeeMasterDetails.add(MasterDetail.builder().name(NDCConstants.NDC_FEE_MODULE).filter(flatFeeFilter).build());
        ModuleDetail flatFeeModule = ModuleDetail.builder().masterDetails(flatFeeMasterDetails)
                .moduleName(NDCConstants.NDC_MODULE.toLowerCase()).build();

        List<ModuleDetail> moduleDetails = new ArrayList<>();
        moduleDetails.add(flatFeeModule);

        MdmsCriteria mdmsCriteria = MdmsCriteria.builder().moduleDetails(moduleDetails).tenantId(tenantId)
                .build();

        return MdmsCriteriaReq.builder().requestInfo(requestInfo).mdmsCriteria(mdmsCriteria).build();
    }

    @Cacheable(value = "mdmsCache", key = "'tenantId'", sync = true)
    public Object mDMSCall(RequestInfo requestInfo,String tenantId){
        MdmsCriteriaReq mdmsCriteriaReq = getMDMSRequest(requestInfo,tenantId);
        StringBuilder url = getMdmsSearchUrl();
        return serviceRequestRepository.fetchResult(url , mdmsCriteriaReq);
    }

    private StringBuilder getMdmsSearchUrl() {
        return new StringBuilder().append(config.getMdmsHost()).append(config.getMdmsSearchEndpoint());
    }


}
