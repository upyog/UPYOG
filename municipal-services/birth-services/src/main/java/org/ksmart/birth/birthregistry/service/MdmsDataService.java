package org.ksmart.birth.birthregistry.service;


import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.birthregistry.model.RegisterCertificateData;
import org.ksmart.birth.common.services.MdmsLocationService;
import org.ksmart.birth.common.services.MdmsTenantService;
import org.ksmart.birth.ksmartbirthapplication.model.newbirth.KsmartBirthAppliactionDetail;
import org.ksmart.birth.utils.BirthConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.ksmart.birth.utils.BirthConstants.*;

@Slf4j
@Service
public class MdmsDataService {

     private RestTemplate restTemplate;

    @Value("${egov.mdms.host}")
    private String mdmsHost;

    @Value("${egov.mdms.search.endpoint}")
    private String mdmsUrl;

    @Value("${egov.mdms.module.name}")
    private String moduleName;

    private final MdmsTenantService mdmsTenantService;

    private final MdmsLocationService mdmsLocationService;

    @Autowired
    MdmsDataService(RestTemplate restTemplate, MdmsTenantService mdmsTenantService, MdmsLocationService mdmsLocationService) {

        this.restTemplate = restTemplate;

        this.mdmsTenantService = mdmsTenantService;

        this.mdmsLocationService = mdmsLocationService;
    }


    public Object mdmsLocCall(RequestInfo requestInfo, String tenantId) {
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

    public Object mdmsTenantCall(RequestInfo requestInfo) {
        // Call MDMS microservice with MdmsCriteriaReq as params

        MdmsCriteriaReq mdmsCriteriaReq = getTenantRequest(requestInfo);
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

    private MdmsCriteriaReq getTenantRequest(RequestInfo requestInfo) {

        List<ModuleDetail> moduleDetails = new LinkedList<>();

        moduleDetails.addAll(getTenantModuleDetails());

        MdmsCriteria mdmsCriteria = MdmsCriteria.builder()
                                                .moduleDetails(moduleDetails)
                                                .tenantId(BirthConstants.CR_MDMS_TENANT)
                                                .build();

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

        ModuleDetail crModuleDetail = ModuleDetail.builder()
                                                  .masterDetails(crMasterDetails)
                                                  .moduleName(BirthConstants.LOCATION_MDMS_MODULE)
                                                  .build();

        return Collections.singletonList(crModuleDetail);

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
    public List<RegisterCertificateData> setTenantDetails(List<RegisterCertificateData> registerBirthDetails, RequestInfo requestInfo) {
        registerBirthDetails
                .forEach(register -> {
                    Object mdmsData = mdmsTenantCall(requestInfo);
                    register.setTenantLbType(mdmsTenantService.getTenantLbType(mdmsData, register.getTenantId()));
                    register.setTenantDistrict(mdmsTenantService.getTenantDistrict(mdmsData, register.getTenantId()));
                    register.setTenantTaluk(mdmsTenantService.getTenantTaluk(mdmsData, register.getTenantId()));
                    register.setTenantState(mdmsTenantService.getTenantState(mdmsData, register.getTenantId()));
                });
        return registerBirthDetails;
    }

    public List<RegisterCertificateData> setLocationDetails(List<RegisterCertificateData> registerBirthDetails, RequestInfo requestInfo) {
        registerBirthDetails
                .forEach(register -> {
                    Object mdmsData = mdmsLocCall(requestInfo, register.getTenantId());

                    if(register.getPlaceDetails().contains(BIRTH_PLACE_HOSPITAL)){
                        String placeEn = mdmsLocationService.getHospitalAddressEn(mdmsData, register.getPlaceDetails())+", "
                                +mdmsLocationService.getHospitalAddressEn(mdmsData, register.getPlaceDetails());
                        String placeMl = mdmsLocationService.getHospitalNameMl(mdmsData, register.getPlaceDetails())+" , "
                                +mdmsLocationService.getHospitalAddressMl(mdmsData, register.getPlaceDetails());
                        register.setPlaceDetails(placeEn);
                        register.setPlaceDetailsMl(placeMl);

                    }
                    else if(register.getPlaceDetails().contains(BIRTH_PLACE_INSTITUTION)) {
                        String placeEn = mdmsLocationService.getInstitutionNameEn(mdmsData, register.getPlaceDetails())+", "
                                +mdmsLocationService.getInstitutionNameMl(mdmsData, register.getPlaceDetails());
                        String placeMl = mdmsLocationService.getHospitalNameMl(mdmsData, register.getPlaceDetails())+" , "
                                +mdmsLocationService.getHospitalAddressMl(mdmsData, register.getPlaceDetails());
                        System.out.println(placeEn);
                        register.setPlaceDetails(placeEn);
                        register.setPlaceDetailsMl(placeMl);
                    } else{

                    }
                });
        return registerBirthDetails;
    }

    public List<KsmartBirthAppliactionDetail> setKsmartLocationDetails(List<KsmartBirthAppliactionDetail> ksmartBirthAppliactionDetail, RequestInfo requestInfo) {
        ksmartBirthAppliactionDetail
                .forEach(register -> {
                    Object mdmsData = mdmsLocCall(requestInfo, register.getTenantId());

                    if(register.getPlaceofBirthId().contains(BIRTH_PLACE_HOSPITAL)){
                        String placeEn = mdmsLocationService.getHospitalAddressEn(mdmsData, register.getHospitalId());
                        String placeMl = mdmsLocationService.getHospitalNameMl(mdmsData, register.getHospitalId());
                        register.setHospitalName(placeEn);
                        register.setHospitalNameMl(placeMl);

                    }
                    else if(register.getPlaceofBirthId().contains(BIRTH_PLACE_INSTITUTION)) {
                        String placeEn = mdmsLocationService.getInstitutionNameEn(mdmsData, register.getInstitutionNameCode());
                        String placeMl = mdmsLocationService.getHospitalNameMl(mdmsData, register.getInstitutionNameCode());
                        register.setInstitution(placeEn);
                        register.setInstitutionIdMl(placeMl);
                    } else{

                    }
                });
        return ksmartBirthAppliactionDetail;
    }


}
