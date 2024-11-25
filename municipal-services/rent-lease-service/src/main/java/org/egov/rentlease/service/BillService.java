package org.egov.rentlease.service;

import org.egov.common.contract.request.RequestInfo;
import org.egov.rentlease.contract.bill.BillRepository;
import org.egov.rentlease.contract.bill.BillResponse;
import org.egov.rentlease.contract.bill.GenerateBillCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BillService {
	
	@Autowired
	private BillRepository billRepository;

 BillResponse generateBill(RequestInfo requestInfo, GenerateBillCriteria billCriteria) {
	 BillResponse billResponse = billRepository.fetchBill(billCriteria, requestInfo);
     
     return billResponse;
	}

}
