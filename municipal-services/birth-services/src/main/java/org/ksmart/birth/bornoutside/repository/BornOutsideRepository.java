package org.ksmart.birth.bornoutside.repository;

import lombok.extern.slf4j.Slf4j;
import org.egov.tracer.model.CustomException;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetailsRequest;
import org.ksmart.birth.birthregistry.repository.rowmapperfornewapplication.RegisterRowMapperForApp;
import org.ksmart.birth.birthregistry.service.MdmsDataService;
import org.ksmart.birth.bornoutside.service.MdmsForBornOutsideService;
import org.ksmart.birth.common.producer.BndProducer;
import org.ksmart.birth.common.repository.builder.CommonQueryBuilder;
import org.ksmart.birth.config.BirthConfiguration;
import org.ksmart.birth.bornoutside.enrichment.BornOutsideEnrichment;
import org.ksmart.birth.bornoutside.repository.querybuilder.BornOutsideQueryBuilder;
import org.ksmart.birth.bornoutside.repository.rowmapper.BornOutsideApplicationRowMapper;
import org.ksmart.birth.utils.BirthConstants;
import org.ksmart.birth.utils.MdmsUtil;
import org.ksmart.birth.utils.enums.ErrorCodes;
import org.ksmart.birth.web.model.SearchCriteria;
import org.ksmart.birth.web.model.bornoutside.BornOutsideApplication;
import org.ksmart.birth.web.model.bornoutside.BornOutsideDetailRequest;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
import org.ksmart.birth.web.model.newbirth.NewBirthDetailRequest;
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
    private final MdmsForBornOutsideService mdmsDataService;
    private final MdmsUtil mdmsUtil;
    private final CommonQueryBuilder commonQueryBuilder;
    private final RegisterRowMapperForApp registerRowMapperForApp;


    @Autowired
    BornOutsideRepository(JdbcTemplate jdbcTemplate, BornOutsideEnrichment enrichment, BirthConfiguration birthDeathConfiguration,
                          BndProducer producer, BornOutsideQueryBuilder queryBuilder, BornOutsideApplicationRowMapper rowMapper,
                          MdmsForBornOutsideService mdmsDataService, MdmsUtil mdmsUtil, CommonQueryBuilder commonQueryBuilder,
                          RegisterRowMapperForApp registerRowMapperForApp) {
        this.jdbcTemplate = jdbcTemplate;
        this.enrichment = enrichment;
        this.birthDeathConfiguration = birthDeathConfiguration;
        this.producer = producer;
        this.queryBuilder = queryBuilder;
        this.rowMapper = rowMapper;
        this.mdmsDataService = mdmsDataService;
        this.mdmsUtil = mdmsUtil;
        this.commonQueryBuilder = commonQueryBuilder;
        this.registerRowMapperForApp = registerRowMapperForApp;
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


    public RegisterBirthDetailsRequest searchBirthDetailsForRegister(BornOutsideDetailRequest requestApplication) {
        List<Object> preparedStmtValues = new ArrayList<>();
        SearchCriteria criteria = new SearchCriteria();
        List<RegisterBirthDetail> result = null;
        if (requestApplication.getNewBirthDetails().size() > 0) {
            criteria.setId(requestApplication.getNewBirthDetails().get(0).getId());
            criteria.setTenantId(requestApplication.getNewBirthDetails().get(0).getTenantId());
            String query = commonQueryBuilder.getApplicationSearchQueryForRegistry(criteria, preparedStmtValues);
            result = jdbcTemplate.query(query, preparedStmtValues.toArray(), registerRowMapperForApp);
        }
        return RegisterBirthDetailsRequest.builder()
                .requestInfo(requestApplication.getRequestInfo())
                .registerBirthDetails(result).build();
    }


    public List<BornOutsideApplication> searchBirthDetails(BornOutsideDetailRequest request, SearchCriteria criteria) {
        String uuid = null;
        List<Object> preparedStmtValues = new ArrayList<>();
        Object mdmsDataComm = mdmsUtil.mdmsCall(request.getRequestInfo());
        if (request.getRequestInfo().getUserInfo() != null) {
            uuid = request.getRequestInfo().getUserInfo().getUuid();
        } else {
            criteria.setApplicationType(BirthConstants.FUN_MODULE_NEW);
        }
        String query = commonQueryBuilder.getBirthApplicationSearchQuery(criteria, uuid, preparedStmtValues, Boolean.FALSE);
        if (preparedStmtValues.size() == 0) {
            throw new CustomException(ErrorCodes.NOT_FOUND.getCode(), "No result found.");
        } else {
            List<BornOutsideApplication> result = jdbcTemplate.query(query, preparedStmtValues.toArray(), rowMapper);

            if (result.size() == 0) {
                throw new CustomException(ErrorCodes.NOT_FOUND.getCode(), "No result found.");
            } else if (result.size() >= 1) {
                result.forEach(birth -> {
                    birth.setIsWorkflow(true);
                    if (birth.getParentAddress().getCountryIdPermanent() != null && birth.getParentAddress().getStateIdPermanent() != null) {
                        if (birth.getParentAddress().getCountryIdPermanent().contains(BirthConstants.COUNTRY_CODE)) {
                            if (birth.getParentAddress().getStateIdPermanent().contains(BirthConstants.STATE_CODE_SMALL)) {
                                mdmsDataService.setTenantDetails(birth, mdmsDataComm);
                                birth.getParentAddress().setPermtaddressCountry(birth.getParentAddress().getCountryIdPermanent());

                                birth.getParentAddress().setPermtaddressStateName(birth.getParentAddress().getStateIdPermanent());

                                birth.getParentAddress().setPermntInKeralaAdrDistrict(birth.getParentAddress().getDistrictIdPermanent());

                                birth.getParentAddress().setPermntInKeralaAdrLocalityNameEn(birth.getParentAddress().getLocalityEnPermanent());
                                birth.getParentAddress().setPermntInKeralaAdrLocalityNameMl(birth.getParentAddress().getLocalityMlPermanent());

                                birth.getParentAddress().setPermntInKeralaAdrStreetNameEn(birth.getParentAddress().getStreetNameEnPermanent());
                                birth.getParentAddress().setPermntInKeralaAdrStreetNameMl(birth.getParentAddress().getStreetNameMlPermanent());

                                birth.getParentAddress().setPermntInKeralaAdrHouseNameEn(birth.getParentAddress().getHouseNameNoEnPermanent());
                                birth.getParentAddress().setPermntInKeralaAdrHouseNameMl(birth.getParentAddress().getHouseNameNoMlPermanent());

                                birth.getParentAddress().setPermntInKeralaAdrPostOffice(birth.getParentAddress().getPoNoPermanent());

                            } else {
                                birth.getParentAddress().setPermtaddressCountry(birth.getParentAddress().getCountryIdPermanent());

                                birth.getParentAddress().setPermtaddressStateName(birth.getParentAddress().getStateIdPermanent());
                                birth.getParentAddress().setPermntOutsideKeralaDistrict(birth.getParentAddress().getDistrictIdPermanent());

                                birth.getParentAddress().setPermntOutsideKeralaVillage(birth.getParentAddress().getVillageNamePermanent());

                                birth.getParentAddress().setPermntOutsideKeralaLocalityNameEn(birth.getParentAddress().getLocalityEnPermanent());
                                birth.getParentAddress().setPermntOutsideKeralaLocalityNameMl(birth.getParentAddress().getLocalityMlPermanent());

                                birth.getParentAddress().setPermntOutsideKeralaStreetNameEn(birth.getParentAddress().getStreetNameEnPermanent());
                                birth.getParentAddress().setPermntOutsideKeralaStreetNameMl(birth.getParentAddress().getStreetNameMlPermanent());

                                birth.getParentAddress().setPermntOutsideKeralaHouseNameEn(birth.getParentAddress().getHouseNameNoEnPermanent());
                                birth.getParentAddress().setPermntOutsideKeralaHouseNameMl(birth.getParentAddress().getHouseNameNoMlPermanent());

                            }
                        } else {
                            birth.getParentAddress().setPermntOutsideIndiaCountry(birth.getParentAddress().getCountryIdPermanent());
                            birth.getParentAddress().setPermntOutsideKeralaVillage(birth.getParentAddress().getVillageNamePermanent());
                        }
                    }
                    if (birth.getParentAddress().getCountryIdPresent() != null && birth.getParentAddress().getStateIdPresent() != null) {
                        if (birth.getParentAddress().getCountryIdPresent().contains(BirthConstants.COUNTRY_CODE)) {
                            if (birth.getParentAddress().getStateIdPresent().contains(BirthConstants.STATE_CODE_SMALL)) {

                                birth.getParentAddress().setPresentaddressCountry(birth.getParentAddress().getCountryIdPresent());

                                birth.getParentAddress().setPresentaddressStateName(birth.getParentAddress().getStateIdPresent());

                                birth.getParentAddress().setPresentInsideKeralaDistrict(birth.getParentAddress().getDistrictIdPresent());

                                birth.getParentAddress().setPresentInsideKeralaLBName(birth.getParentAddress().getPresentInsideKeralaLBName());
                                birth.getParentAddress().setPresentInsideKeralaLocalityNameEn(birth.getParentAddress().getLocalityEnPresent());
                                birth.getParentAddress().setPresentInsideKeralaLocalityNameMl(birth.getParentAddress().getLocalityMlPresent());

                                birth.getParentAddress().setPresentInsideKeralaStreetNameEn(birth.getParentAddress().getStreetNameEnPermanent());
                                birth.getParentAddress().setPresentInsideKeralaStreetNameMl(birth.getParentAddress().getStreetNameMlPermanent());

                                birth.getParentAddress().setPresentInsideKeralaHouseNameEn(birth.getParentAddress().getHouseNameNoEnPresent());
                                birth.getParentAddress().setPresentInsideKeralaHouseNameMl(birth.getParentAddress().getHouseNameNoMlPresent());

                                birth.getParentAddress().setPresentInsideKeralaPincode(birth.getParentAddress().getPinNoPresent());


                                birth.getParentAddress().setPresentInsideKeralaPostOffice(birth.getParentAddress().getPresentInsideKeralaPostOffice());

                            } else {
                                birth.getParentAddress().setPresentaddressCountry(birth.getParentAddress().getCountryIdPresent());

                                birth.getParentAddress().setPresentaddressStateName(birth.getParentAddress().getStateIdPresent());

                                birth.getParentAddress().setPresentOutsideKeralaDistrict(birth.getParentAddress().getDistrictIdPresent());

                                birth.getParentAddress().setPresentOutsideKeralaVillageName(birth.getParentAddress().getVillageNamePresent());

                                birth.getParentAddress().setPresentOutsideKeralaPincode(birth.getParentAddress().getPinNoPresent());

                                birth.getParentAddress().setPresentOutsideKeralaLocalityNameEn(birth.getParentAddress().getLocalityEnPresent());
                                birth.getParentAddress().setPresentOutsideKeralaLocalityNameMl(birth.getParentAddress().getLocalityMlPresent());

                                birth.getParentAddress().setPresentOutsideKeralaStreetNameEn(birth.getParentAddress().getStreetNameEnPresent());
                                birth.getParentAddress().setPresentOutsideKeralaStreetNameMl(birth.getParentAddress().getStreetNameMlPresent());

                                birth.getParentAddress().setPresentOutsideKeralaHouseNameEn(birth.getParentAddress().getHouseNameNoEnPresent());
                                birth.getParentAddress().setPresentOutsideKeralaHouseNameMl(birth.getParentAddress().getHouseNameNoMlPresent());

                                birth.getParentAddress().setPresentOutsideKeralaCityVilgeEn(birth.getParentAddress().getTownOrVillagePresent());

                            }
                        } else {
                            birth.getParentAddress().setPresentOutSideCountry(birth.getParentAddress().getCountryIdPresent());
                            birth.getParentAddress().setPresentOutSideIndiaadrsVillage(birth.getParentAddress().getVillageNamePresent());
                            birth.getParentAddress().setPresentOutSideIndiaadrsCityTown(birth.getParentAddress().getTownOrVillagePresent());
                        }
                    }
                });
            }

            return result;
        }


    }
}

