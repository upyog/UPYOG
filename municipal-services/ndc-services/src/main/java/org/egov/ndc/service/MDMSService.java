package org.egov.ndc.service;

import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.egov.ndc.config.NDCConfiguration;
import org.egov.ndc.repository.ServiceRequestRepository;
import org.egov.ndc.util.NDCConstants;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.jayway.jsonpath.JsonPath.read;

@Slf4j
@Service
public class MDMSService {


    private NDCConfiguration config;

    private ServiceRequestRepository serviceRequestRepository;


    @Autowired
    public MDMSService(NDCConfiguration config, ServiceRequestRepository serviceRequestRepository) {
        this.config = config;
        this.serviceRequestRepository = serviceRequestRepository;
    }


    /**
     * Creates MDMS request
     * @param requestInfo The RequestInfo of the calculationRequest
     * @param tenantId The tenantId of the tradeLicense
     * @return MDMSCriteria Request
     */
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
        Object result = serviceRequestRepository.fetchResult(url , mdmsCriteriaReq);
        return result;
    }

    private StringBuilder getMdmsSearchUrl() {
        return new StringBuilder().append(config.getMdmsHost()).append(config.getMdmsEndPoint());
    }

}
