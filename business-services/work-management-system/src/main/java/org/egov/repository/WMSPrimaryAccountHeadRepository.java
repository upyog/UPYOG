package org.egov.repository;

import java.util.ArrayList;
import java.util.List;

import org.egov.repository.querybuilder.SORApplicationQueryBuilder;
import org.egov.repository.querybuilder.WMSBankDetailsApplicationQueryBuilder;
import org.egov.repository.querybuilder.WMSContractorApplicationQueryBuilder;
import org.egov.repository.querybuilder.WMSContractorSubTypeApplicationQueryBuilder;
import org.egov.repository.querybuilder.WMSPrimaryAccountHeadApplicationQueryBuilder;
import org.egov.repository.querybuilder.WMSWorkApplicationQueryBuilder;
import org.egov.repository.rowmapper.SORApplicationRowMapper;
import org.egov.repository.rowmapper.WMSBankDetailsApplicationRowMapper;
import org.egov.repository.rowmapper.WMSContractorApplicationRowMapper;
import org.egov.repository.rowmapper.WMSContractorSubTypeApplicationRowMapper;
import org.egov.repository.rowmapper.WMSPrimaryAccountHeadApplicationRowMapper;
import org.egov.repository.rowmapper.WMSWorkApplicationRowMapper;
import org.egov.web.models.SORApplicationSearchCriteria;
import org.egov.web.models.ScheduleOfRateApplication;
import org.egov.web.models.WMSBankDetailsApplication;
import org.egov.web.models.WMSBankDetailsApplicationSearchCriteria;
import org.egov.web.models.WMSContractorApplication;
import org.egov.web.models.WMSContractorApplicationSearchCriteria;
import org.egov.web.models.WMSContractorSubTypeApplication;
import org.egov.web.models.WMSContractorSubTypeApplicationSearchCriteria;
import org.egov.web.models.WMSPrimaryAccountHeadApplication;
import org.egov.web.models.WMSPrimaryAccountHeadApplicationSearchCriteria;
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class WMSPrimaryAccountHeadRepository {
	
	@Autowired
    private WMSPrimaryAccountHeadApplicationQueryBuilder queryBuilder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private WMSPrimaryAccountHeadApplicationRowMapper rowMapper;

    public List<WMSPrimaryAccountHeadApplication>getApplications(WMSPrimaryAccountHeadApplicationSearchCriteria searchCriteria){
        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getPrimaryAccountHeadApplicationSearchQuery(searchCriteria, preparedStmtList);
        log.info("Final query: " + query);
        return jdbcTemplate.query(query,  rowMapper,preparedStmtList.toArray());
    }

}
