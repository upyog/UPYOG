package org.ksmart.birth.correction.repository;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetailsRequest;
import org.ksmart.birth.birthregistry.repository.rowmapperfornewapplication.RegisterRowMapperForApp;
import org.ksmart.birth.common.producer.BndProducer;
import org.ksmart.birth.common.repository.builder.CommonQueryBuilder;
import org.ksmart.birth.config.BirthConfiguration;
import org.ksmart.birth.correction.enrichment.CorrectionBirthEnrichment;
import org.ksmart.birth.correction.repository.querybuilder.NewCorrectionQueryBuilder;
import org.ksmart.birth.correction.repository.rowmapper.CorrectionApplicationRowMapper;
import org.ksmart.birth.correction.service.MdmsForCorrectionBirthService;
import org.ksmart.birth.utils.BirthConstants;
import org.ksmart.birth.utils.MdmsUtil;
import org.ksmart.birth.utils.ResponseInfoFactory;
import org.ksmart.birth.web.model.SearchCriteria;
import org.ksmart.birth.web.model.correction.CorrectionApplication;
import org.ksmart.birth.web.model.correction.CorrectionRequest;
import org.ksmart.birth.web.model.correction.CorrectionResponse;
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
public class CorrectionBirthRepository {
    @Autowired
    CommonQueryBuilder commonQueryBuilder;
    private final BndProducer producer;
    private final CorrectionBirthEnrichment enrichment;
    private final BirthConfiguration birthDeathConfiguration;
    private final JdbcTemplate jdbcTemplate;
    private final NewCorrectionQueryBuilder queryBuilder;
    private final CorrectionApplicationRowMapper rowMapper;
    private final MdmsForCorrectionBirthService mdmsBirthService;
    private final MdmsUtil mdmsUtil;
    private final RegisterRowMapperForApp registerRowMapperForApp;
    private final ResponseInfoFactory responseInfoFactory;


    @Autowired
    CorrectionBirthRepository(JdbcTemplate jdbcTemplate, CorrectionBirthEnrichment enrichment, BirthConfiguration birthDeathConfiguration,
                              BndProducer producer, NewCorrectionQueryBuilder queryBuilder, CorrectionApplicationRowMapper rowMapper,
                              MdmsForCorrectionBirthService mdmsBirthService, MdmsUtil mdmsUtil, RegisterRowMapperForApp registerRowMapperForApp,
                              ResponseInfoFactory responseInfoFactory) {
        this.jdbcTemplate = jdbcTemplate;
        this.enrichment = enrichment;
        this.birthDeathConfiguration = birthDeathConfiguration;
        this.producer = producer;
        this.queryBuilder = queryBuilder;
        this.rowMapper = rowMapper;
        this.mdmsBirthService = mdmsBirthService;
        this.mdmsUtil = mdmsUtil;
        this.registerRowMapperForApp = registerRowMapperForApp;
        this.responseInfoFactory = responseInfoFactory;
    }

    public List<CorrectionApplication> saveCorrectionBirthDetails(CorrectionRequest request) {
        enrichment.enrichCreate(request);
        producer.push(birthDeathConfiguration.getSaveCorrectionBirthTopic(), request);
        return request.getCorrectionDetails();
    }

    public List<CorrectionApplication> updateKsmartBirthDetails(CorrectionRequest request) {
        enrichment.enrichUpdate(request);
        producer.push(birthDeathConfiguration.getUpdateCorrectionBirthTopic(), request);
        return request.getCorrectionDetails();
    }
    public RegisterBirthDetailsRequest searchBirthDetailsForRegister(CorrectionRequest requestApplication) {
        List<Object> preparedStmtValues = new ArrayList<>();
        SearchCriteria criteria = new SearchCriteria();
        List<RegisterBirthDetail> result = null;
        if (requestApplication.getCorrectionDetails().size() > 0) {
            criteria.setId(requestApplication.getCorrectionDetails().get(0).getId());
            criteria.setTenantId(requestApplication.getCorrectionDetails().get(0).getTenantId());
            String query = queryBuilder.getApplicationSearchQueryForRegistry(criteria, preparedStmtValues);
            result = jdbcTemplate.query(query, preparedStmtValues.toArray(), registerRowMapperForApp);
        }
        return RegisterBirthDetailsRequest.builder()
                .requestInfo(requestApplication.getRequestInfo())
                .registerBirthDetails(result).build();
    }

    public List<CorrectionApplication> searchCorrectionDetails1(CorrectionRequest request, SearchCriteria criteria) {
        List<Object> preparedStmtValues = new ArrayList<>();
        criteria.setApplicationType(BirthConstants.FUN_MODULE_COR);
        String query = queryBuilder.getNewBirthApplicationSearchQuery(criteria, request, preparedStmtValues, Boolean.FALSE);
        List<CorrectionApplication> result = jdbcTemplate.query(query, preparedStmtValues.toArray(), rowMapper);
        return result;
    }


    public CorrectionResponse searchCorrectionDetails(CorrectionRequest request, SearchCriteria criteria) {
        List<Object> preparedStmtValues = new ArrayList<>();
        criteria.setApplicationType(BirthConstants.FUN_MODULE_COR);
        int cnt = commonQueryBuilder.searchBirthCount(criteria,jdbcTemplate);
        if (cnt == 0) {
            return null;
        } else {
            String query = queryBuilder.getNewBirthApplicationSearchQuery(criteria, request, preparedStmtValues, Boolean.FALSE);
            List<CorrectionApplication> result = jdbcTemplate.query(query, preparedStmtValues.toArray(), rowMapper);

            return CorrectionResponse.builder()
                    .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), Boolean.TRUE))
                    .correctionDetails(result)
                    .count(cnt)
                    .build();
        }
    }
}

