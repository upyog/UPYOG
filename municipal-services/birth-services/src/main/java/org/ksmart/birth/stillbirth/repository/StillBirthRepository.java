package org.ksmart.birth.stillbirth.repository;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetailsRequest;
import org.ksmart.birth.birthregistry.repository.rowmapperfornewapplication.RegisterRowMapperForApp;
import org.ksmart.birth.birthregistry.service.MdmsDataService;
import org.ksmart.birth.common.producer.BndProducer;
import org.ksmart.birth.common.repository.builder.CommonQueryBuilder;
import org.ksmart.birth.config.BirthConfiguration;
import org.ksmart.birth.newbirth.enrichment.NewBirthResponseEnrichment;
import org.ksmart.birth.stillbirth.enrichment.StillBirthEnrichment;
import org.ksmart.birth.stillbirth.enrichment.StillBirthResponseEnrichment;
import org.ksmart.birth.stillbirth.repository.querybuilder.StillBirthQueryBuilder;
import org.ksmart.birth.stillbirth.repository.rowmapper.StillBirthApplicationRowMapper;
import org.ksmart.birth.stillbirth.service.MdmsForStillBirthService;
import org.ksmart.birth.utils.BirthConstants;
import org.ksmart.birth.utils.MdmsUtil;
import org.ksmart.birth.utils.ResponseInfoFactory;
import org.ksmart.birth.utils.enums.ErrorCodes;
import org.ksmart.birth.web.model.SearchCriteria;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
import org.ksmart.birth.web.model.newbirth.NewBirthDetailRequest;
import org.ksmart.birth.web.model.newbirth.NewBirthSearchResponse;
import org.ksmart.birth.web.model.stillbirth.StillBirthApplication;
import org.ksmart.birth.web.model.stillbirth.StillBirthDetailRequest;
import org.ksmart.birth.web.model.stillbirth.StillBirthResponse;
import org.ksmart.birth.web.model.stillbirth.StillBirthSearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class StillBirthRepository {

    @Autowired
    CommonQueryBuilder commonQueryBuilder;
    private final BndProducer producer;
    private final StillBirthEnrichment enrichment;
    private final BirthConfiguration birthDeathConfiguration;
    private final JdbcTemplate jdbcTemplate;
    private final StillBirthQueryBuilder queryBuilder;
    private final StillBirthApplicationRowMapper rowMapper;
    private final  MdmsDataService mdmsDataService;
    private final MdmsForStillBirthService mdmsForStillBirthService;
    private final  MdmsUtil mdmsUtil;
    private final StillBirthResponseEnrichment responseEnrichment;
    private final RegisterRowMapperForApp registerRowMapperForApp;
    private final ResponseInfoFactory responseInfoFactory;
    @Autowired
    StillBirthRepository(JdbcTemplate jdbcTemplate,  StillBirthEnrichment enrichment, BirthConfiguration birthDeathConfiguration,
                         BndProducer producer, StillBirthQueryBuilder queryBuilder, StillBirthApplicationRowMapper rowMapper,
                         MdmsDataService mdmsDataService, MdmsUtil mdmsUtil, MdmsForStillBirthService mdmsForStillBirthService,
                         RegisterRowMapperForApp registerRowMapperForApp, StillBirthResponseEnrichment responseEnrichment,
                         ResponseInfoFactory responseInfoFactory) {
        this.jdbcTemplate = jdbcTemplate;
        this.enrichment = enrichment;
        this.birthDeathConfiguration = birthDeathConfiguration;
        this.producer = producer;
        this.queryBuilder = queryBuilder;
        this.rowMapper = rowMapper;
        this.mdmsDataService = mdmsDataService;
        this.mdmsUtil = mdmsUtil;
        this.mdmsForStillBirthService = mdmsForStillBirthService;
        this.registerRowMapperForApp = registerRowMapperForApp;
        this.responseEnrichment = responseEnrichment;
        this.responseInfoFactory = responseInfoFactory;
    }
    public List<StillBirthApplication> saveBirthDetails(StillBirthDetailRequest request, Object mdmsData) {
        enrichment.enrichCreate(request,mdmsData);
        producer.push(birthDeathConfiguration.getSaveStillBirthTopic(), request);
        return request.getBirthDetails();
    }
    public List<StillBirthApplication> updateBirthDetails(StillBirthDetailRequest request, Object mdmsData) {
        enrichment.enrichUpdate(request, mdmsData);
        producer.push(birthDeathConfiguration.getUpdateStillBirthTopic(), request);
        return request.getBirthDetails();
    }

    public RegisterBirthDetailsRequest searchStillBirthDetailsForRegister(StillBirthDetailRequest request) {
        List<Object> preparedStmtValues = new ArrayList<>();
        SearchCriteria criteria = new SearchCriteria();
        List<RegisterBirthDetail> result = null;
        if (request.getBirthDetails().size() > 0) {
            criteria.getApplicationNumber().add(request.getBirthDetails().get(0).getApplicationNo());
            criteria.setTenantId(request.getBirthDetails().get(0).getTenantId());
            String query = queryBuilder.getApplicationSearchQueryForRegistry(criteria, preparedStmtValues);
            result = jdbcTemplate.query(query, preparedStmtValues.toArray(), registerRowMapperForApp);
        }
        return RegisterBirthDetailsRequest.builder()
                .requestInfo(request.getRequestInfo())
                .registerBirthDetails(result).build();
    }
    public List<StillBirthApplication> searchStillBirthDetails1(StillBirthDetailRequest request, SearchCriteria criteria) {
        String uuid = null;
        List<Object> preparedStmtValues = new ArrayList<>();
        criteria.setApplicationType(BirthConstants.FUN_MODULE_STL);
        String query = commonQueryBuilder.getBirthApplicationSearchQuery(criteria, preparedStmtValues, Boolean.FALSE);
        if(preparedStmtValues.size() == 0){
            throw new CustomException(ErrorCodes.NOT_FOUND.getCode(), "No result found.");
        } else{
            List<StillBirthApplication> result = jdbcTemplate.query(query, preparedStmtValues.toArray(), rowMapper);
            RequestInfo requestInfo = request.getRequestInfo();
            responseEnrichment.setNewBirthRequestData(requestInfo, result);
            return result;
        }
    }
    public StillBirthResponse searchBirthDetails(StillBirthDetailRequest request, SearchCriteria criteria) {
        List<Object> preparedStmtValues = new ArrayList<>();
        criteria.setApplicationType(BirthConstants.FUN_MODULE_STL);
        int cnt = commonQueryBuilder.searchBirthCount(criteria,jdbcTemplate);
        if (cnt == 0) {
            return null;
        } else {
            String query = commonQueryBuilder.getBirthApplicationSearchQuery(criteria, preparedStmtValues, Boolean.FALSE);
            List<StillBirthApplication> result = jdbcTemplate.query(query, preparedStmtValues.toArray(), rowMapper);

            RequestInfo requestInfo = request.getRequestInfo();
            responseEnrichment.setNewBirthRequestData(requestInfo, result);
            return StillBirthResponse.builder()
                    .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), Boolean.TRUE))
                    .birthDetails(result)
                    .count(cnt)
                    .build();
        }
    }
}

