package org.bel.birthdeath.crdeath.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.bel.birthdeath.common.repository.ServiceRequestRepository;
import org.bel.birthdeath.crdeath.config.CrDeathConfiguration;
import org.bel.birthdeath.crdeath.web.models.CrDeathDtl;
import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
/**
     * Creates CrDeathService
     * Rakhi S IKM
     * 
     */
@Component
public class CrDeathMdmsUtil {

    private ServiceRequestRepository serviceRequestRepository;
    private CrDeathConfiguration config;

    @Autowired
    public CrDeathMdmsUtil(CrDeathConfiguration config, ServiceRequestRepository serviceRequestRepository) {
        this.config = config;
        this.serviceRequestRepository = serviceRequestRepository;
    }

    public Object mDMSCall(RequestInfo requestInfo, String tenantId) {
        MdmsCriteriaReq mdmsCriteriaReq = getMDMSRequest(requestInfo, tenantId);
        Object result = serviceRequestRepository.fetchResult(getMdmsSearchUrl(), mdmsCriteriaReq);                 
        return result;
    }

    public StringBuilder getMdmsSearchUrl() {
        return new StringBuilder().append(config.getMdmsHost()).append(config.getMdmsEndPoint());
    }

    private MdmsCriteriaReq getMDMSRequest(RequestInfo requestInfo, String tenantId) {
        ModuleDetail tenantIdRequest = getTenantIdRequest(tenantId);
        ModuleDetail GenderTypeRequest = getGenderTypeRequest();
        List<ModuleDetail> BNDListRequest = getBNDListRequest();
        // ModuleDetail DeathPlaceRequest = getDeathPlaceRequest();


        List<ModuleDetail> moduleDetails = new LinkedList<>();
        moduleDetails.add(tenantIdRequest);
        moduleDetails.add(GenderTypeRequest);
        moduleDetails.addAll(BNDListRequest);
        // moduleDetails.add(DeathPlaceRequest);


        // MdmsCriteria mdmsCriteria = MdmsCriteria.builder().moduleDetails(moduleDetails).tenantId(tenantId)
        //         .build();
        MdmsCriteria mdmsCriteria = MdmsCriteria.builder().moduleDetails(moduleDetails).tenantId(requestInfo.getUserInfo().getTenantId())
        .build();

        MdmsCriteriaReq mdmsCriteriaReq = MdmsCriteriaReq.builder().mdmsCriteria(mdmsCriteria)
                .requestInfo(requestInfo).build();

        System.out.println("mdmsreq2"+mdmsCriteriaReq);
        return mdmsCriteriaReq;
    }

     /**
     * Creates request to search tenantID in mdms
     * 
     * @return MDMS request for tenantID
     */
    private ModuleDetail getTenantIdRequest(String tenantId) {

        // master details for crDeath module
        List<MasterDetail> crDeathMasterDetails = new ArrayList<>();

        // filter to only get code field from master data    
        final String filterCode = "$.[?(@.code=='"+tenantId+"')].*";
        crDeathMasterDetails
                .add(MasterDetail.builder().name(CrDeathConstants.TENANTS).filter(filterCode).build());

        // crDeathMasterDetails
        //         .add(MasterDetail.builder().name(CrDeathConstants.TENANTS).build());
        

        ModuleDetail crDeathModuleDtls = ModuleDetail.builder().masterDetails(crDeathMasterDetails)
                .moduleName(CrDeathConstants.TENANT_MODULE_NAME).build();

       
        return crDeathModuleDtls;
    }

     /**
     * Creates request to search Gender Type in mdms
     * 
     * @return MDMS request for Gender Type
     */
    private ModuleDetail getGenderTypeRequest() {

        // master details for crDeath module
        List<MasterDetail> crDeathMasterDetails = new ArrayList<>();

        // filter to only get code field from master data    
        final String filterCode = "$.[?(@.active==true)].code";
        crDeathMasterDetails
                .add(MasterDetail.builder().name(CrDeathConstants.GENDERTYPE).filter(filterCode).build());
       

        ModuleDetail crDeathModuleDtls = ModuleDetail.builder().masterDetails(crDeathMasterDetails)
                .moduleName(CrDeathConstants.GENDER_MODULE_NAME).build();

       
        return crDeathModuleDtls;
    }

     /**
     * Creates request to search Gender Type in mdms
     * 
     * @return MDMS request for HospitalList
     */
    private List<ModuleDetail> getBNDListRequest() {

        // master details for crDeath module
        List<MasterDetail> crDeathMasterDetails = new ArrayList<>();

        // filter to only get code field from master data    
        final String filterCode = "$.[?(@.active==true)].hospitalName";
        crDeathMasterDetails
                .add(MasterDetail.builder().name(CrDeathConstants.HOSPITAL_LIST).filter(filterCode).build());


        final String filterCodePlaceMaster = "$.[?(@.active==true)].code";
         crDeathMasterDetails
                    .add(MasterDetail.builder().name(CrDeathConstants.DEATH_PLACE).filter(filterCodePlaceMaster).build());
        //Rakhi S on 07.12.2022 DeathCause main validation
        final String filterCodeDeathCauseMain = "$.[?(@.active==true)].code";
         crDeathMasterDetails
                    .add(MasterDetail.builder().name(CrDeathConstants.DEATH_CAUSE_MAIN).filter(filterCodeDeathCauseMain).build());
                    
        final String filterCodeDeathCauseSub = "$.[?(@.active==true)].code";
                    crDeathMasterDetails
                               .add(MasterDetail.builder().name(CrDeathConstants.DEATH_CAUSE_SUB).filter(filterCodeDeathCauseSub).build());

        final String filterCodeMaleDependentType = "$.[?(@.active==true)].code";
                               crDeathMasterDetails
                                          .add(MasterDetail.builder().name(CrDeathConstants.MALE_DEPENDENT_TYPE).filter(filterCodeMaleDependentType).build());
       

        final String filterCodeFemaleDependentType = "$.[?(@.active==true)].code";
                                          crDeathMasterDetails
                                                     .add(MasterDetail.builder().name(CrDeathConstants.FEMALE_DEPENDENT_TYPE).filter(filterCodeFemaleDependentType).build());

        final String filterCodeAgeUnit = "$.[?(@.active==true)].code";
                                          crDeathMasterDetails
                                                     .add(MasterDetail.builder().name(CrDeathConstants.AGE_UNIT).filter(filterCodeAgeUnit).build());

        final String filterCodeMedicalAttention = "$.[?(@.active==true)].code";
                                           crDeathMasterDetails
                                                     .add(MasterDetail.builder().name(CrDeathConstants.MEDICAL_ATTENTION_TYPE).filter(filterCodeMedicalAttention).build());

        final String filterCodeProfession = "$.[?(@.active==true)].code";
                                           crDeathMasterDetails
                                                        .add(MasterDetail.builder().name(CrDeathConstants.PROFESSION).filter(filterCodeProfession).build());

        ModuleDetail crDeathModuleDtls = ModuleDetail.builder().masterDetails(crDeathMasterDetails)
                .moduleName(CrDeathConstants.BND_MODULE_NAME).build();

               
         return Arrays.asList(crDeathModuleDtls);
    }

    
    /**
     * Creates request to search Gender Type in mdms
     * 
     * @return MDMS request for DeathPlace master
     */
    // private ModuleDetail getDeathPlaceRequest() {

    //     // master details for crDeath module
    //     List<MasterDetail> crDeathMasterDetails = new ArrayList<>();

    //     // filter to only get code field from master data    
    //     final String filterCode = "$.[?(@.active==true)].code";
    //     crDeathMasterDetails
    //             .add(MasterDetail.builder().name(CrDeathConstants.DEATH_PLACE).filter(filterCode).build());
       

    //     ModuleDetail crDeathModuleDtls = ModuleDetail.builder().masterDetails(crDeathMasterDetails)
    //             .moduleName(CrDeathConstants.DEATH_PLACE_MODULE_NAME).build();

       
    //     return crDeathModuleDtls;
    // }
}
