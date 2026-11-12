package org.egov.pgr.repository;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.egov.pgr.contract.ReportRequest;
import org.egov.pgr.utils.ReportConstants;
import org.egov.pgr.utils.ReportQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class ReportRepository {
	
	@Autowired
	private ReportQueryBuilder reportQueryBuilder;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public List<Map<String, Object>> getDataFromDb(ReportRequest reportRequest) {
		ReportQuery reportQuery = null;
		List<Map<String, Object>> result = new ArrayList<>();
		if(reportRequest.getReportName().equalsIgnoreCase(ReportConstants.COMPLAINT_TYPE_REPORT)) {
			reportQuery = reportQueryBuilder.getComplaintWiseReportQuery(reportRequest);
		}else if(reportRequest.getReportName().equalsIgnoreCase(ReportConstants.AO_REPORT)) {
			reportQuery = reportQueryBuilder.getAOWiseReportQuery(reportRequest);
		}else if(reportRequest.getReportName().equalsIgnoreCase(ReportConstants.DEPARTMENT_REPORT)) {
			reportQuery = reportQueryBuilder.getDepartmentWiseReportQuery(reportRequest);
		}else if(reportRequest.getReportName().equalsIgnoreCase(ReportConstants.SOURCE_REPORT)) {
			reportQuery = reportQueryBuilder.getSourceWiseReportQuery(reportRequest);
		}else if(reportRequest.getReportName().equalsIgnoreCase(ReportConstants.ULBEMPLOYEE_REPORT)) {
			reportQuery = reportQueryBuilder.getFunctionaryWiseReportQuert(reportRequest);
		}
		try {
			result = jdbcTemplate.queryForList(
			        reportQuery.getQuery(),
			        reportQuery.getParams().toArray()
			);
		}catch(Exception e) {
			log.error("Exception while executing query: "+e);
		}
		log.info("dbResponse: "+result);
		return result;
	}
	
	public void createOrDropViewDb(ReportRequest reportRequest, Boolean shouldbeDropped) {
		List<String> queries = new LinkedList<>();
		if(!shouldbeDropped) {
/*			queries.add(reportQueryBuilder.createATempTable());
			queries.add(reportQueryBuilder.populateTempTable(reportRequest));*/
			queries.add(reportQueryBuilder.getCreateViewQuery());
		}else {
			queries.add(reportQueryBuilder.getDropViewQuery());
		//	queries.add(reportQueryBuilder.getDropTempTableQuery());
		}
		for(String query: queries) {
			try {
				   jdbcTemplate.execute(query);
			}catch(Exception e) {
				log.error("Query: "+query);
				log.error("Execution failed: "+e);
			}
		}
	}

}
