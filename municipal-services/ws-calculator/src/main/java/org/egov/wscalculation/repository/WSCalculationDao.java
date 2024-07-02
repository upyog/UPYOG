package org.egov.wscalculation.repository;

import java.util.ArrayList;
import java.util.List;

import org.egov.wscalculation.web.models.MeterConnectionRequest;
import org.egov.wscalculation.web.models.MeterConnectionRequests;
import org.egov.wscalculation.web.models.MeterReading;
import org.egov.wscalculation.web.models.MeterReadingSearchCriteria;
import org.egov.wscalculation.web.models.WaterConnection;
import org.egov.wscalculation.web.models.WaterDetails;

public interface WSCalculationDao {

	void saveMeterReading(MeterConnectionRequest meterConnectionRequest);
	
	List<MeterReading> searchMeterReadings(MeterReadingSearchCriteria criteria);
	
	ArrayList<String> searchTenantIds();

	ArrayList<String> searchConnectionNos(String connectionType, String tenantId);
	
	List<MeterReading> searchCurrentMeterReadings(MeterReadingSearchCriteria criteria);
	
	int isMeterReadingConnectionExist(List<String> ids);
	
	List<WaterConnection> getConnectionsNoList(String tenantId, String connectionType, Integer batchOffset, Integer batchsize, Long fromDate, Long toDate);
	
	List<WaterConnection> getConnectionsNoListForDemand(String tenantId, String connectionType, Long fromDate, Long toDate);

	List<String> getTenantId();
	
	int isBillingPeriodExists(String connectionNo, String billingPeriod);
	
	List<String> getConnectionsNoByLocality(String tenantId, String connectionType, String locality);
	
	Long searchLastDemandGenFromDate(String consumerCode, String tenantId);
	
	Boolean isConnectionDemandAvailableForBillingCycle(String tenantId, Long taxPeriodFrom, Long taxPeriodTo, String consumerCode); 

	long getConnectionCount(String tenantid, Long fromDate, Long toDate);
	
	List<WaterConnection> getConnection(String tenantId, String consumerCode,String connectionType,Long fromDate, Long toDate);
	List<String> getLocalityList(String tenantId, String locality); 
	List<WaterDetails> getConnectionsNoListforsingledemand(String tenantId, String connectionType, Long taxPeriodFrom,
			Long taxPeriodTo, String cone);

	List<String> fetchUsageCategory(String consumerCodes);
	List<String> fetchSewConnection(String consumerCodes); 
	void updateBillStatus(List<String> consumerCodes, String string, String string2);
}
