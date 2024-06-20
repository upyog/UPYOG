package org.egov.repository;

import java.util.ArrayList;
import java.util.List;

import org.egov.repository.querybuilder.SORApplicationQueryBuilder;
import org.egov.repository.querybuilder.WMSContractorApplicationQueryBuilder;
import org.egov.repository.querybuilder.WMSWorkApplicationQueryBuilder;
import org.egov.repository.querybuilder.WMSWorkOrderApplicationQueryBuilder;
import org.egov.repository.rowmapper.SORApplicationRowMapper;
import org.egov.repository.rowmapper.WMSContractorApplicationRowMapper;
import org.egov.repository.rowmapper.WMSWorkApplicationRowMapper;
import org.egov.repository.rowmapper.WMSWorkOrderApplicationRowMapper;
import org.egov.web.models.SORApplicationSearchCriteria;
import org.egov.web.models.ScheduleOfRateApplication;
import org.egov.web.models.WMSContractorApplication;
import org.egov.web.models.WMSContractorApplicationSearchCriteria;
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.egov.web.models.WMSWorkOrderApplication;
import org.egov.web.models.WMSWorkOrderApplicationSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class WMSWorkOrderRepository {
	
	@Autowired
    private WMSWorkOrderApplicationQueryBuilder queryBuilder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private WMSWorkOrderApplicationRowMapper rowMapper;

    public List<WMSWorkOrderApplication>getApplications(WMSWorkOrderApplicationSearchCriteria searchCriteria){
        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getWorkOrderApplicationSearchQuery(searchCriteria, preparedStmtList);
        log.info("Final query: " + query);
        return jdbcTemplate.query(query,  rowMapper,preparedStmtList.toArray());
    }

}
