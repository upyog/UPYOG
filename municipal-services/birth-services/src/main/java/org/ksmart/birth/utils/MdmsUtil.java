package org.ksmart.birth.utils;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Component
public class MdmsUtil {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${egov.mdms.host}")
    private String mdmsHost;

    @Value("${egov.mdms.search.endpoint}")
    private String mdmsUrl;

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
    public Object mdmsCallForLocation (RequestInfo requestInfo, String tenantId) {
        // Call MDMS microservice with MdmsCriteriaReq as params

        MdmsCriteriaReq mdmsCriteriaReq = getLocRequest(requestInfo, tenantId);
        String mdmsUri = String.format("%s%s", mdmsHost, mdmsUrl);
        Object result = null;
        try {
            result = restTemplate.postForObject(mdmsUri, mdmsCriteriaReq, Map.class);
        } catch (Exception e) {
            log.error("Exception occurred while fetching MDMS data: ", e);
        }
        return result;
    }

    private MdmsCriteriaReq getLocRequest(RequestInfo requestInfo, String tenantId) {

        List<ModuleDetail> moduleDetails = new LinkedList<>();

        moduleDetails.addAll(getBoundaryDetails());

        //Prepare MDMS Criteria wih all modules in birth-death services and common services

        MdmsCriteria mdmsCriteria = MdmsCriteria.builder()
                .moduleDetails(moduleDetails)
                .tenantId(tenantId)
                .build();
        //Return MDMS criteria request for calling  MDMS microservice
        return MdmsCriteriaReq.builder()
                .mdmsCriteria(mdmsCriteria)
                .requestInfo(requestInfo)
                .build();
    }

    private MdmsCriteriaReq getMdmsRequest(RequestInfo requestInfo) {

        List<ModuleDetail> moduleDetails = new LinkedList<>();

        List<ModuleDetail> moduleDetailsLoc = new LinkedList<>();

        //Add all modules of birth-death services
        moduleDetails.addAll(getCRModuleDetails());

        //Add all modules of common services
        moduleDetails.addAll(getCommonModuleDetails());

        moduleDetails.addAll(getTenantModuleDetails());

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

    public List<ModuleDetail> getBoundaryDetails() {
        // master details for Boundary

        List<MasterDetail> crMasterDetails = new LinkedList<>();

        List<MasterDetail> masterHospital = Collections.singletonList(MasterDetail.builder()
                .name(BirthConstants.LOCATION_MDMS_HOSPITAL)
                .build());
        crMasterDetails.addAll(masterHospital);

        List<MasterDetail> masterInstitution = Collections.singletonList(MasterDetail.builder()
                .name(BirthConstants.LOCATION_MDMS_INSTITUTION)
                .build());
        crMasterDetails.addAll(masterInstitution);

        List<MasterDetail> masterBoundary = Collections.singletonList(MasterDetail.builder()
                .name(BirthConstants.LOCATION_MDMS_BOUNDARY)
                .build());
        crMasterDetails.addAll(masterBoundary);

        ModuleDetail crModuleDetail = ModuleDetail.builder()
                .masterDetails(crMasterDetails)
                .moduleName(BirthConstants.LOCATION_MDMS_MODULE)
                .build();

        return Collections.singletonList(crModuleDetail);

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

        // Add Module InstitutionTypePlaceOfEvent
        List<MasterDetail> masterPlaceOfEventInstitutionType = Collections.singletonList(MasterDetail.builder()
                                                                                                     .name(BirthConstants.CR_MDMS_PLACE_INSTITUTION_TYPE)
                                                                                                     .build());
        crMasterDetails.addAll(masterPlaceOfEventInstitutionType);

        // Add Module DeliveryMethod
        List<MasterDetail> masterDeliveryMethod = Collections.singletonList(MasterDetail.builder()
                                                                                        .name(BirthConstants.CR_MDMS_DELIVERYMETHOD)
                                                                                        .build());
        crMasterDetails.addAll(masterDeliveryMethod);

        // Add Module CauseFoetalDeath
        List<MasterDetail> masterCauseFoetalDeath = Collections.singletonList(MasterDetail.builder()
                                                                                          .name(BirthConstants.CR_MDMS_FOETAL)
                                                                                          .build());
        crMasterDetails.addAll(masterCauseFoetalDeath);

        // Add Module Vehicle Type
        List<MasterDetail> masterVehicleType = Collections.singletonList(MasterDetail.builder()
                                                                                     .name(BirthConstants.CR_MDMS_VEHICLETYPEe)
                                                                                     .build());
        crMasterDetails.addAll(masterVehicleType);


        // Add Module workflow
        List<MasterDetail> masterWorkflow = Collections.singletonList(MasterDetail.builder()
                                                                                  .name(BirthConstants.CR_MDMS_WORKFLOW_NEW)
                                                                                  .build());
        crMasterDetails.addAll(masterWorkflow);

        // Add Module PlaceMaster
        List<MasterDetail> masterBirthPlace = Collections.singletonList(MasterDetail.builder()
                .name(BirthConstants.CR_MDMS_PLACEMASTER)
                .build());
        crMasterDetails.addAll(masterBirthPlace);

        // Add Module marital status
        List<MasterDetail> masterMaritalStatus = Collections.singletonList(MasterDetail.builder()
                .name(BirthConstants.CR_MDMS_MARITAL)
                .build());
        crMasterDetails.addAll(masterMaritalStatus);

        // Add Module initiator
        List<MasterDetail> masterInitiator = Collections.singletonList(MasterDetail.builder()
                .name(BirthConstants.CR_MDMS_INITIATOR)
                .build());
        crMasterDetails.addAll(masterInitiator);

        // Add Module relation
        List<MasterDetail> masterRelation = Collections.singletonList(MasterDetail.builder()
                .name(BirthConstants.CR_MDMS_RELATION)
                .build());
        crMasterDetails.addAll(masterRelation);

        // Add Module care taker
        List<MasterDetail> masterCaretaker = Collections.singletonList(MasterDetail.builder()
                .name(BirthConstants.CR_MDMS_CARETAKER)
                .build());
        crMasterDetails.addAll(masterCaretaker);

        // Add Module ip op list
        List<MasterDetail> masterIpop = Collections.singletonList(MasterDetail.builder()
                .name(BirthConstants.CR_MDMS_IPOP)
                .build());
        crMasterDetails.addAll(masterIpop);

        // Add Module ip op list
        List<MasterDetail> masterOtherBirthPlace = Collections.singletonList(MasterDetail.builder()
                .name(BirthConstants.CR_MDMS_OTHER_BP)
                .build());
        crMasterDetails.addAll(masterOtherBirthPlace);

        crMasterDetails.addAll(masterIpop);


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
                                                                               .name(BirthConstants.COMMON_MDMS_TALUK)
                                                                               .build());

        commonMasterDetails.addAll(masterTaluk);
        // Add Module State
        List<MasterDetail> masterState = Collections.singletonList(MasterDetail.builder()
                                                                               .name(BirthConstants.COMMON_MDMS_STATE)
                                                                               .build());
        commonMasterDetails.addAll(masterState);
        // Add Module Country
        List<MasterDetail> masterCountry = Collections.singletonList(MasterDetail.builder()
                                                                                 .name(BirthConstants.COMMON_MDMS_COUNTRY)
                                                                                 .build());
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


        ModuleDetail commonModuleDetail = ModuleDetail.builder()
                                                      .masterDetails(commonMasterDetails)
                                                      .moduleName(BirthConstants.COMMON_MDMS_MODULE)
                                                      .build();

        return Collections.singletonList(commonModuleDetail);

    }

    public List<ModuleDetail> getTenantModuleDetails() {
        // master details for Tenant module
        moduleName = "tenant";
        List<MasterDetail> tenantDetails = new LinkedList<>();

        List<MasterDetail> masterTenants = Collections.singletonList(MasterDetail.builder()
                                                                                  .name(BirthConstants.CR_MDMS_TENANTS)
                                                                                  .build());
        tenantDetails.addAll(masterTenants);

        ModuleDetail tenantModuleDetail = ModuleDetail.builder()
                                                      .masterDetails(tenantDetails)
                                                      .moduleName(BirthConstants.TENANTS_MODULE)
                                                      .build();

        return Collections.singletonList(tenantModuleDetail);

    }
}