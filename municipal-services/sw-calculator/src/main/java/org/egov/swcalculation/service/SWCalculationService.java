package org.egov.swcalculation.service;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.swcalculation.web.models.BulkBillCriteria;
import org.egov.swcalculation.web.models.Calculation;
import org.egov.swcalculation.web.models.CalculationReq;
import org.egov.swcalculation.web.models.SewerageConnection;
import org.egov.swcalculation.web.models.SingleDemand;

public interface SWCalculationService {
	
	List<Calculation> getCalculation(CalculationReq request);
	
//	void generateDemandBasedOnTimePeriod(RequestInfo requestInfo);
	
	void generateSingleDemand(SingleDemand singledemand);
	
	void generateBillBasedLocality(RequestInfo requestInfo);
	
	void generateDemandBasedOnTimePeriod(RequestInfo requestInfo, BulkBillCriteria bulkBillCriteria);
	
	List<Calculation> getEstimation(CalculationReq request);
	
	String generateDemandForConsumerCodeBasedOnTimePeriod(RequestInfo requestInfo, BulkBillCriteria bulkBillCriteria);
	
	List<SewerageConnection> getConnnectionWithPendingDemand(RequestInfo requestInfo, BulkBillCriteria bulkBillCriteria);

}
