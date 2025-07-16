package org.egov.waterquality.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.exception.InvalidTenantIdException;
import org.egov.common.utils.MultiStateInstanceUtil;
import org.egov.tracer.model.CustomException;
import org.egov.waterquality.producer.WaterQualityProducer;
import org.egov.waterquality.repository.builder.WsQueryBuilder;
import org.egov.waterquality.repository.rowmapper.WaterQualityApplicationRowMapper;
import org.egov.waterquality.web.models.collection.SearchCriteria;
import org.egov.waterquality.web.models.collection.WaterQuality;
import org.egov.waterquality.web.models.collection.WaterQualityRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class WaterDaoImpl implements WaterDao {

    @Autowired
    private WaterQualityProducer waterQualityProducer;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private WsQueryBuilder wsQueryBuilder;

    @Autowired
    private WaterQualityApplicationRowMapper waterQARowMapper;

	@Value("${egov.waterquality.createapplication.topic}")
	private String createWaterConnection;

    @Autowired
    private MultiStateInstanceUtil centralInstanceutil;

	@Override
    public void saveWaterQualityApplication(WaterQualityRequest waterQualityRequest) {
        waterQualityProducer.push(waterQualityRequest.getWaterQualityApplication().getTenantId(), createWaterConnection, waterQualityRequest);
    }

    @Override
    public List<WaterQuality> getWaterQualityApplicationList(SearchCriteria criteria,
                                                        RequestInfo requestInfo) {

        List<WaterQuality> waterQualityApplications = new ArrayList<>();
        List<Object> preparedStatement = new ArrayList<>();
        String query = wsQueryBuilder.getSearchQueryString(criteria, preparedStatement, requestInfo);

        if (query == null)
            return Collections.emptyList();

        try {
            query = centralInstanceutil.replaceSchemaPlaceholder(query, criteria.getTenantId());
        } catch (InvalidTenantIdException e) {
            throw new CustomException("WS_SEARCH_TENANTID_ERROR",
                    "TenantId length is not sufficient to replace query schema in a multi state instance");
        }
        waterQualityApplications = jdbcTemplate.query(query, preparedStatement.toArray(), waterQARowMapper);

        if (waterQualityApplications == null)
            return Collections.emptyList();
        return waterQualityApplications;
    }
	
}
