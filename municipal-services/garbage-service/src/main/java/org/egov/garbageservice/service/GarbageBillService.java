package org.egov.garbageservice.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Collections;

import org.egov.common.contract.request.RequestInfo;
import org.egov.garbageservice.contract.bill.UpdateBillRequest;
import org.egov.garbageservice.model.AuditDetails;
import org.egov.garbageservice.model.GarbageBill;
import org.egov.garbageservice.model.GarbageBillRequest;
import org.egov.garbageservice.model.GarbageBillSearchCriteria;
import org.egov.garbageservice.model.GrbgBillTracker;
import org.egov.garbageservice.model.GrbgBillTrackerSearchCriteria;
import org.egov.garbageservice.model.SearchGarbageBillRequest;
import org.egov.garbageservice.repository.GarbageBillRepository;
import org.egov.garbageservice.repository.GarbageBillTrackerRepository;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.egov.garbageservice.contract.bill.Bill;
import org.egov.garbageservice.contract.bill.BillResponse;
import org.egov.garbageservice.contract.bill.BillSearchCriteria;
import org.egov.garbageservice.contract.bill.CancleBillRequest;
import org.egov.garbageservice.contract.bill.Demand;
import org.egov.garbageservice.contract.bill.Demand.StatusEnum;
import org.egov.garbageservice.contract.bill.UpdateBillCriteria;
import org.egov.garbageservice.util.GrbgUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.garbageservice.contract.bill.BillDetail;


/**
 * Service for local GarbageBill CRUD and cancellation aligned with billing-service bills.
 * Persists garbage bill rows, searches by criteria, and cancels remote bills via BillService
 * when CancleBillRequest is received from GarbageBillController.
 */
@Service
public class GarbageBillService {

	@Autowired
	private GarbageBillRepository repository;
	
	@Autowired
	private BillService billService;
	
	@Autowired
	private DemandService demandService;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private GrbgUtils grbgUtils;
	
	@Autowired
	private GarbageBillTrackerRepository trackerRepository;
	
	@Autowired
	private GarbageAccountService garbageAccountService;

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
	
	public Boolean cancelGarbageBill(CancleBillRequest cancleBillRequest) {

		String demandId = cancleBillRequest.getDemandId().iterator().next();

		GrbgBillTrackerSearchCriteria grbgBillTrackerSearchCriteria = GrbgBillTrackerSearchCriteria.builder()
				.demandIds(Collections.singleton(demandId))
				.status(new HashSet<>(Arrays.asList("ACTIVE")))
				.build();

		List<GrbgBillTracker> trackers = garbageAccountService.getBillCalculatedGarbageAccounts(grbgBillTrackerSearchCriteria);

		if (CollectionUtils.isEmpty(trackers)) {
			throw new CustomException("NO_TRACKER", "No active tracker found for the demand id");
		}

		GrbgBillTracker tracker = trackers.stream()
			    .filter(t -> t.getDemandId().equals(demandId))
			    .findFirst()
			    .orElseThrow(() -> new CustomException("NO_TRACKER", "Tracker not found"));

		if (trackers.size() > 1) {
			throw new CustomException("MULTI_TRACKER", "Multiple trackers for demand id");
		}

		BillSearchCriteria billSearchCriteria = BillSearchCriteria.builder()
				.billId(Collections.singleton(tracker.getBillId()))
				.tenantId(cancleBillRequest.getTenantId())
				.consumerCode(cancleBillRequest.getConsumerCode())
				.status(StatusEnum.ACTIVE)
				.build();
		BillResponse billResponse = billService.searchBill(billSearchCriteria, cancleBillRequest.getRequestInfo());

		if (CollectionUtils.isEmpty(billResponse.getBill())) {
			throw new CustomException("NO_BILL", "No bill found for the tracker");
		}

		Bill bill = billResponse.getBill().get(0);

		demandService.cancelDemand(bill.getTenantId(), Collections.singleton(tracker.getDemandId()), cancleBillRequest.getRequestInfo(), bill.getBusinessService());

		Map<String, Object> additionalDetails = null;
		if (bill.getAdditionalDetails() != null && !bill.getAdditionalDetails().isNull() && !bill.getAdditionalDetails().isEmpty()) {
			additionalDetails = (Map<String, Object>) bill.getAdditionalDetails();
		}
		if (additionalDetails == null) {
			additionalDetails = new HashMap<>();
		}
		additionalDetails.put("reasonMessage", cancleBillRequest.getReason());
		additionalDetails.put("reason", "BILL_GRBG_CANCEL");
		JsonNode additionalDetailsNode = objectMapper.valueToTree(additionalDetails);

		UpdateBillCriteria updateBillCriteria = UpdateBillCriteria.builder().tenantId(bill.getTenantId())
				.statusToBeUpdated(StatusEnum.CANCELLED)
				.consumerCodes(Collections.singleton(bill.getConsumerCode()))
				.billIds(Collections.singleton(bill.getId())) 
				.additionalDetails(additionalDetailsNode).businessService(bill.getBusinessService())
				.build();
		billService.cancelBill(updateBillCriteria, cancleBillRequest.getRequestInfo());

		AuditDetails audit = grbgUtils.buildCreateAuditDetails(cancleBillRequest.getRequestInfo());
		GrbgBillTracker grbgBillTracker = GrbgBillTracker.builder()
				.status("CANCELLED")
				.billId(bill.getId()).auditDetails(audit).build();
		trackerRepository.updateStatusBillTracker(grbgBillTracker);

		GrbgBillTracker previousTracker = getPreviousTracker(tracker);
		if (previousTracker != null) {
			BillSearchCriteria prevBillSearch = BillSearchCriteria.builder()
					.billId(Collections.singleton(previousTracker.getBillId()))
					.tenantId(cancleBillRequest.getTenantId())
					.status(StatusEnum.EXPIRED)
					.consumerCode(cancleBillRequest.getConsumerCode())
					.build();
			BillResponse prevBillResponse = billService.searchBill(prevBillSearch, cancleBillRequest.getRequestInfo());
			if (!CollectionUtils.isEmpty(prevBillResponse.getBill())) {
				Bill prevBill = prevBillResponse.getBill().get(0);
				prevBill.setStatus(Bill.StatusEnum.ACTIVE);
				
				long newExpiryDate = java.time.Instant.now()
				        .plus(30, java.time.temporal.ChronoUnit.DAYS)
				        .toEpochMilli();
				
				if (!CollectionUtils.isEmpty(prevBill.getBillDetails())) {
			        for (BillDetail detail : prevBill.getBillDetails()) {
			            detail.setExpiryDate(newExpiryDate);
			        }
			    }

				billService.updateBill(cancleBillRequest.getRequestInfo(), Collections.singletonList(prevBill));
			}
			
			GrbgBillTracker updatedPrevTracker = GrbgBillTracker.builder()
					.billId(previousTracker.getBillId())
		            .status("ACTIVE")
		            .auditDetails(grbgUtils.buildCreateAuditDetails(cancleBillRequest.getRequestInfo()))
		            .build();

			trackerRepository.activatePreviousTrackerByBillId(
			        previousTracker.getBillId(),
			        grbgUtils.buildCreateAuditDetails(cancleBillRequest.getRequestInfo())
			);
		}

		return true;
	}
	
	private GrbgBillTracker getPreviousTracker(GrbgBillTracker grbgBillTracker) {
		Set<String> type = new HashSet<>();
		type.add("MONTHLY");
		type.add("ARREAR");
		GrbgBillTrackerSearchCriteria grbgBillTrackerSearchCriteria = GrbgBillTrackerSearchCriteria.builder()
				.grbgApplicationIds(Collections.singleton(grbgBillTracker.getGrbgApplicationId()))
				.type(type)
				.build();

		List<GrbgBillTracker> trackers = garbageAccountService.getBillCalculatedGarbageAccounts(grbgBillTrackerSearchCriteria);
		trackers.sort((a, b) -> Long.compare(b.getAuditDetails().getCreatedDate(), a.getAuditDetails().getCreatedDate()));
		
		int index = -1;
		for (int i = 0; i < trackers.size(); i++) {
			if (trackers.get(i).getUuid().equals(grbgBillTracker.getUuid())) {
				index = i;
				break;
			}
		}
		
		if (index >= 0) {
		    for (int i = index + 1; i < trackers.size(); i++) {
		        GrbgBillTracker candidate = trackers.get(i);

		        if (!"CANCELLED".equalsIgnoreCase(candidate.getStatus())) {
		            return candidate;
		        }
		    }
		}
		return null;
	}
}