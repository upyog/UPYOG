package org.ksmart.birth.birthcommon.services;

import com.jayway.jsonpath.JsonPath;
import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.ksmart.birth.birthcommon.model.WorkFlowCheck;
import org.ksmart.birth.birthcommon.model.demand.Demand;
import org.ksmart.birth.birthcommon.model.demand.DemandDetail;
import org.ksmart.birth.birthcommon.model.demand.DemandResponse;
import org.ksmart.birth.birthcommon.repoisitory.DemandRepository;
import org.ksmart.birth.common.repository.ServiceRequestRepository;
import org.ksmart.birth.config.BirthConfiguration;
import org.ksmart.birth.utils.BirthDeathConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.ksmart.birth.utils.BirthDeathConstants.*;

@Service
public class DemandService {
    @Autowired
    BirthConfiguration config;

    @Autowired
    ServiceRequestRepository serviceRequestRepository;

    @Autowired
    DemandRepository demandRepository;

    public List<Demand> saveDemandDetails(List<Demand> demands, RequestInfo requestInfo, WorkFlowCheck wfc) {
        demands.forEach(demand -> setDemandParamsLateFee(demand, requestInfo, wfc));
        return  demandRepository.saveDemand(requestInfo,demands);
    }
    public void setDemandParamsLateFee(Demand demand, RequestInfo requestInfo, WorkFlowCheck wfc) {
        demand.setConsumerType("FEE");
        demand.setBusinessService("CR");
        ArrayList<DemandDetail> demandDetails = new ArrayList<>();
        DemandDetail demandDetail=new DemandDetail();
        demandDetail.setTaxHeadMasterCode("140130200");
        demandDetail.setTaxAmount(new BigDecimal(wfc.getAmount()));
        demandDetail.setTenantId(demand.getTenantId());
        setGLCode(demandDetail, requestInfo);
        demandDetails.add(demandDetail);
        demand.setDemandDetails(demandDetails);
        demand.setPayer(requestInfo.getUserInfo());
        demand.setTaxPeriodFrom(System.currentTimeMillis());
        demand.setTaxPeriodTo(System.currentTimeMillis()+86400000);
        demand.setMinimumAmountPayable(new BigDecimal(wfc.getAmount()));
    }

    private ModuleDetail getGLCodeRequest() {
        List<MasterDetail> masterDetails = new ArrayList<>();
        masterDetails.add(MasterDetail.builder().name(TAX_MASTER).build());
        return ModuleDetail.builder().masterDetails(masterDetails)
                .moduleName(BirthDeathConstants.BILLING_SERVICE).build();
    }

    public void setGLCode(DemandDetail demandDetail, RequestInfo requestInfo) {
        String tenantId = demandDetail.getTenantId();
        ModuleDetail glCodeRequest = getGLCodeRequest();
        List<ModuleDetail> moduleDetails = new LinkedList<>();
        moduleDetails.add(glCodeRequest);
        MdmsCriteria mdmsCriteria = MdmsCriteria.builder().moduleDetails(moduleDetails).tenantId(tenantId)
                .build();
        MdmsCriteriaReq mdmsCriteriaReq = MdmsCriteriaReq.builder().mdmsCriteria(mdmsCriteria)
                .requestInfo(requestInfo).build();

        StringBuilder url = new StringBuilder().append(config.getMdmsHost()).append(config.getMdmsEndPoint());

        Object result = serviceRequestRepository.fetchResult(url, mdmsCriteriaReq);
        String jsonPath = TAX_MASTER_JSONPATH_CODE.replace("{}",demandDetail.getTaxHeadMasterCode());
        List<Map<String,Object>> jsonOutput =  JsonPath.read(result, jsonPath);
        if(!jsonOutput.isEmpty()) {
            Map<String,Object> glCodeObj = jsonOutput.get(0);
            demandDetail.setAdditionalDetails(glCodeObj);
        }
    }
}
