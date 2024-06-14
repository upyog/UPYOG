package org.egov.wscalculation.service;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.wscalculation.web.models.BulkBillCriteria;
import org.egov.wscalculation.web.models.Calculation;
import org.egov.wscalculation.web.models.CalculationReq;
import org.egov.wscalculation.web.models.WaterConnection;

public interface WSCalculationService {

	List<Calculation> getCalculation(CalculationReq calculationReq);

	void jobScheduler();
	
	void generateDemandBasedOnTimePeriod(RequestInfo requestInfo);
	
	void generateBillBasedLocality(RequestInfo requestInfo);

	void generateDemandBasedOnTimePeriod(RequestInfo requestInfo, BulkBillCriteria bulkBillCriteria);
	
	String generateDemandForConsumerCodeBasedOnTimePeriod(RequestInfo requestInfo, BulkBillCriteria bulkBillCriteria);
	
	List<WaterConnection> getConnnectionWithPendingDemand(RequestInfo requestInfo, BulkBillCriteria bulkBillCriteria);

}
