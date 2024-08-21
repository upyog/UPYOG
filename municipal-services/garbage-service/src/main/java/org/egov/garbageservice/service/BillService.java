package org.egov.garbageservice.service;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.garbageservice.contract.bill.Bill;
import org.egov.garbageservice.contract.bill.BillRepository;
import org.egov.garbageservice.contract.bill.BillResponse;
import org.egov.garbageservice.contract.bill.BillSearchCriteria;
import org.egov.garbageservice.contract.bill.GenerateBillCriteria;
import org.egov.garbageservice.util.ResponseInfoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

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
	
}
