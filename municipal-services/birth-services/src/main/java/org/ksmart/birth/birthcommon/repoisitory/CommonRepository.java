package org.ksmart.birth.birthcommon.repoisitory;

import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.ksmart.birth.birthcommon.model.common.CommonPay;
import org.ksmart.birth.birthcommon.model.common.CommonPayRequest;
import org.ksmart.birth.common.producer.BndProducer;
import org.ksmart.birth.common.repository.builder.CommonQueryBuilder;
import org.ksmart.birth.config.BirthConfiguration;
import org.ksmart.birth.newbirth.enrichment.NewBirthResponseEnrichment;
import org.ksmart.birth.newbirth.repository.rowmapper.BirthApplicationRowMapper;
import org.ksmart.birth.utils.enums.ErrorCodes;
import org.ksmart.birth.utils.enums.SearchCriteriaCodes;
import org.ksmart.birth.web.model.SearchCriteria;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
import org.ksmart.birth.web.model.newbirth.NewBirthDetailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class CommonRepository {
     @Autowired
    CommonQueryBuilder commonQueryBuilder;

    @Autowired
    BirthConfiguration birthDeathConfiguration;
    private final BndProducer producer;
    private final JdbcTemplate jdbcTemplate;
    private final NewBirthResponseEnrichment enrichment;
    private final BirthApplicationRowMapper rowMapper;
    @Autowired
    CommonRepository(BndProducer producer, JdbcTemplate jdbcTemplate,
                     BirthApplicationRowMapper rowMapper, NewBirthResponseEnrichment enrichment){
        this.producer = producer;
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
        this.enrichment = enrichment;
    }
    public List<CommonPay> updatePaymentDetails(CommonPayRequest request) {   
    	 
        producer.push(birthDeathConfiguration.getUpdateBirthPaymentTopic(), request);
        return request.getCommonPays();
    }

    public List<NewBirthApplication> searchBirthDetailsCommon(NewBirthDetailRequest request, SearchCriteria criteria) {
        List<Object> preparedStmtValues = new ArrayList<>();
        List<String> uuids = new ArrayList<>();
        if(criteria.getSearchType() != null) {
            if(criteria.getSearchType() == SearchCriteriaCodes.SEARCH_TYPE_MYAPP.getCode() && criteria.getSearchType() != null) {
                if (request.getRequestInfo() != null && !StringUtils.isEmpty(request.getRequestInfo().getUserInfo().getUuid())) {
                    uuids.add(request.getRequestInfo().getUserInfo().getUuid());
                    criteria.setCreatedBy(uuids);
                }
            }
        }
        String query = commonQueryBuilder.getBirthApplicationSearchQuery(criteria, preparedStmtValues, Boolean.FALSE);
        if (preparedStmtValues.size() == 0) {
            throw new CustomException(ErrorCodes.NOT_FOUND.getCode(), "No result found.");
        } else {
            List<NewBirthApplication> result = jdbcTemplate.query(query, preparedStmtValues.toArray(), rowMapper);
            RequestInfo requestInfo = request.getRequestInfo();
            enrichment.setNewBirthRequestData(requestInfo, result);
            return result;
        }
    }
}
