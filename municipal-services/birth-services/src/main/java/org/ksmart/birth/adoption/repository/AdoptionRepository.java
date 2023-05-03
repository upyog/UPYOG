package org.ksmart.birth.adoption.repository;

import lombok.extern.slf4j.Slf4j;

import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetailsRequest;
import org.ksmart.birth.birthregistry.service.MdmsDataService;
import org.ksmart.birth.common.producer.BndProducer;
import org.ksmart.birth.config.BirthConfiguration;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.ksmart.birth.adoption.enrichment.AdoptionEnrichment;
import org.ksmart.birth.adoption.enrichment.AdoptionResponseEnrichment;
import org.ksmart.birth.adoption.repository.querybuilder.AdoptionQueryBuilder;
import org.ksmart.birth.adoption.repository.rowmapper.AdoptionApplicationRowMapper;
import org.ksmart.birth.birthregistry.repository.rowmapperfornewapplication.RegisterRowMapperForApp;
import org.ksmart.birth.adoption.service.MdmsForAdoptionService;
import org.ksmart.birth.birthnac.enrichment.NacResponseEnrichment;
import org.ksmart.birth.utils.BirthConstants;
import org.ksmart.birth.utils.MdmsUtil;
import org.ksmart.birth.utils.enums.ErrorCodes;
import org.ksmart.birth.web.model.SearchCriteria;
import org.ksmart.birth.web.model.adoption.AdoptionApplication;
import org.ksmart.birth.web.model.adoption.AdoptionDetailRequest;
import org.ksmart.birth.web.model.newbirth.NewBirthDetailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class AdoptionRepository {
    private final BndProducer producer;
    private final AdoptionEnrichment adoptionEnrichment;
    private final BirthConfiguration birthDeathConfiguration;
    private final JdbcTemplate jdbcTemplate;
 
    private final AdoptionQueryBuilder adoptionQueryBuilder;
    private final AdoptionApplicationRowMapper adoptionApplicationRowMapper;
 
    private final  MdmsForAdoptionService mdmsDataService;
    private final  MdmsUtil mdmsUtil;
    private final RegisterRowMapperForApp registerRowMapperForApp;

    private final AdoptionResponseEnrichment responseEnrichment;


    @Autowired 
    AdoptionRepository(JdbcTemplate jdbcTemplate, AdoptionEnrichment adoptionEnrichment, BirthConfiguration birthDeathConfiguration,
                       BndProducer producer, AdoptionQueryBuilder adoptionQueryBuilder, AdoptionApplicationRowMapper adoptionApplicationRowMapper, 
                       MdmsForAdoptionService mdmsDataService, MdmsUtil mdmsUtil,RegisterRowMapperForApp registerRowMapperForApp,
                       AdoptionResponseEnrichment responseEnrichment) {
        this.jdbcTemplate = jdbcTemplate;
        this.adoptionEnrichment = adoptionEnrichment;
        this.birthDeathConfiguration = birthDeathConfiguration;
        this.producer = producer;
        this.adoptionQueryBuilder = adoptionQueryBuilder;
        this.adoptionApplicationRowMapper = adoptionApplicationRowMapper;
        this.mdmsDataService = mdmsDataService;
        this.mdmsUtil = mdmsUtil;
        this.registerRowMapperForApp = registerRowMapperForApp;
        this.responseEnrichment=responseEnrichment;
    }
    public RegisterBirthDetailsRequest searchBirthDetailsForRegister(AdoptionDetailRequest requestApplication) {
        List<Object> preparedStmtValues = new ArrayList<>();
        SearchCriteria criteria = new SearchCriteria();
        List<RegisterBirthDetail> result = null;
        if (requestApplication.getAdoptionDetails().size() > 0) {    
        	criteria.setId(requestApplication.getAdoptionDetails().get(0).getId());        	
//            criteria.getApplicationNumber().add(requestApplication.getAdoptionDetails().get(0).getApplicationNo());
            criteria.setTenantId(requestApplication.getAdoptionDetails().get(0).getTenantId());
            String query = adoptionQueryBuilder.getApplicationSearchQueryForRegistry(criteria, preparedStmtValues);
            result = jdbcTemplate.query(query, preparedStmtValues.toArray(), registerRowMapperForApp);
        }
        return RegisterBirthDetailsRequest.builder()
                .requestInfo(requestApplication.getRequestInfo())
                .registerBirthDetails(result).build();
    }

    public List<AdoptionApplication> saveAdoptionDetails(AdoptionDetailRequest request) {    	
        producer.push(birthDeathConfiguration.getSaveBirthAdoptionTopic(), request);
        return request.getAdoptionDetails();
    }

    public List<AdoptionApplication> updateKsmartBirthDetails(AdoptionDetailRequest request) {
    	
        producer.push(birthDeathConfiguration.getUpdateBirthAdoptionTopic(), request);
        return request.getAdoptionDetails();
    }


    public List<AdoptionApplication> searchKsmartBirthDetails(AdoptionDetailRequest request, SearchCriteria criteria) {
        List<Object> preparedStmtValues = new ArrayList<>();
        Object mdmsDataComm = mdmsUtil.mdmsCall(request.getRequestInfo());
        criteria.setApplicationType(BirthConstants.FUN_MODULE_ADOP);
        String query = adoptionQueryBuilder.getAdoptionSearchQuery(criteria, request, preparedStmtValues, Boolean.FALSE);
        List<AdoptionApplication> result = jdbcTemplate.query(query, preparedStmtValues.toArray(), adoptionApplicationRowMapper);
        RequestInfo requestInfo = request.getRequestInfo();
        responseEnrichment.setAdoptionRequestData(requestInfo, result);
        return result;
    }
        
//        
//        if(result.size() == 0){
//            throw new CustomException(ErrorCodes.NOT_FOUND.getCode(), "No result found.");
//        } else if(result.size() >= 1) {
//            result.forEach(birth -> {
//                birth.setIsWorkflow(true);
//                Object mdmsData = mdmsUtil.mdmsCallForLocation(request.getRequestInfo(), birth.getTenantId());
//                Object mdmsDataLoc = mdmsUtil.mdmsCallForLocation(request.getRequestInfo(), birth.getTenantId());
//                if (birth.getPlaceofBirthId() != null) {
//                	mdmsDataService.setLocationDetails(birth, mdmsData,mdmsDataLoc);
//                	mdmsDataService.setInstitutionDetails(birth, mdmsDataComm);
//                }
//                if (birth.getParentAddress().getCountryIdPermanent() != null && birth.getParentAddress().getStateIdPermanent() != null) {
//                    if (birth.getParentAddress().getCountryIdPermanent().contains(BirthConstants.COUNTRY_CODE)) {
//                        if (birth.getParentAddress().getStateIdPermanent().contains(BirthConstants.STATE_CODE_SMALL)) {
//                        	mdmsDataService.setTenantDetails(birth, mdmsDataComm);
//                            birth.getParentAddress().setPermtaddressCountry(birth.getParentAddress().getCountryIdPermanent());
//
//                            birth.getParentAddress().setPermtaddressStateName(birth.getParentAddress().getStateIdPermanent());
//
//                            birth.getParentAddress().setPermntInKeralaAdrDistrict(birth.getParentAddress().getDistrictIdPermanent());
//
//                            birth.getParentAddress().setPermntInKeralaAdrLocalityNameEn(birth.getParentAddress().getLocalityEnPermanent());
//                            birth.getParentAddress().setPermntInKeralaAdrLocalityNameMl(birth.getParentAddress().getLocalityMlPermanent());
//
//                            birth.getParentAddress().setPermntInKeralaAdrStreetNameEn(birth.getParentAddress().getStreetNameEnPermanent());
//                            birth.getParentAddress().setPermntInKeralaAdrStreetNameMl(birth.getParentAddress().getStreetNameMlPermanent());
//
//                            birth.getParentAddress().setPermntInKeralaAdrHouseNameEn(birth.getParentAddress().getHouseNameNoEnPermanent());
//                            birth.getParentAddress().setPermntInKeralaAdrHouseNameMl(birth.getParentAddress().getHouseNameNoMlPermanent());
//
//                            birth.getParentAddress().setPermntInKeralaAdrPostOffice(birth.getParentAddress().getPoNoPermanent());
//
//                        } else {
//                            birth.getParentAddress().setPermtaddressCountry(birth.getParentAddress().getCountryIdPermanent());
//
//                            birth.getParentAddress().setPermtaddressStateName(birth.getParentAddress().getStateIdPermanent());
//                            birth.getParentAddress().setPermntOutsideKeralaDistrict(birth.getParentAddress().getDistrictIdPermanent());
//
//                            birth.getParentAddress().setPermntOutsideKeralaVillage(birth.getParentAddress().getVillageNamePermanent());
//
//                            birth.getParentAddress().setPermntOutsideKeralaLocalityNameEn(birth.getParentAddress().getLocalityEnPermanent());
//                            birth.getParentAddress().setPermntOutsideKeralaLocalityNameMl(birth.getParentAddress().getLocalityMlPermanent());
//
//                            birth.getParentAddress().setPermntOutsideKeralaStreetNameEn(birth.getParentAddress().getStreetNameEnPermanent());
//                            birth.getParentAddress().setPermntOutsideKeralaStreetNameMl(birth.getParentAddress().getStreetNameMlPermanent());
//
//                            birth.getParentAddress().setPermntOutsideKeralaHouseNameEn(birth.getParentAddress().getHouseNameNoEnPermanent());
//                            birth.getParentAddress().setPermntOutsideKeralaHouseNameMl(birth.getParentAddress().getHouseNameNoMlPermanent());
//
//                        }
//                    } else {
//                        birth.getParentAddress().setPermntOutsideIndiaCountry(birth.getParentAddress().getCountryIdPermanent());
//                        birth.getParentAddress().setPermntOutsideKeralaVillage(birth.getParentAddress().getVillageNamePermanent());
//                    }
//                }
//                if (birth.getParentAddress().getCountryIdPresent() != null && birth.getParentAddress().getStateIdPresent() != null) {
//                    if (birth.getParentAddress().getCountryIdPresent().contains(BirthConstants.COUNTRY_CODE)) {
//                        if (birth.getParentAddress().getStateIdPresent().contains(BirthConstants.STATE_CODE_SMALL)) {
//
//                            birth.getParentAddress().setPresentaddressCountry(birth.getParentAddress().getCountryIdPresent());
//
//                            birth.getParentAddress().setPresentaddressStateName(birth.getParentAddress().getStateIdPresent());
//
//                            birth.getParentAddress().setPresentInsideKeralaDistrict(birth.getParentAddress().getDistrictIdPresent());
//
//                            birth.getParentAddress().setPresentInsideKeralaLBName(birth.getParentAddress().getPermntInKeralaAdrLBName());
//                            birth.getParentAddress().setPresentInsideKeralaLocalityNameEn(birth.getParentAddress().getLocalityEnPresent());
//                            birth.getParentAddress().setPresentInsideKeralaLocalityNameMl(birth.getParentAddress().getLocalityMlPresent());
//
//                            birth.getParentAddress().setPresentInsideKeralaStreetNameEn(birth.getParentAddress().getPresentInsideKeralaStreetNameEn());
//                            birth.getParentAddress().setPresentInsideKeralaStreetNameMl(birth.getParentAddress().getPresentInsideKeralaStreetNameMl());
//
//                            birth.getParentAddress().setPresentInsideKeralaHouseNameEn(birth.getParentAddress().getHouseNameNoEnPresent());
//                            birth.getParentAddress().setPresentInsideKeralaHouseNameMl(birth.getParentAddress().getHouseNameNoMlPresent());
//
//                            birth.getParentAddress().setPresentInsideKeralaPincode(birth.getParentAddress().getPinNoPresent());
//
//
//                            birth.getParentAddress().setPresentInsideKeralaPostOffice(birth.getParentAddress().getPresentInsideKeralaPostOffice());
//
//                        } else {
//                            birth.getParentAddress().setPresentaddressCountry(birth.getParentAddress().getCountryIdPresent());
//
//                            birth.getParentAddress().setPresentaddressStateName(birth.getParentAddress().getStateIdPresent());
//
//                            birth.getParentAddress().setPresentOutsideKeralaDistrict(birth.getParentAddress().getDistrictIdPresent());
//
//                            birth.getParentAddress().setPresentOutsideKeralaVillageName(birth.getParentAddress().getVillageNamePresent());
//
//                            birth.getParentAddress().setPresentOutsideKeralaPincode(birth.getParentAddress().getPinNoPresent());
//
//                            birth.getParentAddress().setPresentOutsideKeralaLocalityNameEn(birth.getParentAddress().getLocalityEnPresent());
//                            birth.getParentAddress().setPresentOutsideKeralaLocalityNameMl(birth.getParentAddress().getLocalityMlPresent());
//
//                            birth.getParentAddress().setPresentOutsideKeralaStreetNameEn(birth.getParentAddress().getStreetNameEnPresent());
//                            birth.getParentAddress().setPresentOutsideKeralaStreetNameMl(birth.getParentAddress().getStreetNameMlPresent());
//
//                            birth.getParentAddress().setPresentOutsideKeralaHouseNameEn(birth.getParentAddress().getHouseNameNoEnPresent());
//                            birth.getParentAddress().setPresentOutsideKeralaHouseNameMl(birth.getParentAddress().getHouseNameNoMlPresent());
//
//                            birth.getParentAddress().setPresentOutsideKeralaCityVilgeEn(birth.getParentAddress().getTownOrVillagePresent());
//
//                        }
//                    } else {
//                        birth.getParentAddress().setPresentOutSideCountry(birth.getParentAddress().getCountryIdPresent());
//                        birth.getParentAddress().setPresentOutSideIndiaadrsVillage(birth.getParentAddress().getVillageNamePresent());
//                        birth.getParentAddress().setPresentOutSideIndiaadrsCityTown(birth.getParentAddress().getTownOrVillagePresent());
//                    }
//                }
//            });
//        }
//
//      
//    }
}

