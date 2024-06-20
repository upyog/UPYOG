package org.egov.repository;

import java.util.ArrayList;
import java.util.List;

import org.egov.repository.querybuilder.SORApplicationQueryBuilder;
import org.egov.repository.querybuilder.WMSBankDetailsApplicationQueryBuilder;
import org.egov.repository.querybuilder.WMSContractorApplicationQueryBuilder;
import org.egov.repository.querybuilder.WMSWorkApplicationQueryBuilder;
import org.egov.repository.rowmapper.SORApplicationRowMapper;
import org.egov.repository.rowmapper.WMSBankDetailsApplicationRowMapper;
import org.egov.repository.rowmapper.WMSContractorApplicationRowMapper;
import org.egov.repository.rowmapper.WMSWorkApplicationRowMapper;
import org.egov.web.models.SORApplicationSearchCriteria;
import org.egov.web.models.ScheduleOfRateApplication;
import org.egov.web.models.WMSBankDetailsApplication;
import org.egov.web.models.WMSBankDetailsApplicationSearchCriteria;
import org.egov.web.models.WMSContractorApplication;
import org.egov.web.models.WMSContractorApplicationSearchCriteria;
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class WMSBankDetailsRepository {
	
	@Autowired
    private WMSBankDetailsApplicationQueryBuilder queryBuilder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private WMSBankDetailsApplicationRowMapper rowMapper;

    public List<WMSBankDetailsApplication>getApplications(WMSBankDetailsApplicationSearchCriteria searchCriteria){
        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getBankDetailsApplicationSearchQuery(searchCriteria, preparedStmtList);
        log.info("Final query: " + query);
        return jdbcTemplate.query(query,  rowMapper,preparedStmtList.toArray());
    }

	public void saveFile(List<WMSBankDetailsApplication> entities) {
		// TODO Auto-generated method stub
		for (WMSBankDetailsApplication entity : entities) {
            String sql = "INSERT INTO bank_details (bank_id,bank_name,bank_branch,bank_ifsc_code,bank_branch_ifsc_code,status,createdby,lastmodifiedby,createdtime, lastmodifiedtime) VALUES (?, ?, ?, ?,?,?,?,?,?,?)";
            jdbcTemplate.update(sql, entity.getBankId(), entity.getBankName(),entity.getBankBranch(),entity.getBankIfscCode(),entity.getBankBranchIfscCode(),entity.getStatus(),"CSV File Upload","WMS User",System.currentTimeMillis(),System.currentTimeMillis());
        }
		
	}

}
