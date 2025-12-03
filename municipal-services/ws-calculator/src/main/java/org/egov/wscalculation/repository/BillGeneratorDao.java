package org.egov.wscalculation.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.egov.wscalculation.constants.WSCalculationConstant;
import org.egov.wscalculation.producer.WSCalculationProducer;
import org.egov.wscalculation.repository.builder.WSCalculatorQueryBuilder;
import org.egov.wscalculation.repository.rowmapper.BillGenerateSchedulerRowMapper;
import org.egov.wscalculation.web.models.BillGenerationReq;
import org.egov.wscalculation.web.models.BillGenerationSearchCriteria;
import org.egov.wscalculation.web.models.BillGeneratorReq;
import org.egov.wscalculation.web.models.BillScheduler;
import org.egov.wscalculation.web.models.BillScheduler.StatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Repository;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class BillGeneratorDao {

	@Autowired
	private WSCalculationProducer wSCalculationProducer;

	@Autowired
	private BillGenerateSchedulerRowMapper billGenerateSchedulerRowMapper;

	@Autowired
	private WSCalculatorQueryBuilder queryBuilder;

	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Value("${egov.billgenerate.scheduler}")
	private String createBillScheduler;

	public void saveBillGenertaionDetails(BillGenerationReq billGenerationReq) {
		wSCalculationProducer.push(createBillScheduler, billGenerationReq);
	}

	public List<BillScheduler> getBillGenerationDetails(BillGenerationSearchCriteria criteria) {
		List<Object> preparedStatement = new ArrayList<>();
		String query = queryBuilder.searchBillGenerationSchedulerQuery(criteria, preparedStatement);
		if (query == null)
			return Collections.emptyList();
		log.debug("Prepared Statement" + preparedStatement.toString());
		return jdbcTemplate.query(query, preparedStatement.toArray(), billGenerateSchedulerRowMapper);
	}
	
	
	
	public List<BillScheduler> getBillGenerationGroup(BillGenerationSearchCriteria criteria) {
		List<Object> preparedStatement = new ArrayList<>();
		String query = queryBuilder.searchBillGenerationSchedulerQuerys(criteria, preparedStatement);
		if (query == null)
			return Collections.emptyList();
		log.debug("Prepared Statement" + preparedStatement.toString());
		return jdbcTemplate.query(query, preparedStatement.toArray(), billGenerateSchedulerRowMapper);
	}
	
	public List<String> getConnectionsByStatus(String string, String status) {
	    List<Object> preparedStatement = new ArrayList<>();

	    // ✅ Use Map instead of a class
	    Map<String, Object> criteria = new HashMap<>();
	    criteria.put("billSchedulerId", string);
	    criteria.put("status", status);

	    // ✅ Build query dynamically using your queryBuilder
	    String query = queryBuilder.buildGetConnectionsByStatusQuery(criteria, preparedStatement);
	    if (query == null) {
	        return Collections.emptyList();
	    }

	    log.debug("getConnectionsByStatus | Query: {} | Params: {}", query, preparedStatement);

	    // ✅ Execute query and map result
	    return jdbcTemplate.query(
	            query,
	            preparedStatement.toArray(),
	            (rs, rowNum) -> rs.getString("consumercode")
	    );
	}


	
	/**
	 * executes query to update bill scheduler status 
	 * @param billIds
	 */
	public void updateBillSchedularStatus(String schedulerId, StatusEnum status) {

		List<Object> preparedStmtList = new ArrayList<>();
		preparedStmtList.add(status.toString());
		preparedStmtList.add(schedulerId.toString());
		String queryStr = queryBuilder.getBillSchedulerUpdateQuery(schedulerId, preparedStmtList);
		jdbcTemplate.update(queryStr, preparedStmtList.toArray());
	}
	
	/**
	 * fetch existing scheduled bill status for the locality  
	 * @param locality,billFromDate,billToDate and tenantId
	 */
	public List<String> fetchExistingBillSchedularStatusForLocality(String locality, Long billFromDate, Long billToDate,
			String tenantId, String group) {

		List<Object> preparedStmtList = new ArrayList<>();
		List<String> res = new ArrayList<>();
		String queryString = queryBuilder.getBillSchedulerSearchQuery(locality, billFromDate, billToDate, tenantId,group,
				preparedStmtList);
		log.info(queryString);
		log.info(preparedStmtList.toString());

		try {
			res = jdbcTemplate.queryForList(queryString, preparedStmtList.toArray(), String.class);
		} catch (Exception ex) {
			log.error("Exception while reading bill scheduler status" + ex.getMessage());
		}
		return res;
	}
	
	public void insertBillSchedulerConnectionStatus(List<String> consumerCodes, String scheduler_id, 
			String locality, String Status, String tenantid, String reason, long createdTime) {
		try {

			log.info("Entered into insertBillSchedulerConnectionStatus");
			if(consumerCodes ==null || consumerCodes.isEmpty())
				return;
			
			consumerCodes.forEach(consumercode -> {
				String id = UUID.randomUUID().toString();

				int rows = jdbcTemplate.update(WSCalculatorQueryBuilder.EG_WS_BILL_SCHEDULER_CONNECTION_STATUS_INSERT, new PreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps) throws SQLException {

						ps.setString(1, id);
						ps.setString(2, scheduler_id);
						ps.setString(3, locality);
						ps.setString(4, WSCalculationConstant.SERVICE_FIELD_VALUE_WS);
						ps.setObject(5, createdTime);
						ps.setObject(6, createdTime);
						ps.setString(7, Status);
						ps.setString(8, tenantid);
						ps.setString(9, reason+consumercode);
						ps.setString(10, consumercode);

					}
				});
				log.info("Insert result: consumerCode={} rowsInserted={}", consumercode, rows);

			});
		}catch (Exception e) {
			log.error("Exception occurred in the insertBillSchedulerConnectionStatus: {}", e);
			e.printStackTrace();
		}
	}

	
	public void updateBillSchedulerConnectionStatus(String consumerCode, String schedulerId,
	        String locality, String status, String tenantId, String reason, long modifiedTime) {
	    try {
	        log.info("Entered into updateBillSchedulerConnectionStatus for consumerCode: {}", consumerCode);

	        if (consumerCode == null || consumerCode.isEmpty())
	            return;
	        
	        String sql = "UPDATE eg_ws_bill_scheduler_connection_status "
	                   + "SET status = ?, reason = ?, lastupdatedtime = ? "
	                   + "WHERE status='Initiated' AND eg_ws_scheduler_id = ? AND tenantid = ? AND consumercode = ?";

	        int rows = jdbcTemplate.update(sql, ps -> {
	            ps.setString(1, status);
	            ps.setString(2, reason);
	            ps.setObject(3, modifiedTime);
	            ps.setString(4, schedulerId);
	            ps.setString(5, tenantId);
	            ps.setString(6, consumerCode);
	        });

	        
	        log.info("Update result: consumerCode={} rowsUpdated={}", consumerCode, rows);

	    } catch (Exception e) {
	        log.error("Exception occurred in updateBillSchedulerConnectionStatus: {}", e.getMessage(), e);
	    }
	}

}
