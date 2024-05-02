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
import org.egov.wscalculation.web.models.BillScheduler;
import org.egov.wscalculation.web.models.BillScheduler.StatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Repository;

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

		String query = queryBuilder.getBillGenerationSchedulerQuery(criteria, preparedStatement);
		if (query == null)
			return Collections.emptyList();
		log.debug("Prepared Statement" + preparedStatement.toString());
		return jdbcTemplate.query(query, preparedStatement.toArray(), billGenerateSchedulerRowMapper);
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
			String tenantId) {

		List<Object> preparedStmtList = new ArrayList<>();
		List<String> res = new ArrayList<>();
		String queryString = queryBuilder.getBillSchedulerSearchQuery(locality, billFromDate, billToDate, tenantId,
				preparedStmtList);
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

				jdbcTemplate.update(WSCalculatorQueryBuilder.EG_WS_BILL_SCHEDULER_CONNECTION_STATUS_INSERT, new PreparedStatementSetter() {

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
			});
		}catch (Exception e) {
			log.error("Exception occurred in the insertBillSchedulerConnectionStatus: {}", e);
			e.printStackTrace();
		}
	}

}
