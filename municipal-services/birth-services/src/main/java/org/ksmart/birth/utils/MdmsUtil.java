package org.ksmart.birth.utils;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class MdmsUtil {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${egov.mdms.host}")
    private String mdmsHost;

    @Value("${egov.mdms.search.endpoint}")
    private String mdmsUrl;

//    @Value("${egov.mdms.master.name}")
//    private String masterName;

    @Value("${egov.mdms.module.name}")
    private String moduleName;

    public Object mdmsCall(RequestInfo requestInfo) {
        // Call MDMS microservice with MdmsCriteriaReq as params

        MdmsCriteriaReq mdmsCriteriaReq = getMdmsRequest(requestInfo);

        String mdmsUri = String.format("%s%s", mdmsHost, mdmsUrl);
        Object result = null;
        try {
            result = restTemplate.postForObject(mdmsUri, mdmsCriteriaReq, Map.class);
        } catch (Exception e) {
            log.error("Exception occurred while fetching MDMS data: ", e);
        }
        return result;
    }

    private MdmsCriteriaReq getMdmsRequest(RequestInfo requestInfo) {

        List<ModuleDetail> moduleDetails = new LinkedList<>();

        //Add all modules of birth-death services
        moduleDetails.addAll(getCRModuleDetails());

        //Add all modules of common services
        moduleDetails.addAll(getCommonModuleDetails());

        //Prepare MDMS Criteria wih all modules in birth-death services and common services

        MdmsCriteria mdmsCriteria = MdmsCriteria.builder()
                                                .moduleDetails(moduleDetails)
                                                .tenantId(BirthConstants.CR_MDMS_TENANT)
                                                .build();
        //Return MDMS criteria request for calling  MDMS microservice
        return MdmsCriteriaReq.builder()
                              .mdmsCriteria(mdmsCriteria)
                              .requestInfo(requestInfo)
                              .build();
    }

    public List<ModuleDetail> getCRModuleDetails() {
        // master details for CR module

        List<MasterDetail> crMasterDetails = new LinkedList<>();

        //
        List<MasterDetail> masterProfession = Collections.singletonList(MasterDetail.builder()
                                                                                    .name(BirthConstants.CR_MDMS_PROFESSION)
                                                                                    .build());
        crMasterDetails.addAll(masterProfession);

        // Add Module Qualification
        List<MasterDetail> masterQualification = Collections.singletonList(MasterDetail.builder()
                                                                                       .name(BirthConstants.CR_MDMS_QUALIFICATION)
                                                                                       .build());
        crMasterDetails.addAll(masterQualification);



        //Add masters to modules
        ModuleDetail crModuleDetail = ModuleDetail.builder()
                                                  .masterDetails(crMasterDetails)
                                                  .moduleName(BirthConstants.CR_MDMS_MODULE)
                                                  .build();

        return Collections.singletonList(crModuleDetail);

    }

    public List<ModuleDetail> getCommonModuleDetails() {
        // master details for Common module

        List<MasterDetail> commonMasterDetails = new LinkedList<>();

        List<MasterDetail> masterReligion = Collections.singletonList(MasterDetail.builder()
                                                                                  .name(BirthConstants.COMMON_MDMS_RELIGION)
                                                                                  .build());

        commonMasterDetails.addAll(masterReligion);

        ModuleDetail commonModuleDetail = ModuleDetail.builder()
                                                      .masterDetails(commonMasterDetails)
                                                      .moduleName(BirthConstants.COMMON_MDMS_MODULE)
                                                      .build();

        return Collections.singletonList(commonModuleDetail);

    }
}