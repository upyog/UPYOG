package org.egov.repository;

import java.util.ArrayList;
import java.util.List;

import org.egov.repository.querybuilder.SORApplicationQueryBuilder;
import org.egov.repository.querybuilder.WMSBankDetailsApplicationQueryBuilder;
import org.egov.repository.querybuilder.WMSContractorApplicationQueryBuilder;
import org.egov.repository.querybuilder.WMSFunctionApplicationQueryBuilder;
import org.egov.repository.querybuilder.WMSWorkApplicationQueryBuilder;
import org.egov.repository.rowmapper.SORApplicationRowMapper;
import org.egov.repository.rowmapper.WMSBankDetailsApplicationRowMapper;
import org.egov.repository.rowmapper.WMSContractorApplicationRowMapper;
import org.egov.repository.rowmapper.WMSFunctionApplicationRowMapper;
import org.egov.repository.rowmapper.WMSWorkApplicationRowMapper;
import org.egov.web.models.SORApplicationSearchCriteria;
import org.egov.web.models.ScheduleOfRateApplication;
import org.egov.web.models.WMSBankDetailsApplication;
import org.egov.web.models.WMSBankDetailsApplicationSearchCriteria;
import org.egov.web.models.WMSContractorApplication;
import org.egov.web.models.WMSContractorApplicationSearchCriteria;
import org.egov.web.models.WMSFunctionApplication;
import org.egov.web.models.WMSFunctionApplicationSearchCriteria;
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class WMSFunctionRepository {
	
	@Autowired
    private WMSFunctionApplicationQueryBuilder queryBuilder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private WMSFunctionApplicationRowMapper rowMapper;

    public List<WMSFunctionApplication>getApplications(WMSFunctionApplicationSearchCriteria searchCriteria){
        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getFunctionApplicationSearchQuery(searchCriteria, preparedStmtList);
        log.info("Final query: " + query);
        return jdbcTemplate.query(query,  rowMapper,preparedStmtList.toArray());
    }

	

}
