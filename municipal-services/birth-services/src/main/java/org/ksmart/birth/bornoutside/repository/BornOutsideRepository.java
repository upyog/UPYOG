package org.ksmart.birth.bornoutside.repository;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetailsRequest;
import org.ksmart.birth.birthregistry.repository.rowmapperfornewapplication.RegisterRowMapperForApp;
import org.ksmart.birth.birthregistry.service.MdmsDataService;
import org.ksmart.birth.bornoutside.enrichment.BornOutsideResponseEnrichment;
import org.ksmart.birth.bornoutside.service.MdmsForBornOutsideService;
import org.ksmart.birth.common.producer.BndProducer;
import org.ksmart.birth.common.repository.builder.CommonQueryBuilder;
import org.ksmart.birth.common.services.MdmsTenantService;
import org.ksmart.birth.config.BirthConfiguration;
import org.ksmart.birth.bornoutside.enrichment.BornOutsideEnrichment;
import org.ksmart.birth.bornoutside.repository.querybuilder.BornOutsideQueryBuilder;
import org.ksmart.birth.bornoutside.repository.rowmapper.BornOutsideApplicationRowMapper;
import org.ksmart.birth.newbirth.enrichment.NewBirthResponseEnrichment;
import org.ksmart.birth.utils.BirthConstants;
import org.ksmart.birth.utils.MdmsUtil;
import org.ksmart.birth.utils.ResponseInfoFactory;
import org.ksmart.birth.utils.enums.ErrorCodes;
import org.ksmart.birth.web.model.SearchCriteria;
import org.ksmart.birth.web.model.bornoutside.BornOutsideApplication;
import org.ksmart.birth.web.model.bornoutside.BornOutsideDetailRequest;
import org.ksmart.birth.web.model.bornoutside.BornOutsideResponse;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
import org.ksmart.birth.web.model.newbirth.NewBirthDetailRequest;
import org.ksmart.birth.web.model.stillbirth.StillBirthApplication;
import org.ksmart.birth.web.model.stillbirth.StillBirthDetailRequest;
import org.ksmart.birth.web.model.stillbirth.StillBirthSearchResponse;
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
    private final BornOutsideResponseEnrichment responseEnrichment;
    private final MdmsTenantService mdmsTenantService;
    private final ResponseInfoFactory responseInfoFactory;


    @Autowired
    BornOutsideRepository(JdbcTemplate jdbcTemplate, BornOutsideEnrichment enrichment, BirthConfiguration birthDeathConfiguration,
                          BndProducer producer, BornOutsideQueryBuilder queryBuilder, BornOutsideApplicationRowMapper rowMapper,
                          MdmsForBornOutsideService mdmsDataService, MdmsUtil mdmsUtil, CommonQueryBuilder commonQueryBuilder,
                          RegisterRowMapperForApp registerRowMapperForApp, MdmsTenantService mdmsTenantService,
                          BornOutsideResponseEnrichment responseEnrichment, ResponseInfoFactory responseInfoFactory) {
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
        this.mdmsTenantService = mdmsTenantService;
        this.responseEnrichment = responseEnrichment;
        this.responseInfoFactory = responseInfoFactory;
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

    public List<BornOutsideApplication> searchBirthDetails1(BornOutsideDetailRequest request, SearchCriteria criteria) {
        String uuid = null;
        List<Object> preparedStmtValues = new ArrayList<>();
//        if (request.getRequestInfo().getUserInfo() != null) {
//            uuid = request.getRequestInfo().getUserInfo().getUuid();
//        }
            criteria.setApplicationType(BirthConstants.FUN_MODULE_OSC);
        String query = commonQueryBuilder.getBirthApplicationSearchQuery(criteria, preparedStmtValues, Boolean.FALSE);
        if (preparedStmtValues.size() == 0) {
            throw new CustomException(ErrorCodes.NOT_FOUND.getCode(), "No result found.");
        } else {
            List<BornOutsideApplication> result = jdbcTemplate.query(query, preparedStmtValues.toArray(), rowMapper);
            RequestInfo requestInfo = request.getRequestInfo();
            responseEnrichment.setNewBirthRequestData(requestInfo, result);
            return result;
        }
    }
    public BornOutsideResponse searchBirthDetails(BornOutsideDetailRequest request, SearchCriteria criteria) {
        List<Object> preparedStmtValues = new ArrayList<>();
        criteria.setApplicationType(BirthConstants.FUN_MODULE_OSC);
        int cnt = commonQueryBuilder.searchBirthCount(criteria,jdbcTemplate);
        if (cnt == 0) {
            return null;
        } else {
            String query = commonQueryBuilder.getBirthApplicationSearchQuery(criteria, preparedStmtValues, Boolean.FALSE);
            List<BornOutsideApplication> result = jdbcTemplate.query(query, preparedStmtValues.toArray(), rowMapper);

            RequestInfo requestInfo = request.getRequestInfo();
            responseEnrichment.setNewBirthRequestData(requestInfo, result);
            return BornOutsideResponse.builder()
                    .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), Boolean.TRUE))
                    .birthDetails(result)
                    .count(cnt)
                    .build();
        }
    }
}

