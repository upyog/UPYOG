package org.egov.tl.service;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.repository.BillRepository;
import org.egov.tl.web.models.collection.Bill;
import org.egov.tl.web.models.contract.BillResponse;
import org.egov.tl.web.models.contract.BillSearchCriteria;
import org.egov.tl.web.models.contract.GenerateBillCriteria;
import org.egov.tl.web.models.contract.ResponseFactory;
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
	private ResponseFactory responseFactory;
	

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

		return BillResponse.builder().resposneInfo(responseFactory.getResponseInfo(requestInfo, HttpStatus.OK))
				.bill(bills).build();
	}
	
}
