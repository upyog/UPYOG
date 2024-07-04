package org.egov.swcalculation.repository;

import java.util.List;

import org.egov.swcalculation.web.models.SewerageConnection;
import org.egov.swcalculation.web.models.SewerageDetails;

public interface SewerageCalculatorDao {

	List<String> getTenantId();
	
	List<SewerageConnection> getConnectionsNoList(String tenantId, String connectionType, Integer batchOffset, Integer batchsize, Long fromDate, Long toDate);
	List<SewerageDetails> getConnectionsNoListsingle(String tenantId, String connectionType,  Long fromDate, Long toDate, String connectionno);

	long getConnectionCount(String tenantid, Long fromDate, Long toDate);
	
	//List<SewerageDetails> getConnectionsNoList(String tenantId, String connectionType, Long taxPeriodFrom, Long taxPeriodTo, String cone );
	
	List<String> getConnectionsNoByLocality(String tenantId, String connectionType, String locality);
	
	Long searchLastDemandGenFromDate(String consumerCode, String tenantId);
	
	Boolean isConnectionDemandAvailableForBillingCycle(String tenantId, Long taxPeriodFrom, Long taxPeriodTo, String consumerCode); 
	
	List<SewerageConnection> getConnectionsNoListForDemand(String tenantId, String connectionType, Long fromDate, Long toDate);

	List<SewerageConnection> getConnection(String tenantId, String consumerCode,String connectionType,Long fromDate, Long toDate);
	
	List<String> getLocalityList(String tenantId, String batchCode); 
	
}
