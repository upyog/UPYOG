package org.ksmart.birth.newbirth.repository;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.ksmart.birth.birthcommon.enrichment.RegisterStatisticsEnrichment;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetailsRequest;
import org.ksmart.birth.birthregistry.repository.rowmapperfornewapplication.RegisterRowMapperForApp;
import org.ksmart.birth.common.producer.BndProducer;
import org.ksmart.birth.common.repository.builder.CommonQueryBuilder;
import org.ksmart.birth.config.BirthConfiguration;
import org.ksmart.birth.newbirth.enrichment.NewBirthEnrichment;
import org.ksmart.birth.newbirth.enrichment.NewBirthResponseEnrichment;
import org.ksmart.birth.newbirth.repository.rowmapper.BirthApplicationRowMapper;
import org.ksmart.birth.newbirth.repository.rowmapper.BirthApplicationRowMapperForPay;
import org.ksmart.birth.utils.BirthConstants;
import org.ksmart.birth.utils.ResponseInfoFactory;
import org.ksmart.birth.utils.enums.ErrorCodes;
import org.ksmart.birth.web.model.SearchCriteria;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
import org.ksmart.birth.web.model.newbirth.NewBirthDetailRequest;
import org.ksmart.birth.web.model.newbirth.NewBirthSearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class NewBirthRepository {
    @Autowired
    CommonQueryBuilder commonQueryBuilder;
    private final BndProducer producer;
    private final NewBirthEnrichment ksmartBirthEnrichment;
    private final NewBirthResponseEnrichment responseEnrichment;
    private final BirthConfiguration birthDeathConfiguration;
    private final JdbcTemplate jdbcTemplate;
    private final BirthApplicationRowMapper ksmartBirthApplicationRowMapper;
    private final BirthApplicationRowMapperForPay ksmartBirthApplicationRowMapperPay;
    private final RegisterRowMapperForApp registerRowMapperForApp;
    private final RegisterStatisticsEnrichment registerStatisticsEnrichment;
    private final ResponseInfoFactory responseInfoFactory;

    @Autowired
    NewBirthRepository(JdbcTemplate jdbcTemplate, NewBirthEnrichment ksmartBirthEnrichment, BirthConfiguration birthDeathConfiguration,
                       BndProducer producer, NewBirthResponseEnrichment responseEnrichment, BirthApplicationRowMapperForPay ksmartBirthApplicationRowMapperPay,
                       BirthApplicationRowMapper ksmartBirthApplicationRowMapper, RegisterRowMapperForApp registerRowMapperForApp,
                       RegisterStatisticsEnrichment registerStatisticsEnrichment, ResponseInfoFactory responseInfoFactory) {
        this.jdbcTemplate = jdbcTemplate;
        this.ksmartBirthEnrichment = ksmartBirthEnrichment;
        this.birthDeathConfiguration = birthDeathConfiguration;
        this.producer = producer;
        this.ksmartBirthApplicationRowMapper = ksmartBirthApplicationRowMapper;
        this.responseEnrichment = responseEnrichment;
        this.registerRowMapperForApp = registerRowMapperForApp;
        this.ksmartBirthApplicationRowMapperPay = ksmartBirthApplicationRowMapperPay;
        this.registerStatisticsEnrichment = registerStatisticsEnrichment;
        this.responseInfoFactory = responseInfoFactory;
    }

    public List<NewBirthApplication> saveKsmartBirthDetails(NewBirthDetailRequest request, Object mdmsData) {
//        ksmartBirthEnrichment.enrichCreate(request, mdmsData);
        producer.push(birthDeathConfiguration.getSaveKsmartBirthApplicationTopic(), request);
        return request.getNewBirthDetails();
    }

    public List<NewBirthApplication> updateKsmartBirthDetails(NewBirthDetailRequest request, Object mdmsData) {
//        ksmartBirthEnrichment.enrichUpdate(request, mdmsData);
        producer.push(birthDeathConfiguration.getUpdateKsmartBirthApplicationTopic(), request);
        return request.getNewBirthDetails();
    }

    public RegisterBirthDetailsRequest searchBirthDetailsForRegister(NewBirthDetailRequest requestApplication, Object mdmsData) {
        List<Object> preparedStmtValues = new ArrayList<>();
        SearchCriteria criteria = new SearchCriteria();
        List<RegisterBirthDetail> result = null;
        if (requestApplication.getNewBirthDetails().size() > 0) {
            criteria.setId(requestApplication.getNewBirthDetails().get(0).getId());
            criteria.setTenantId(requestApplication.getNewBirthDetails().get(0).getTenantId());
            String query = commonQueryBuilder.getApplicationSearchQueryForRegistry(criteria, preparedStmtValues);
            result = jdbcTemplate.query(query, preparedStmtValues.toArray(), registerRowMapperForApp);
            registerStatisticsEnrichment.setRegisterStatistics(result.get(0).getRegisterBirthStatitical(), mdmsData, result.get(0).getRegisterBirthPresent().getTenantId());
        }
        return RegisterBirthDetailsRequest.builder()
                .requestInfo(requestApplication.getRequestInfo())
                .registerBirthDetails(result).build();
    }

    public NewBirthSearchResponse searchBirthDetails(NewBirthDetailRequest request, SearchCriteria criteria) {
        List<Object> preparedStmtValues = new ArrayList<>();
        criteria.setApplicationType(BirthConstants.FUN_MODULE_NEW);
        int cnt = commonQueryBuilder.searchBirthCount(criteria,jdbcTemplate);
        if (cnt == 0) {
            return null;
        } else {
            String query = commonQueryBuilder.getBirthApplicationSearchQuery(criteria, preparedStmtValues, Boolean.FALSE);
            List<NewBirthApplication> result = jdbcTemplate.query(query, preparedStmtValues.toArray(), ksmartBirthApplicationRowMapper);

            RequestInfo requestInfo = request.getRequestInfo();
            responseEnrichment.setNewBirthRequestData(requestInfo, result);
            return NewBirthSearchResponse.builder()
                    .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), Boolean.TRUE))
                    .newBirthDetails(result)
                    .count(cnt)
                    .build();
        }
    }



    public List<NewBirthApplication> searchBirth(RequestInfo requestInfo, SearchCriteria criteria) {
        List<Object> preparedStmtValues = new ArrayList<>();        
        String query = commonQueryBuilder.getBirthApplicationSearchQueryCommon(criteria, preparedStmtValues, Boolean.FALSE);
        if (preparedStmtValues.size() == 0) {
            throw new CustomException(ErrorCodes.NOT_FOUND.getCode(), "No result found.");
        } else {
            List<NewBirthApplication> result = jdbcTemplate.query(query, preparedStmtValues.toArray(), ksmartBirthApplicationRowMapperPay);
            return result;
        }
    }

}

