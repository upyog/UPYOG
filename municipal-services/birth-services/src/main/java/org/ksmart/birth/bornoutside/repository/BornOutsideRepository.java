package org.ksmart.birth.bornoutside.repository;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birthregistry.service.MdmsDataService;
import org.ksmart.birth.common.producer.BndProducer;
import org.ksmart.birth.config.BirthConfiguration;
import org.ksmart.birth.bornoutside.enrichment.BornOutsideEnrichment;
import org.ksmart.birth.bornoutside.repository.querybuilder.BornOutsideQueryBuilder;
import org.ksmart.birth.bornoutside.repository.rowmapper.BornOutsideApplicationRowMapper;
import org.ksmart.birth.utils.BirthConstants;
import org.ksmart.birth.utils.MdmsUtil;
import org.ksmart.birth.web.model.SearchCriteria;
import org.ksmart.birth.web.model.bornoutside.BornOutsideApplication;
import org.ksmart.birth.web.model.bornoutside.BornOutsideDetailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class BornOutsideRepository {
    private final BndProducer producer;
    private final BornOutsideEnrichment enrichment;
    private final BirthConfiguration birthDeathConfiguration;
    private final JdbcTemplate jdbcTemplate;
    private final BornOutsideQueryBuilder queryBuilder;
    private final BornOutsideApplicationRowMapper rowMapper;
    private final  MdmsDataService mdmsDataService;
    private final  MdmsUtil mdmsUtil;



    @Autowired
    BornOutsideRepository(JdbcTemplate jdbcTemplate, BornOutsideEnrichment enrichment, BirthConfiguration birthDeathConfiguration,
                          BndProducer producer, BornOutsideQueryBuilder queryBuilder, BornOutsideApplicationRowMapper rowMapper,
                          MdmsDataService mdmsDataService, MdmsUtil mdmsUtil) {
        this.jdbcTemplate = jdbcTemplate;
        this.enrichment = enrichment;
        this.birthDeathConfiguration = birthDeathConfiguration;
        this.producer = producer;
        this.queryBuilder = queryBuilder;
        this.rowMapper = rowMapper;
        this.mdmsDataService = mdmsDataService;
        this.mdmsUtil = mdmsUtil;
    }

    public List<BornOutsideApplication> saveBirthApplication(BornOutsideDetailRequest request) {
        enrichment.enrichCreate(request);
        producer.push(birthDeathConfiguration.getSaveBornOutsideTopic(), request);
        return request.getNewBirthDetails();
    }

    public List<BornOutsideApplication> updateBirthApplication(BornOutsideDetailRequest request) {
        enrichment.enrichUpdate(request);
        producer.push(birthDeathConfiguration.getUpdateBornOutsideTopic(), request);
        return request.getNewBirthDetails();
    }


    public List<BornOutsideApplication> searchKsmartBirthDetails(BornOutsideDetailRequest request, SearchCriteria criteria) {
        List<Object> preparedStmtValues = new ArrayList<>();
        String query = queryBuilder.getNewBirthApplicationSearchQuery(criteria, request,preparedStmtValues, Boolean.FALSE);
        List<BornOutsideApplication> result = jdbcTemplate.query(query, preparedStmtValues.toArray(), rowMapper);
        result.forEach(birth -> {
            if(birth.getPlaceofBirthId()!=null){
                Object mdmsData = mdmsUtil.mdmsCallForLocation(request.getRequestInfo(), birth.getTenantId());
                //mdmsDataService.setKsmartLocationDetails(birth, mdmsData);
            }
            if (birth.getParentAddress().getCountryIdPermanent() != null && birth.getParentAddress().getStateIdPermanent() != null) {
                if (birth.getParentAddress().getCountryIdPermanent().contains(BirthConstants.COUNTRY_CODE)) {
                    if (birth.getParentAddress().getStateIdPermanent().contains(BirthConstants.STATE_CODE_SMALL)) {
                        birth.getParentAddress().setPermtaddressCountry(birth.getParentAddress().getCountryIdPermanent());

                        birth.getParentAddress().setPermtaddressStateName(birth.getParentAddress().getStateIdPermanent());

                        birth.getParentAddress().setPermntInKeralaAdrDistrict(birth.getParentAddress().getDistrictIdPermanent());

                        birth.getParentAddress().setPermntInKeralaAdrVillage(birth.getParentAddress().getVillageNamePermanent());

                        birth.getParentAddress().setPermntInKeralaAdrLocalityNameEn(birth.getParentAddress().getLocalityEnPermanent());
                        birth.getParentAddress().setPermntInKeralaAdrLocalityNameMl(birth.getParentAddress().getLocalityMlPermanent());

                        birth.getParentAddress().setPermntInKeralaAdrStreetNameEn(birth.getParentAddress().getStreetNameEnPermanent());
                        birth.getParentAddress().setPermntInKeralaAdrStreetNameMl(birth.getParentAddress().getStreetNameMlPermanent());

                        birth.getParentAddress().setPermntInKeralaAdrHouseNameEn(birth.getParentAddress().getHouseNameNoEnPermanent());
                        birth.getParentAddress().setPermntInKeralaAdrHouseNameMl(birth.getParentAddress().getHouseNameNoMlPermanent());

                        birth.getParentAddress().setPermntInKeralaAdrPincode(birth.getParentAddress().getPinNoPermanent());

                        birth.getParentAddress().setPermntInKeralaAdrPostOffice(birth.getParentAddress().getPoNoPermanent());

                    }
                    else{
                        birth.getParentAddress().setPermntOutsideKeralaDistrict(birth.getParentAddress().getDistrictIdPermanent());

                        birth.getParentAddress().setPermntOutsideKeralaVillage(birth.getParentAddress().getVillageNamePermanent());

                        birth.getParentAddress().setPermntOutsideKeralaPincode(birth.getParentAddress().getPinNoPermanent());

                        birth.getParentAddress().setPermntOutsideKeralaLocalityNameEn(birth.getParentAddress().getLocalityEnPermanent());
                        birth.getParentAddress().setPermntOutsideKeralaLocalityNameMl(birth.getParentAddress().getLocalityMlPermanent());

                        birth.getParentAddress().setPermntOutsideKeralaStreetNameEn(birth.getParentAddress().getStreetNameEnPermanent());
                        birth.getParentAddress().setPermntOutsideKeralaStreetNameMl(birth.getParentAddress().getStreetNameMlPermanent());

                        birth.getParentAddress().setPermntOutsideKeralaHouseNameEn(birth.getParentAddress().getHouseNameNoEnPermanent());
                        birth.getParentAddress().setPermntOutsideKeralaHouseNameMl(birth.getParentAddress().getHouseNameNoMlPermanent());



                    }
                }
            }
            if(birth.getParentAddress().getCountryIdPresent()!=null && birth.getParentAddress().getStateIdPresent()!=null) {
                if (birth.getParentAddress().getCountryIdPresent().contains(BirthConstants.COUNTRY_CODE)) {
                    if (birth.getParentAddress().getStateIdPresent().contains(BirthConstants.STATE_CODE_SMALL)) {

                        birth.getParentAddress().setPresentaddressCountry(birth.getParentAddress().getCountryIdPresent());

                        birth.getParentAddress().setPresentaddressStateName(birth.getParentAddress().getStateIdPresent());

                        birth.getParentAddress().setPresentaddressCountry(birth.getParentAddress().getDistrictIdPresent());

                        birth.getParentAddress().setPresentInsideKeralaDistrict(birth.getParentAddress().getDistrictIdPresent());

                        birth.getParentAddress().setPresentInsideKeralaVillage(birth.getParentAddress().getVillageNamePresent());

                        birth.getParentAddress().setPresentInsideKeralaLocalityNameEn(birth.getParentAddress().getLocalityEnPresent());
                        birth.getParentAddress().setPresentInsideKeralaLocalityNameMl(birth.getParentAddress().getLocalityMlPresent());

                        birth.getParentAddress().setPresentInsideKeralaStreetNameEn(birth.getParentAddress().getStreetNameEnPermanent());
                        birth.getParentAddress().setPresentInsideKeralaStreetNameMl(birth.getParentAddress().getStreetNameMlPermanent());

                        birth.getParentAddress().setPresentInsideKeralaHouseNameEn(birth.getParentAddress().getHouseNameNoEnPresent());
                        birth.getParentAddress().setPresentInsideKeralaHouseNameMl(birth.getParentAddress().getHouseNameNoMlPresent());

                        birth.getParentAddress().setPresentInsideKeralaPincode(birth.getParentAddress().getPinNoPresent());

                        birth.getParentAddress().setPresentInsideKeralaPostOffice(birth.getParentAddress().getPoNoPresent());

                    }

                    else{
                        birth.getParentAddress().setPresentOutsideKeralaDistrict(birth.getParentAddress().getDistrictIdPresent());

                        birth.getParentAddress().setPresentOutsideKeralaVillageName(birth.getParentAddress().getVillageNamePresent());

                        birth.getParentAddress().setPresentOutsideKeralaPincode(birth.getParentAddress().getPinNoPresent());

                        birth.getParentAddress().setPresentOutsideKeralaLocalityNameEn(birth.getParentAddress().getLocalityEnPresent());
                        birth.getParentAddress().setPresentOutsideKeralaLocalityNameMl(birth.getParentAddress().getLocalityMlPresent());

                        birth.getParentAddress().setPresentOutsideKeralaStreetNameEn(birth.getParentAddress().getStreetNameEnPresent());
                        birth.getParentAddress().setPresentOutsideKeralaStreetNameMl(birth.getParentAddress().getStreetNameMlPresent());

                        birth.getParentAddress().setPresentOutsideKeralaHouseNameEn(birth.getParentAddress().getHouseNameNoEnPresent());
                        birth.getParentAddress().setPresentOutsideKeralaHouseNameMl(birth.getParentAddress().getHouseNameNoMlPresent());

                    }
                }
            }
        });

        return result;
    }
}

