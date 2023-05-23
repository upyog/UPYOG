package org.ksmart.birth.birthnac.repository;

import lombok.extern.slf4j.Slf4j;

import org.egov.common.contract.request.RequestInfo;
import org.ksmart.birth.birthcommon.model.common.CommonPay;
import org.ksmart.birth.birthnac.enrichment.NacEnrichment;
import org.ksmart.birth.birthnac.enrichment.NacResponseEnrichment;
import org.ksmart.birth.birthnac.repository.querybuilder.NacQueryBuilder;
import org.ksmart.birth.birthnac.repository.rowmapper.NacApplicationRowMapper;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.birthnacregistry.model.RegisterNac;
import org.ksmart.birth.birthnacregistry.model.RegisterNacRequest;
import org.ksmart.birth.birthregistry.service.MdmsDataService;
import org.ksmart.birth.common.producer.BndProducer;
import org.ksmart.birth.config.BirthConfiguration;
import org.ksmart.birth.newbirth.enrichment.NewBirthResponseEnrichment;
import org.ksmart.birth.utils.BirthConstants;
import org.ksmart.birth.utils.MdmsUtil;
import org.ksmart.birth.web.model.SearchCriteria;
import org.ksmart.birth.web.model.adoption.AdoptionDetailRequest;
import org.ksmart.birth.web.model.birthnac.NacApplication; 
import org.ksmart.birth.web.model.birthnac.NacDetailRequest;
import org.ksmart.birth.web.model.birthnac.NacSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.ksmart.birth.birthnacregistry.repository.rowmapperfornewapplicationnac.RegisterRowMapperForNacApp;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class NacRepository {
    private final BndProducer producer;
    private final NacEnrichment adoptionEnrichment;
    private final BirthConfiguration birthDeathConfiguration;
    private final JdbcTemplate jdbcTemplate;
 
    private final NacQueryBuilder nacQueryBuilder;
    private final NacApplicationRowMapper nacApplicationRowMapper;
 
    private final  MdmsDataService mdmsDataService;
    private final  MdmsUtil mdmsUtil;
    private final RegisterRowMapperForNacApp registerRowMapperForApp;
    
    private final NacResponseEnrichment responseEnrichment;



    @Autowired
    NacRepository(JdbcTemplate jdbcTemplate, NacEnrichment adoptionEnrichment, BirthConfiguration birthDeathConfiguration,
                  BndProducer producer, NacQueryBuilder nacQueryBuilder, NacApplicationRowMapper nacApplicationRowMapper,
                  MdmsDataService mdmsDataService, MdmsUtil mdmsUtil,RegisterRowMapperForNacApp registerRowMapperForApp,
                  NacResponseEnrichment responseEnrichment) {
        this.jdbcTemplate = jdbcTemplate;
        this.adoptionEnrichment = adoptionEnrichment;
        this.birthDeathConfiguration = birthDeathConfiguration;
        this.producer = producer;
        this.nacQueryBuilder = nacQueryBuilder;
        this.nacApplicationRowMapper = nacApplicationRowMapper;
        this.mdmsDataService = mdmsDataService;
        this.mdmsUtil = mdmsUtil;
        this.registerRowMapperForApp = registerRowMapperForApp;
        this.responseEnrichment =responseEnrichment;
    }

    public List<NacApplication> saveNacDetails(NacDetailRequest request) {
    	adoptionEnrichment.enrichCreate(request);
        producer.push(birthDeathConfiguration.getSaveBirthNacTopic(), request);
        
        return request.getNacDetails();
    }

    public List<NacApplication> updateKsmartBirthDetails(NacDetailRequest request) {
    	adoptionEnrichment.enrichUpdate(request);
        producer.push(birthDeathConfiguration.getUpdateBirthNacTopic(), request);
        return request.getNacDetails();
    }

    public RegisterNacRequest searchNacDetailsForRegister(NacDetailRequest requestApplication) {
        List<Object> preparedStmtValues = new ArrayList<>();
        SearchCriteria criteria = new SearchCriteria();
        List<RegisterNac> result = null;
 
       
        if (requestApplication.getNacDetails().size() > 0) {
 
        	criteria.setId(requestApplication.getNacDetails().get(0).getId());     
            criteria.setTenantId(requestApplication.getNacDetails().get(0).getTenantId());
            String query = nacQueryBuilder.getApplicationSearchQueryForRegistry(criteria, preparedStmtValues);
            result = jdbcTemplate.query(query, preparedStmtValues.toArray(), registerRowMapperForApp);            
            result.get(0).setIsBirthNAC(requestApplication.getNacDetails().get(0).getIsBirthNAC());
            result.get(0).setIsBirthNIA(requestApplication.getNacDetails().get(0).getIsBirthNIA()); 
        }       
      
        return RegisterNacRequest.builder()
                .requestInfo(requestApplication.getRequestInfo())
                .registernacDetails(result).build();       
        
    }
    
    
    public List<NacApplication> searchNacDetails(NacDetailRequest request, SearchCriteria criteria) {
        List<Object> preparedStmtValues = new ArrayList<>();
        criteria.setApplicationType(BirthConstants.FUN_MODULE_NAC);
        String query = nacQueryBuilder.getNacSearchQuery(criteria, request, preparedStmtValues, Boolean.FALSE);
        List<NacApplication> result = jdbcTemplate.query(query, preparedStmtValues.toArray(), nacApplicationRowMapper);
        
        RequestInfo requestInfo = request.getRequestInfo();
        responseEnrichment.setNacRequestData(requestInfo, result);
        return result;
    }
        
//        
//        result.forEach(birth -> {
//            if(birth.getPlaceofBirthId()!=null){
//                Object mdmsData = mdmsUtil.mdmsCallForLocation(request.getRequestInfo(), birth.getTenantId());
//               
//            }
//            if (birth.getParentAddress().getCountryIdPermanent() != null && birth.getParentAddress().getStateIdPermanent() != null) {
//                if (birth.getParentAddress().getCountryIdPermanent().contains(BirthConstants.COUNTRY_CODE)) {
//                    if (birth.getParentAddress().getStateIdPermanent().contains(BirthConstants.STATE_CODE_SMALL)) {
//                        birth.getParentAddress().setPermtaddressCountry(birth.getParentAddress().getCountryIdPermanent());
//
//                        birth.getParentAddress().setPermtaddressStateName(birth.getParentAddress().getStateIdPermanent());
//
//                        birth.getParentAddress().setPermntInKeralaAdrDistrict(birth.getParentAddress().getDistrictIdPermanent());
//
//                        birth.getParentAddress().setPermntInKeralaAdrVillage(birth.getParentAddress().getPermntInKeralaAdrVillage());
//
//                        birth.getParentAddress().setPermntInKeralaAdrLocalityNameEn(birth.getParentAddress().getLocalityEnPermanent());
//                        birth.getParentAddress().setPermntInKeralaAdrLocalityNameMl(birth.getParentAddress().getLocalityMlPermanent());
//
//                        birth.getParentAddress().setPermntInKeralaAdrStreetNameEn(birth.getParentAddress().getStreetNameEnPermanent());
//                        birth.getParentAddress().setPermntInKeralaAdrStreetNameMl(birth.getParentAddress().getStreetNameMlPermanent());
//
//                        birth.getParentAddress().setPermntInKeralaAdrHouseNameEn(birth.getParentAddress().getHouseNameNoEnPermanent());
//                        birth.getParentAddress().setPermntInKeralaAdrHouseNameMl(birth.getParentAddress().getHouseNameNoMlPermanent());
//
//                        birth.getParentAddress().setPermntInKeralaAdrPincode(birth.getParentAddress().getPinNoPermanent());
//
//                        birth.getParentAddress().setPermntInKeralaAdrPostOffice(birth.getParentAddress().getPermntInKeralaAdrPostOffice());
//
//                    }
//                    else{
//                        birth.getParentAddress().setPermntOutsideKeralaDistrict(birth.getParentAddress().getDistrictIdPermanent());
//
//                        birth.getParentAddress().setPermntOutsideKeralaVillage(birth.getParentAddress().getPermntOutsideKeralaVillage());
//
//                        birth.getParentAddress().setPermntOutsideKeralaPincode(birth.getParentAddress().getPinNoPermanent());
//                        
//                        birth.getParentAddress().setPermntOutsideKeralaPostOfficeEn(birth.getParentAddress().getPermntOutsideKeralaPostOfficeEn());
//
//                        birth.getParentAddress().setPermntOutsideKeralaLocalityNameEn(birth.getParentAddress().getLocalityEnPermanent());
//                        birth.getParentAddress().setPermntOutsideKeralaLocalityNameMl(birth.getParentAddress().getLocalityMlPermanent());
//
//                        birth.getParentAddress().setPermntOutsideKeralaStreetNameEn(birth.getParentAddress().getStreetNameEnPermanent());
//                        birth.getParentAddress().setPermntOutsideKeralaStreetNameMl(birth.getParentAddress().getStreetNameMlPermanent());
//
//                        birth.getParentAddress().setPermntOutsideKeralaHouseNameEn(birth.getParentAddress().getHouseNameNoEnPermanent());
//                        birth.getParentAddress().setPermntOutsideKeralaHouseNameMl(birth.getParentAddress().getHouseNameNoMlPermanent());
//
//
//
//                    }
//                }
//            }
//            if(birth.getParentAddress().getCountryIdPresent()!=null && birth.getParentAddress().getStateIdPresent()!=null) {
//                if (birth.getParentAddress().getCountryIdPresent().contains(BirthConstants.COUNTRY_CODE)) {
//                    if (birth.getParentAddress().getStateIdPresent().contains(BirthConstants.STATE_CODE_SMALL)) {
//
//                        birth.getParentAddress().setPresentaddressCountry(birth.getParentAddress().getCountryIdPresent());
//
//                        birth.getParentAddress().setPresentaddressStateName(birth.getParentAddress().getStateIdPresent());
//
//                        birth.getParentAddress().setPresentaddressCountry(birth.getParentAddress().getDistrictIdPresent());
//
//                        birth.getParentAddress().setPresentInsideKeralaDistrict(birth.getParentAddress().getDistrictIdPresent());
//
//                        birth.getParentAddress().setPresentInsideKeralaVillage(birth.getParentAddress().getVillageNamePresent());
//
//                        birth.getParentAddress().setPresentInsideKeralaLocalityNameEn(birth.getParentAddress().getLocalityEnPresent());
//                        birth.getParentAddress().setPresentInsideKeralaLocalityNameMl(birth.getParentAddress().getLocalityMlPresent());
//
//                        birth.getParentAddress().setPresentInsideKeralaStreetNameEn(birth.getParentAddress().getStreetNameEnPermanent());
//                        birth.getParentAddress().setPresentInsideKeralaStreetNameMl(birth.getParentAddress().getStreetNameMlPermanent());
//
//                        birth.getParentAddress().setPresentInsideKeralaHouseNameEn(birth.getParentAddress().getHouseNameNoEnPresent());
//                        birth.getParentAddress().setPresentInsideKeralaHouseNameMl(birth.getParentAddress().getHouseNameNoMlPresent());
//
//                        birth.getParentAddress().setPresentInsideKeralaPincode(birth.getParentAddress().getPinNoPresent());
//
////                        birth.getParentAddress().setPresentInsideKeralaPostOffice(birth.getParentAddress().getPoNoPresent());
//                        
//                        birth.getParentAddress().setPresentInsideKeralaPostOffice(birth.getParentAddress().getPresentInsideKeralaPostOffice());
//
//                    }
//
//                    else{
//                        birth.getParentAddress().setPresentOutsideKeralaDistrict(birth.getParentAddress().getDistrictIdPresent());
//
//                        birth.getParentAddress().setPresentOutsideKeralaVillageName(birth.getParentAddress().getVillageNamePresent());
//
//                        birth.getParentAddress().setPresentOutsideKeralaPincode(birth.getParentAddress().getPinNoPresent());
//
//                        birth.getParentAddress().setPresentOutsideKeralaLocalityNameEn(birth.getParentAddress().getLocalityEnPresent());
//                        birth.getParentAddress().setPresentOutsideKeralaLocalityNameMl(birth.getParentAddress().getLocalityMlPresent());
//
//                        birth.getParentAddress().setPresentOutsideKeralaStreetNameEn(birth.getParentAddress().getStreetNameEnPresent());
//                        birth.getParentAddress().setPresentOutsideKeralaStreetNameMl(birth.getParentAddress().getStreetNameMlPresent());
//
//                        birth.getParentAddress().setPresentOutsideKeralaHouseNameEn(birth.getParentAddress().getHouseNameNoEnPresent());
//                        birth.getParentAddress().setPresentOutsideKeralaHouseNameMl(birth.getParentAddress().getHouseNameNoMlPresent());
//
//                    }
//                }
//            }
//        });

//        return result;
    

}

