package org.egov.swcalculation.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.egov.swcalculation.constants.SWCalculationConstant;
import org.egov.swcalculation.repository.builder.SWCalculatorQueryBuilder;
import org.egov.swcalculation.repository.rowMapper.BillSearchRowMapper;
import org.egov.swcalculation.repository.rowMapper.BillSearchRowMappers;
import org.egov.swcalculation.repository.rowMapper.DemandSchedulerRowMapper;
import org.egov.swcalculation.repository.rowMapper.Demandcancelwrapper;
import org.egov.swcalculation.repository.rowMapper.SewerageConnectionRowMapper;
import org.egov.swcalculation.repository.rowMapper.SewerageDemandRowMapper;
import org.egov.swcalculation.repository.rowMapper.SewerageRowMapper;
import org.egov.swcalculation.web.models.BillSearch;
import org.egov.swcalculation.web.models.BillSearchs;
import org.egov.swcalculation.web.models.Canceldemandsearch;
import org.egov.swcalculation.web.models.SewerageConnection;
import org.egov.swcalculation.web.models.SewerageDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class SewerageCalculatorDaoImpl implements SewerageCalculatorDao {
	
	@Autowired
	SWCalculatorQueryBuilder queryBuilder;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	DemandSchedulerRowMapper demandSchedulerRowMapper;

	@Autowired
	SewerageRowMapper sewerageRowMapper;
	
	@Autowired
	SewerageConnectionRowMapper sewerageConnectionRowMapper;

	@Autowired
	SewerageDemandRowMapper sewerageDemandRowMapper;
	
	
	
	
	
	@Autowired
	private Demandcancelwrapper demandcancelwrapper;
	
	@Autowired
	private BillSearchRowMapper billsearchMapper;
	
	@Autowired
	private BillSearchRowMappers billsearchMappers;

	@Override
	public List<String> getTenantId() {
		String query = queryBuilder.getDistinctTenantIds();
		log.info("Tenant Id's List Query : "+query);
		return (ArrayList<String>) jdbcTemplate.queryForList(query, String.class);
	}

	@Override
	public List<SewerageConnection> getConnectionsNoList(String tenantId, String connectionType, Integer batchOffset, Integer batchsize, Long fromDate, Long toDate) {
		List<Object> preparedStatement = new ArrayList<>();
		String query = queryBuilder.getConnectionNumberList(tenantId, connectionType, preparedStatement, batchOffset, batchsize, fromDate, toDate);
		StringBuilder builder = new StringBuilder();
		builder.append("sewerage ").append(connectionType).append(" connection list : ").append(query);
		log.info(builder.toString());
		return jdbcTemplate.query(query, preparedStatement.toArray(), sewerageRowMapper);
	}
	@Override
	public List<SewerageDetails> getConnectionsNoListsingle(String tenantId, String connectionType,Long fromDate, Long toDate, String Connectionno) {
		List<Object> preparedStatement = new ArrayList<>();
		List<Object> preparedStatementTwo = new ArrayList<>();
		//String query = queryBuilder.getConnectionNumberListsingle(tenantId, connectionType, preparedStatement, fromDate, toDate,Connectionno);
		String query = "";
//		String queryTwo = "";
		List<SewerageDetails> sewerageDetails = null;
//		List<SewerageDetails> sewerageDetailsTwo =  null;
		if((tenantId.equals("pb.amritsar"))) {
			 query = queryBuilder.getConnectionNumberListForNonCommercial(tenantId, connectionType,SWCalculationConstant.ACTIVE, fromDate, toDate, Connectionno, preparedStatement);
//		         queryTwo = queryBuilder.getConnectionNumberListForCommercialOnlySewerage(tenantId, connectionType,SWCalculationConstant.ACTIVE, fromDate, toDate, Connectionno, preparedStatementTwo);  
//			 // log.info("Demand will not generate for water metered connections in Amritsar: "+query);
			 sewerageDetails= jdbcTemplate.query(query, preparedStatement.toArray(), demandSchedulerRowMapper);
//			 sewerageDetailsTwo=jdbcTemplate.query(queryTwo, preparedStatement.toArray(), demandSchedulerRowMapper);
//			 sewerageDetails.addAll(sewerageDetailsTwo);
		}else {
			query = queryBuilder.getConnectionNumberListsingle(tenantId, connectionType, preparedStatement, fromDate, toDate,Connectionno);
			sewerageDetails= jdbcTemplate.query(query, preparedStatement.toArray(), demandSchedulerRowMapper);
		}
		
		// log.info(query.toString());	
		// log.info(preparedStatement.toString());
		// log.info(queryTwo.toString());
		// log.info(preparedStatementTwo.toString());
		List<SewerageDetails> deDuplSewerageList = sewerageDetails.stream().distinct().collect(Collectors.toList());
	 
		return deDuplSewerageList;
	}
	@Override
	public List<String> getConnectionsNoByLocality(String tenantId, String connectionType, String locality) {
		List<Object> preparedStatement = new ArrayList<>();
		String query = queryBuilder.getConnectionsNoByLocality(tenantId, connectionType,SWCalculationConstant.ACTIVE, locality,null, preparedStatement);
		log.info("Sewerage " + connectionType + " connection list : " + query);
		return jdbcTemplate.queryForList(query, preparedStatement.toArray(), String.class);
	}
	
	@Override
	public List<String> getConnectionsNoByGroups(String tenantId, String connectionType, String group) {
		List<Object> preparedStatement = new ArrayList<>();
		String query = queryBuilder.getConnectionsNoByLocality(tenantId, connectionType,SWCalculationConstant.ACTIVE, null,group, preparedStatement);
		log.info("preparedStatement: " + preparedStatement + " query : " + query);
		return jdbcTemplate.queryForList(query, preparedStatement.toArray(), String.class);
	}
	
	
	@Override
	public long getConnectionCount(String tenantid, Long fromDate, Long toDate){
		//List<Object> preparedStatement = new ArrayList<>();
		String query = queryBuilder.getCountQuery();
		//preparedStatement.add(tenantid);
		/*preparedStatement.add(fromDate);
		preparedStatement.add(toDate);
		preparedStatement.add(tenantid);*/

		long count = jdbcTemplate.queryForObject(query,Integer.class);
		return count;
	}
	
	@Override
	public List<SewerageConnection> getConnectionsNoListForDemand(String tenantId, String connectionType, Long fromDate, Long toDate) {
		List<Object> preparedStatement = new ArrayList<>();
		String query = queryBuilder.getConnectionNumberListForDemand(tenantId, connectionType, preparedStatement,fromDate, toDate);
		log.info("sewerage " + connectionType + " connection list : " + query + " Parameters: "+preparedStatement);
		return jdbcTemplate.query(query, preparedStatement.toArray(), sewerageDemandRowMapper);
	}
	
	@Override
	public List<SewerageConnection> getConnection(String tenantId, String consumerCode,String connectionType, Long fromDate, Long toDate) {
		List<Object> preparedStatement = new ArrayList<>();
		String query = queryBuilder.getConnectionNumber(tenantId, consumerCode,connectionType, preparedStatement,fromDate, toDate);
		log.info("Sewerage " + connectionType + " connection list : " + query);
		return jdbcTemplate.query(query, preparedStatement.toArray(), sewerageConnectionRowMapper);
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
	public List<String> getLocalityList(String tenantId,String batchCode ) {
		List<Object> preparedStatement = new ArrayList<>();
		String query = queryBuilder.getLocalityListWithBatch(tenantId,batchCode,preparedStatement);
		log.info("batchCode " + batchCode + " Locality list : " + query);
		return jdbcTemplate.queryForList(query, preparedStatement.toArray(), String.class);
	}
	
	
	
/* CancelBill */
	
	
	
	/* DEMAND ID PICK */
	public List<Canceldemandsearch> getConnectionCancel(String businessService, String tenantId, String consumerCode,  Long taxPeriodFrom,
			Long taxPeriodTo ) {
		
			List<Object> preparedStatement = new ArrayList<>();
			String query = queryBuilder.getCancelBill(businessService ,tenantId , consumerCode , taxPeriodTo,  taxPeriodFrom, preparedStatement);
			log.info("preparedStatement: " + preparedStatement + " connection type: " + 
					 " connection list : " + query);
			return jdbcTemplate.query(query, preparedStatement.toArray(), demandcancelwrapper);
		
	}
	
	
	public List<Canceldemandsearch> getConnectionCancels(String tenantId, String demandid) {
		
		List<Object> preparedStatement = new ArrayList<>();
		String query = queryBuilder.getCancelBills( tenantId,demandid, preparedStatement);
		log.info("preparedStatement: " + preparedStatement + " connection type: " + 
				 " connection list : " + query);
		return jdbcTemplate.query(query, preparedStatement.toArray(), demandcancelwrapper);
	
}
	
	/* UPDATE */
	
	
	public Boolean getUpdate(List demandlist) {		
			List<Object> preparedStatement = new ArrayList<>();
			String query = queryBuilder.getUpdateDemand(demandlist,preparedStatement);
			log.info("preparedStatement: " + preparedStatement + " connection type: " + 
					 " connection list : " + query);
			jdbcTemplate.update(query, preparedStatement.toArray());	
			return true;
	}
	
	public Boolean getUpdates(List demandlists) {		
		List<Object> preparedStatement = new ArrayList<>();
		String query = queryBuilder.getUpdateDemands(demandlists,preparedStatement);
		log.info("preparedStatement: " + preparedStatement + " connection type: " + 
				 " connection list : " + query);
		jdbcTemplate.update(query, preparedStatement.toArray());	
		return true;
}
	
	
	
	
	public List<BillSearch> getBill(String consumercode,String businessService) {
		
		List<Object> preparedStatement = new ArrayList<>();
		String query = queryBuilder.getBillid(consumercode,businessService,preparedStatement);
		log.info("preparedStatement: " + preparedStatement + " connection type: " + 
				 " connection list : " + query);
		return jdbcTemplate.query(query, preparedStatement.toArray(),billsearchMapper);
	
}
	
	public List<BillSearchs> getBillss(String tenantId, String demandid) {
		
		List<Object> preparedStatement = new ArrayList<>();
		String query = queryBuilder.getBillidss(tenantId,demandid,preparedStatement);
		log.info("preparedStatement: " + preparedStatement + " connection type: " + 
				 " connection list : " + query);
		return jdbcTemplate.query(query, preparedStatement.toArray(),billsearchMappers);

	}


	
	
	public Boolean getexpiryBill(List billSearch) {
		
		List<Object> preparedStatement = new ArrayList<>();
		String query = queryBuilder.getBillDemand(billSearch,preparedStatement);
		log.info("preparedStatement: " + preparedStatement + " connection type: " + 
				 " connection list : " + query);
		jdbcTemplate.update(query, preparedStatement.toArray());	
		return true;
}
	
public Boolean getexpiryBills(List billSearchsss) {
		
		List<Object> preparedStatement = new ArrayList<>();
		String query = queryBuilder.getBillDemands(billSearchsss,preparedStatement);
		log.info("preparedStatement: " + preparedStatement + " connection type: " + 
				 " connection list : " + query);
		jdbcTemplate.update(query, preparedStatement.toArray());	
		return true;
}
	
	
}
