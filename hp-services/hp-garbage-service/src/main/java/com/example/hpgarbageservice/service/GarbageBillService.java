package com.example.hpgarbageservice.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.example.hpgarbageservice.model.AuditDetails;
import com.example.hpgarbageservice.model.GarbageBill;
import com.example.hpgarbageservice.model.GarbageBillRequest;
import com.example.hpgarbageservice.model.GarbageBillSearchCriteria;
import com.example.hpgarbageservice.model.SearchGarbageBillRequest;
import com.example.hpgarbageservice.model.contract.RequestInfo;
import com.example.hpgarbageservice.repository.GarbageBillRepository;

@Service
public class GarbageBillService {

    @Autowired
    private GarbageBillRepository repository;

    public List<GarbageBill> createGarbageBill(GarbageBillRequest garbageBillRequest) {
    	
    	List<GarbageBill> garbageBills = new ArrayList<>();
    	
    	if (!CollectionUtils.isEmpty(garbageBillRequest.getGarbageBills())) {
			garbageBillRequest.getGarbageBills().forEach(garbageBill -> {

				// validate create grbg bill
				validateCreateGarbageBill(garbageBill);

				// enrich grbg bill
				enrichCreateGarbageBill(garbageBill, garbageBillRequest.getRequestInfo());

				// create grbg bill
				garbageBills.add(repository.create(garbageBill));

			});
		}
        
        return garbageBills;
    }

    private void enrichCreateGarbageBill(GarbageBill garbageBill, RequestInfo requestInfo) {
    	AuditDetails auditDetails = null;

		if (null != requestInfo
				&& null != requestInfo.getUserInfo()) {
			auditDetails = AuditDetails.builder()
					.createdBy(requestInfo.getUserInfo().getUuid())
					.createdDate(new Date().getTime())
					.lastModifiedBy(requestInfo.getUserInfo().getUuid())
					.lastModifiedDate(new Date().getTime()).build();
			garbageBill.setAuditDetails(auditDetails);
		}

		// generate garbage_id
		garbageBill.setBillRefNo("GRBG_BILL_REF_"+Long.toString(System.currentTimeMillis()));
		garbageBill.setPaymentStatus("PENDING");
	}

	private void validateCreateGarbageBill(GarbageBill garbageBill) {
    	if (null == garbageBill
				|| null == garbageBill.getGarbageId()
				|| null == garbageBill.getBillAmount()
				|| null == garbageBill.getTotalBillAmount()
				|| null == garbageBill.getBillDueDate()
				|| null == garbageBill.getBillPeriod()) {
			throw new RuntimeException("Provide garbage bill details.");
		}
	}

	public GarbageBill update(GarbageBill bill) {
        repository.update(bill);
        return bill;
    }

    public List<GarbageBill> searchGarbageBills(SearchGarbageBillRequest searchGarbageBillRequest) {

		//validate search criteria
		validateGarbageBillSearchCriteria(searchGarbageBillRequest.getGarbageBillSearchCriteria());
		
		//search garbage account
		List<GarbageBill> garbageBills = repository.searchGarbageBills(searchGarbageBillRequest.getGarbageBillSearchCriteria());
		
		return garbageBills;
    }

	private void validateGarbageBillSearchCriteria(GarbageBillSearchCriteria garbageBillSearchCriteria) {

		if(CollectionUtils.isEmpty(garbageBillSearchCriteria.getIds()) &&
		        CollectionUtils.isEmpty(garbageBillSearchCriteria.getBillRefNos()) &&
		        CollectionUtils.isEmpty(garbageBillSearchCriteria.getGarbageIds()) &&
		        CollectionUtils.isEmpty(garbageBillSearchCriteria.getPaymentIds()) &&
		        CollectionUtils.isEmpty(garbageBillSearchCriteria.getPaymentStatus())) {
			throw new RuntimeException("Provide the parameters to search garbage bills.");
		}
		
	}

}
