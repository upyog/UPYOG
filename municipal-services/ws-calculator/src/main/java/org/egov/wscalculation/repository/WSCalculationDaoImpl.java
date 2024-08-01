package org.egov.wscalculation.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.egov.wscalculation.constants.WSCalculationConstant;
import org.egov.wscalculation.producer.WSCalculationProducer;
import org.egov.wscalculation.repository.builder.WSCalculatorQueryBuilder;
import org.egov.wscalculation.web.models.MeterConnectionRequests;
import org.egov.wscalculation.repository.rowmapper.DemandSchedulerRowMapper;
import org.egov.wscalculation.repository.rowmapper.Demandcancelwrapper;
import org.egov.wscalculation.repository.rowmapper.MeterReadingCurrentReadingRowMapper;
import org.egov.wscalculation.repository.rowmapper.MeterReadingRowMapper;
import org.egov.wscalculation.repository.rowmapper.WaterConnectionRowMapper;
import org.egov.wscalculation.repository.rowmapper.WaterDemandRowMapper;
import org.egov.wscalculation.repository.rowmapper.WaterRowMapper;
import org.egov.wscalculation.web.models.CancelDemand;
import org.egov.wscalculation.web.models.Canceldemandsearch;
import org.egov.wscalculation.web.models.MeterConnectionRequest;
import org.egov.wscalculation.web.models.MeterReading;
import org.egov.wscalculation.web.models.MeterReadingSearchCriteria;
import org.egov.wscalculation.web.models.WaterConnection;
import org.egov.wscalculation.web.models.WaterDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class WSCalculationDaoImpl implements WSCalculationDao {

	@Autowired
	private WSCalculationProducer wSCalculationProducer;

	@Autowired
	private WSCalculatorQueryBuilder queryBuilder;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private MeterReadingRowMapper meterReadingRowMapper;

	@Autowired
	private MeterReadingCurrentReadingRowMapper currentMeterReadingRowMapper;

	@Autowired
	private DemandSchedulerRowMapper demandSchedulerRowMapper;
	
	@Autowired
	private Demandcancelwrapper demandcancelwrapper;
	@Autowired
	private WaterRowMapper waterRowMapper;

	@Autowired
	private WaterConnectionRowMapper waterConnectionRowMapper;

	@Autowired
	private WaterDemandRowMapper waterDemandRowMapper;

	@Value("${egov.meterservice.createmeterconnection}")
	private String createMeterConnection;

	/**
	 * 
	 * @param meterConnectionRequest MeterConnectionRequest contains meter reading
	 *                               connection to be created
	 */
	@Override
	public void saveMeterReading(MeterConnectionRequest meterConnectionRequest) {
		wSCalculationProducer.push(createMeterConnection, meterConnectionRequest);
	}

	/**
	 * 
	 * @param criteria would be meter reading criteria
	 * @return List of meter readings based on criteria
	 */
	@Override
	public List<MeterReading> searchMeterReadings(MeterReadingSearchCriteria criteria) {
		List<Object> preparedStatement = new ArrayList<>();
		String query = queryBuilder.getSearchQueryString(criteria, preparedStatement);
		if (query == null)
			return Collections.emptyList();
		log.debug("Query: " + query);
		log.debug("Prepared Statement" + preparedStatement.toString());
		return jdbcTemplate.query(query, preparedStatement.toArray(), meterReadingRowMapper);
	}
	
	@Override
	public List<MeterReading> searchCurrentMeterReadings(MeterReadingSearchCriteria criteria) {
		List<Object> preparedStatement = new ArrayList<>();
		String query = queryBuilder.getCurrentReadingConnectionQuery(criteria, preparedStatement);
		if (query == null)
			return Collections.emptyList();
		log.debug("Query: " + query);
		log.debug("Prepared Statement" + preparedStatement.toString());
		return jdbcTemplate.query(query, preparedStatement.toArray(), currentMeterReadingRowMapper);
	}

	/**
	 * 
	 * @param ids of string of connection ids on which search is performed
	 * @return total number of meter reading objects if present in the table for
	 *         that particular connection ids
	 */
	@Override
	public int isMeterReadingConnectionExist(List<String> ids) {
		Set<String> connectionIds = new HashSet<>(ids);
		List<Object> preparedStatement = new ArrayList<>();
		String query = queryBuilder.getNoOfMeterReadingConnectionQuery(connectionIds, preparedStatement);
		log.debug("Query: " + query);
		return jdbcTemplate.queryForObject(query, preparedStatement.toArray(), Integer.class);
	}

	@Override
	public ArrayList<String> searchTenantIds() {
		ArrayList<String> tenantIds = new ArrayList<>();
		String query = queryBuilder.getTenantIdConnectionQuery();
		if (query == null)
			return tenantIds;
		log.debug("Query: " + query);
		tenantIds = (ArrayList<String>) jdbcTemplate.queryForList(query, String.class);
		return tenantIds;
	}

	@Override
	public ArrayList<String> searchConnectionNos(String connectionType, String tenantId) {
		ArrayList<String> connectionNos = new ArrayList<>();
		List<Object> preparedStatement = new ArrayList<>();
		String query = queryBuilder.getConnectionNumberFromWaterServicesQuery(preparedStatement, connectionType,
				tenantId);
		if (query == null)
			return connectionNos;
		log.info("Query: " + query);

		List<WaterDetails> waterDetails=jdbcTemplate.query(query,preparedStatement.toArray(),demandSchedulerRowMapper);
		connectionNos = (ArrayList<String>) waterDetails.stream().map(waterdetail->waterdetail.getConnectionNo()).collect(Collectors.toList());
		return connectionNos;
	}

	

	@Override
	public List<WaterConnection> getConnectionsNoList(String tenantId, String connectionType, Integer batchOffset,
			Integer batchsize, Long fromDate, Long toDate) {
		List<Object> preparedStatement = new ArrayList<>();
		String query = queryBuilder.getConnectionNumberList(tenantId, connectionType, preparedStatement, batchOffset,
				batchsize, fromDate, toDate);
		log.info("water " + connectionType + " connection list : " + query);
		return jdbcTemplate.query(query, preparedStatement.toArray(), waterRowMapper);
	}
	

	@Override
	public List<WaterConnection> getConnectionsNoListForDemand(String tenantId, String connectionType, Long fromDate,
			Long toDate) {
		List<Object> preparedStatement = new ArrayList<>();
		String query = queryBuilder.getConnectionNumberListForDemand(tenantId, connectionType, preparedStatement,
				fromDate, toDate);
		log.info("water " + connectionType + " connection list : " + query + " Parameters: " + preparedStatement);
		return jdbcTemplate.query(query, preparedStatement.toArray(), waterDemandRowMapper);
	}

	@Override
	public List<WaterConnection> getConnection(String tenantId, String consumerCode, String connectionType,
			Long fromDate, Long toDate) {
		List<Object> preparedStatement = new ArrayList<>();
		String query = queryBuilder.getConnectionNumber(tenantId, consumerCode, connectionType, preparedStatement,
				fromDate, toDate);
		log.info("water " + connectionType + " connection list : " + query);
		return jdbcTemplate.query(query, preparedStatement.toArray(), waterConnectionRowMapper);
	}

	@Override
	public List<String> getTenantId() {
		String query = queryBuilder.getDistinctTenantIds();
		log.info("Tenant Id's List Query : " + query);
		return jdbcTemplate.queryForList(query, String.class);
	}
	
	@Override
	public int isBillingPeriodExists(String connectionNo, String billingPeriod) {
		List<Object> preparedStatement = new ArrayList<>();
		String query = queryBuilder.isBillingPeriodExists(connectionNo, billingPeriod, preparedStatement);
		log.info("Is BillingPeriod Exits Query: " + query);
		return jdbcTemplate.queryForObject(query, preparedStatement.toArray(), Integer.class);
	}
	
	@Override
	public long getConnectionCount(String tenantid, Long fromDate, Long toDate) {
		// List<Object> preparedStatement = new ArrayList<>();
		String query = queryBuilder.getCountQuery();
		// preparedStatement.add(tenantid);
		/*
		 * preparedStatement.add(fromDate); preparedStatement.add(toDate);
		 * preparedStatement.add(tenantid);
		 */

		long count = jdbcTemplate.queryForObject(query, Integer.class);
		return count;
	}

	@Override
	public List<String> getConnectionsNoByLocality(String tenantId, String connectionType, String locality) {
		List<Object> preparedStatement = new ArrayList<>();
		String query = queryBuilder.getConnectionsNoByLocality(tenantId, connectionType,WSCalculationConstant.ACTIVE_CONNECTION, locality, preparedStatement);
		log.info("preparedStatement: " + preparedStatement + " query : " + query);
		return jdbcTemplate.queryForList(query, preparedStatement.toArray(), String.class);
	}
	
	@Override
	public List<String> getLocalityList(String tenantId,String batchCode ) {
		List<Object> preparedStatement = new ArrayList<>();
		String query = queryBuilder.getLocalityListWithBatch(tenantId,batchCode,preparedStatement);
		log.info("batchCode " + batchCode + " Locality list : " + query);
		return jdbcTemplate.queryForList(query, preparedStatement.toArray(), String.class);
	}
	@Override
	public Long searchLastDemandGenFromDate(String consumerCode, String tenantId) {
		List<Object> preparedStatement = new ArrayList<>();
		String query = queryBuilder.searchLastDemandGenFromDate(consumerCode, tenantId, preparedStatement);
		log.info("preparedStatement: "+ preparedStatement + " searchLastDemandGenFromDate Query : " + query);
		List<Long> fromDate = jdbcTemplate.queryForList(query, preparedStatement.toArray(), Long.class);
		if(fromDate != null && !fromDate.isEmpty())
			return  fromDate.get(0);
		
		return null;
	}
	@Override
	public Boolean isConnectionDemandAvailableForBillingCycle(String tenantId, Long taxPeriodFrom, Long taxPeriodTo,
			String consumerCode) {
		List<Object> preparedStatement = new ArrayList<>();
		String query = queryBuilder.isConnectionDemandAvailableForBillingCycle(tenantId, taxPeriodFrom, taxPeriodTo, consumerCode, preparedStatement);
		log.info("isConnectionDemandAvailableForBillingCycle Query: " + query + " preparedStatement: "+ preparedStatement);
		
		return jdbcTemplate.queryForObject(query, preparedStatement.toArray(), Boolean.class);
	}

	@Override
	public List<String> fetchUsageCategory(String consumerCodes) {		
		List<Object> preparedStatement = new ArrayList<>();
		String queryString = "select a2.usagecategory from eg_ws_connection a1 inner join eg_pt_property a2 on a1.property_id= a2.propertyid"
				+ " where a1.connectionno = '"+consumerCodes+"'";
		log.info("preparedStatement: " + preparedStatement + " query : " + queryString);
		return jdbcTemplate.queryForList(queryString, preparedStatement.toArray(), String.class);
	}
	@Override
	public List<String> fetchSewConnection(String consumerCodes) {
		List<Object> preparedStatement = new ArrayList<>();
		String queryString = "select a1.connectionno from eg_sw_connection a1 inner join eg_pt_property a2 on a1.property_id= a2.propertyid"+ 
				" where a1.property_id in (select property_id from eg_ws_connection where connectionno ='"+consumerCodes+"')";
		log.info("preparedStatement: " + preparedStatement + " query : " + queryString);
		return jdbcTemplate.queryForList(queryString, preparedStatement.toArray(), String.class);
	}

	public void updateBillStatus(List<String> consumerCodes, String businessService, String status) {
		
		List<Object> preparedStmtList = new ArrayList<>();
		preparedStmtList.add(status.toString());
		String queryStr = queryBuilder.getBillStatusUpdateQuery(consumerCodes,businessService, preparedStmtList);
		jdbcTemplate.update(queryStr, preparedStmtList.toArray());
	}
	@Override
	public List<WaterDetails> getConnectionsNoListforsingledemand(String tenantId, String connectionType, Long taxPeriodFrom,
			Long taxPeriodTo, String cone) {
		
			List<Object> preparedStatement = new ArrayList<>();
			String query = queryBuilder.getConnectionNumberList(tenantId, connectionType,
					WSCalculationConstant.ACTIVE_CONNECTION, taxPeriodFrom, taxPeriodTo, cone, preparedStatement);
			log.info("preparedStatement: " + preparedStatement + " connection type: " + connectionType
					+ " connection list : " + query);
			return jdbcTemplate.query(query, preparedStatement.toArray(), demandSchedulerRowMapper);
		
	}
	
	
	/* DEMAND ID PICK */
	public List<Canceldemandsearch> getConnectionCancel(String businessService, String tenantId, String consumerCode,  Long taxPeriodFrom,
			Long taxPeriodTo ) {
		
			List<Object> preparedStatement = new ArrayList<>();
			String query = queryBuilder.getCancelBill(businessService ,tenantId , consumerCode , taxPeriodTo,  taxPeriodFrom, preparedStatement);
			log.info("preparedStatement: " + preparedStatement + " connection type: " + 
					 " connection list : " + query);
			return jdbcTemplate.query(query, preparedStatement.toArray(), demandcancelwrapper);
		
	}
	
	/* UPDATE */
	
	
	public Boolean getUpdate(String demandId) {
		
			List<Object> preparedStatement = new ArrayList<>();
			String query = queryBuilder.getUpdateDemand(demandId,preparedStatement);
			log.info("preparedStatement: " + preparedStatement + " connection type: " + 
					 " connection list : " + query);
			jdbcTemplate.update(query, preparedStatement.toArray());	
			return true;
	}
	
	
	public Boolean getexpiryBill(String demandId) {
		
		List<Object> preparedStatement = new ArrayList<>();
		String query = queryBuilder.getBillDemand(demandId,preparedStatement);
		log.info("preparedStatement: " + preparedStatement + " connection type: " + 
				 " connection list : " + query);
		jdbcTemplate.update(query, preparedStatement.toArray());	
		return true;
}



	
	
}
