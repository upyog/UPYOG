package org.egov.garbageservice.service;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.garbageservice.contract.bill.Bill;
import org.egov.garbageservice.contract.bill.BillRepository;
import org.egov.garbageservice.contract.bill.BillResponse;
import org.egov.garbageservice.contract.bill.BillSearchCriteria;
import org.egov.garbageservice.contract.bill.GenerateBillCriteria;
import org.egov.garbageservice.contract.bill.UpdateBillCriteria;
import org.egov.garbageservice.util.ResponseInfoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.egov.garbageservice.model.BillV2;

import lombok.extern.slf4j.Slf4j; 

/**
 * Facade over BillRepository for collection-service bill fetch, search, update, and cancel.
 * Builds BillResponse with ResponseInfo and delegates HTTP calls to the billing microservice.
 */
@Service
@Slf4j
public class BillService {

	@Autowired
	private BillRepository billRepository;

	@Autowired
	private ResponseInfoFactory responseInfoFactory;
	

    BillResponse generateBill(RequestInfo requestInfo,GenerateBillCriteria billCriteria){

        BillResponse billResponse = billRepository.fetchBill(billCriteria, requestInfo);
        
         return billResponse;
    }

	/**
	 * Searches the bills from DB for given criteria and enriches them with TaxAndPayments array
	 * 
	 * @param billCriteria
	 * @param requestInfo
	 * @return
	 */
	public BillResponse searchBill(BillSearchCriteria billSearchCriteria, RequestInfo requestInfo) {

		List<Bill> bills = billRepository.searchBill(billSearchCriteria, requestInfo);

		return BillResponse.builder().resposneInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfo, true))
				.bill(bills).build();
	}
	
	public void cancelBill(UpdateBillCriteria updateBillCriteria, RequestInfo requestInfo) {

		 billRepository.cancelBill(updateBillCriteria, requestInfo);
//		return BillResponse.builder().resposneInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfo, true))
//				.bill(bills).build();
	}
	
	public void updateBill(RequestInfo requestInfo, List<Bill> bills) {
	    billRepository.updateBill(requestInfo, bills);
	}
	
	
}
