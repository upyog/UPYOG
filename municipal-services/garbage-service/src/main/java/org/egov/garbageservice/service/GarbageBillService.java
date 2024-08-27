package org.egov.garbageservice.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.garbageservice.model.AuditDetails;
import org.egov.garbageservice.model.GarbageBill;
import org.egov.garbageservice.model.GarbageBillRequest;
import org.egov.garbageservice.model.GarbageBillSearchCriteria;
import org.egov.garbageservice.model.SearchGarbageBillRequest;
import org.egov.garbageservice.repository.GarbageBillRepository;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class GarbageBillService {

	@Autowired
	private GarbageBillRepository repository;

	public List<GarbageBill> createGarbageBills(GarbageBillRequest garbageBillRequest) {

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

		if (null != requestInfo && null != requestInfo.getUserInfo()) {
			auditDetails = AuditDetails.builder().createdBy(requestInfo.getUserInfo().getUuid())
					.createdDate(new Date().getTime()).lastModifiedBy(requestInfo.getUserInfo().getUuid())
					.lastModifiedDate(new Date().getTime()).build();
			garbageBill.setAuditDetails(auditDetails);
		}

		// generate garbage_id
		garbageBill.setBillRefNo("GRBG_BILL_REF_" + Long.toString(System.currentTimeMillis()));
		garbageBill.setPaymentStatus("PENDING");
	}

	private void validateCreateGarbageBill(GarbageBill garbageBill) {
		if (null == garbageBill || null == garbageBill.getGarbageId() || null == garbageBill.getBillAmount()
				|| null == garbageBill.getTotalBillAmount() || null == garbageBill.getBillDueDate()
				|| null == garbageBill.getBillPeriod()) {
			throw new CustomException("MISSING_BILL_DETAILS","Provide garbage bill details.");
		}
	}

	public List<GarbageBill> updateGarbageBills(GarbageBillRequest garbageBillRequest) {

		List<GarbageBill> garbageBillsResponse = new ArrayList<>();
		GarbageBillSearchCriteria garbageBillSearchCriteria = createSearchCriteriaByGarbageBills(
				garbageBillRequest.getGarbageBills());
		Map<Long, GarbageBill> existingGarbageBillsMap = searchGarbageBillMap(garbageBillSearchCriteria,
				garbageBillRequest.getRequestInfo());

		if (!CollectionUtils.isEmpty(garbageBillRequest.getGarbageBills())) {
			garbageBillRequest.getGarbageBills().forEach(newGarbageBill -> {
				// search existing grbg bill
				GarbageBill existingGarbageBill = existingGarbageBillsMap.get(newGarbageBill.getId());

				// validate existing and new grbg bill
				validateUpdateGarbageBill(newGarbageBill, existingGarbageBill);

				// replicate existing grbg bill to history table

				// enrich new request
				enrichUpdateGarbageBill(newGarbageBill, existingGarbageBill, garbageBillRequest.getRequestInfo());

				// update garbage bill
				repository.update(newGarbageBill);
				garbageBillsResponse.add(newGarbageBill);
			});
		}

		return garbageBillsResponse;
	}

	private void enrichUpdateGarbageBill(GarbageBill newGarbageBill, GarbageBill existingGarbageBill,
			RequestInfo requestInfo) {

		AuditDetails auditDetails = null;
		if (null != requestInfo && null != requestInfo.getUserInfo()) {
			auditDetails = AuditDetails.builder().lastModifiedBy(requestInfo.getUserInfo().getUuid())
					.lastModifiedDate(new Date().getTime()).build();
		}
		if (null != existingGarbageBill.getAuditDetails()) {
			auditDetails.setCreatedBy(existingGarbageBill.getAuditDetails().getCreatedBy());
			auditDetails.setCreatedDate(existingGarbageBill.getAuditDetails().getCreatedDate());
		}

		newGarbageBill.setAuditDetails(auditDetails);
		newGarbageBill.setId(existingGarbageBill.getId());
		newGarbageBill.setGarbageId(existingGarbageBill.getGarbageId());
	}

	private void validateUpdateGarbageBill(GarbageBill newGarbageBill, GarbageBill existingGarbageBill) {
		if (null == existingGarbageBill) {
			throw new CustomException("GARBAGE_ACCOUNT_MISSING","Provided garbage account doesn't exist.");
		}
		// validate grbg acc req
		validateCreateGarbageBill(newGarbageBill);
	}

	private Map<Long, GarbageBill> searchGarbageBillMap(GarbageBillSearchCriteria garbageBillSearchCriteria,
			RequestInfo requestInfo) {

		SearchGarbageBillRequest searchGarbageBillRequest = SearchGarbageBillRequest.builder()
				.garbageBillSearchCriteria(garbageBillSearchCriteria).requestInfo(requestInfo).build();

		List<GarbageBill> garbageBills = searchGarbageBills(searchGarbageBillRequest);

		Map<Long, GarbageBill> existingGarbageBillMap = new HashMap<>();
		garbageBills.stream().forEach(bill -> {
			existingGarbageBillMap.put(bill.getId(), bill);
		});

		return existingGarbageBillMap;
	}

	private GarbageBillSearchCriteria createSearchCriteriaByGarbageBills(List<GarbageBill> garbageBills) {

		GarbageBillSearchCriteria searchCriteriaGarbageBill = GarbageBillSearchCriteria.builder().build();
		List<Long> ids = new ArrayList<>();
//		List<Long> garbageIds = new ArrayList<>();

		garbageBills.stream().forEach(grbgBill -> {
			if (null != grbgBill.getId() && 0 <= grbgBill.getId()) {
				ids.add(grbgBill.getId());
			}
//			if(null != grbgAcc.getGarbageId() && 0 <= grbgAcc.getGarbageId()) {
//				garbageIds.add(grbgAcc.getGarbageId());
//			}
		});

		if (!CollectionUtils.isEmpty(ids)) {
			searchCriteriaGarbageBill.setIds(ids);
		}
//		if (!CollectionUtils.isEmpty(garbageIds)) {
//			searchCriteriaGarbageAccount.setGarbageId(garbageIds);
//		}

		return searchCriteriaGarbageBill;
	}

	public List<GarbageBill> searchGarbageBills(SearchGarbageBillRequest searchGarbageBillRequest) {

		// validate search criteria
		validateGarbageBillSearchCriteria(searchGarbageBillRequest.getGarbageBillSearchCriteria());

		// search garbage account
		List<GarbageBill> garbageBills = repository
				.searchGarbageBills(searchGarbageBillRequest.getGarbageBillSearchCriteria());

		return garbageBills;
	}

	private void validateGarbageBillSearchCriteria(GarbageBillSearchCriteria garbageBillSearchCriteria) {

		if(CollectionUtils.isEmpty(garbageBillSearchCriteria.getIds()) &&
		        CollectionUtils.isEmpty(garbageBillSearchCriteria.getBillRefNos()) &&
		        CollectionUtils.isEmpty(garbageBillSearchCriteria.getGarbageIds()) &&
		        CollectionUtils.isEmpty(garbageBillSearchCriteria.getPaymentIds()) &&
		        CollectionUtils.isEmpty(garbageBillSearchCriteria.getPaymentStatus())) {
			throw new CustomException("MISSING_SEARCH_CRITERIA","Provide the parameters to search garbage bills.");
		}

	}

}
