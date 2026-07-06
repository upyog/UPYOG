package org.egov.wscalculation.service;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.wscalculation.web.models.*;

public interface WSCalculationService {

	List<Calculation> getCalculation(CalculationReq calculationReq);

	void jobScheduler();
	
	void generateDemandBasedOnTimePeriod(RequestInfo requestInfo);
	String  generateSingleDemand(SingleDemand singledemand);
	
//	String cancelDemand(CancelDemand cancelDemand);
	void generateBillBasedLocality(RequestInfo requestInfo);

	void generateDemandBasedOnTimePeriod(RequestInfo requestInfo, BulkDemandCriteria bulkBillCriteria);

    void generateDemandLocalityBasedOnTimePeriod(RequestInfo requestInfo, BulkDemandCriteria bulkBillCriteria);
	
	String generateDemandForConsumerCodeBasedOnTimePeriod(RequestInfo requestInfo, BulkBillCriteria bulkBillCriteria);
	
	List<WaterConnection> getConnnectionWithPendingDemand(RequestInfo requestInfo, BulkBillCriteria bulkBillCriteria);

}
