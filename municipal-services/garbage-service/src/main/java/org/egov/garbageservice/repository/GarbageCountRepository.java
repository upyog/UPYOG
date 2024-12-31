package org.egov.garbageservice.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.egov.garbageservice.model.GarbageBill;
import org.egov.garbageservice.model.GarbageBillSearchCriteria;
import org.egov.garbageservice.repository.rowmapper.GarbageBillRowMapper;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class GarbageCountRepository {
	
//    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	@Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    GarbageBillRowMapper garbageBillRowMapper;

	public List<Map<String, Object>> getAllCounts() {
			List<Map<String, Object>> statusList = null;
			String query = "SELECT SUM(COUNT(*)) OVER () AS total_applications,EXTRACT(MONTH FROM TO_TIMESTAMP(createdtime / 1000)) AS month,COUNT(*) AS application_count FROM eg_wf_processinstance_v2 WHERE modulename = 'GB' AND action = 'APPROVE' GROUP BY month ORDER BY month";
			statusList =jdbcTemplate.queryForList(query);
	        return statusList;
	}
     
}
