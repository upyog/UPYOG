package com.example.hpgarbageservice.service;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.hpgarbageservice.contract.bill.Bill;
import com.example.hpgarbageservice.contract.bill.BillRepository;
import com.example.hpgarbageservice.contract.bill.BillResponse;
import com.example.hpgarbageservice.contract.bill.BillSearchCriteria;
import com.example.hpgarbageservice.contract.bill.GenerateBillCriteria;
import com.example.hpgarbageservice.util.ResponseInfoFactory;

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
