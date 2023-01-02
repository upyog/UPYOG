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

        // Add Module Institution
        List<MasterDetail> masterInstitution = Collections.singletonList(MasterDetail.builder()
                                                                                     .name(BirthConstants.COMMON_MDMS_INSTITUTION)
                                                                                     .build());
        crMasterDetails.addAll(masterInstitution);

        // Add Module Medical Attention Type
        List<MasterDetail> masterMedicalAttentionType = Collections.singletonList(MasterDetail.builder()
                                                                                              .name(BirthConstants.COMMON_MDMS_MEDICAL_ATTENTION_TYPE)
                                                                                              .build());
        crMasterDetails.addAll(masterMedicalAttentionType);

        // Add Module InstitutionType
        List<MasterDetail> masterInstitutionType = Collections.singletonList(MasterDetail.builder()
                                                                                         .name(BirthConstants.CR_MDMS_INSTITUTIONTYPE)
                                                                                         .build());
        crMasterDetails.addAll(masterInstitutionType);

        // Add Module DeliveryMethod
        List<MasterDetail> masterDeliveryMethod = Collections.singletonList(MasterDetail.builder()
                                                                                        .name(BirthConstants.CR_MDMS_DELIVERYMETHOD)
                                                                                        .build());
        crMasterDetails.addAll(masterDeliveryMethod);


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
        // Add Module Religion
        List<MasterDetail> masterReligion = Collections.singletonList(MasterDetail.builder()
                                                                                  .name(BirthConstants.COMMON_MDMS_RELIGION)
                                                                                  .build());

        commonMasterDetails.addAll(masterReligion);
        // Add Module Taluk
        List<MasterDetail> masterTaluk = Collections.singletonList(MasterDetail.builder()
                                                                               .name(BirthConstants
                                                                               .COMMON_MDMS_TALUK).build());

        commonMasterDetails.addAll(masterTaluk);
        // Add Module State
        List<MasterDetail> masterState = Collections.singletonList(MasterDetail.builder()
                                                                               .name(BirthConstants
                                                                                .COMMON_MDMS_STATE).build());
        commonMasterDetails.addAll(masterState);
        // Add Module Country
        List<MasterDetail> masterCountry = Collections.singletonList(MasterDetail.builder()
                                                                                 .name(BirthConstants
                                                                                 .COMMON_MDMS_COUNTRY).build());
        commonMasterDetails.addAll(masterCountry);


        // Add Module Village

        List<MasterDetail> masterVillage = Collections.singletonList(MasterDetail.builder()
                                                                                 .name(BirthConstants.COMMON_MDMS_VILLAGE)
                                                                                 .build());

        commonMasterDetails.addAll(masterVillage);

        // Add Module District

        List<MasterDetail> masterDistrict = Collections.singletonList(MasterDetail.builder()
                                                                                  .name(BirthConstants.COMMON_MDMS_DISTRICT)
                                                                                  .build());

        commonMasterDetails.addAll(masterDistrict);

        // Add Module Postoffice

        List<MasterDetail> masterPostOffice = Collections.singletonList(MasterDetail.builder()
                                                                                    .name(BirthConstants.COMMON_MDMS_POSTOFFICE)
                                                                                    .build());

        commonMasterDetails.addAll(masterPostOffice);

        // Add Module LbType

        List<MasterDetail> masterLbType = Collections.singletonList(MasterDetail.builder()
                                                                                .name(BirthConstants.COMMON_MDMS_LBTYPE)
                                                                                .build());

        commonMasterDetails.addAll(masterLbType);

        // Add Module PlaceMaster

        List<MasterDetail> masterBirthPlace = Collections.singletonList(MasterDetail.builder()
                                                                                    .name(BirthConstants.COMMON_MDMS_PLACEMASTER)
                                                                                    .build());

        commonMasterDetails.addAll(masterBirthPlace);

        ModuleDetail commonModuleDetail = ModuleDetail.builder()
                                                      .masterDetails(commonMasterDetails)
                                                      .moduleName(BirthConstants.COMMON_MDMS_MODULE)
                                                      .build();

        return Collections.singletonList(commonModuleDetail);

    }
}